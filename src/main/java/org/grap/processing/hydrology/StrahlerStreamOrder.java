/*
 * The GRAP library (GeoRAster Processing) is a middleware dedicated
 * to the processing of various kinds of geographic raster data. It
 * provides a complete and robust API to manipulate ASCII Grid or
 * tiff, png, bmp, jpg (with the corresponding world file) geographic
 * images. GRAP is produced  by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GRAP.
 *
 * GRAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAP. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.grap.processing.hydrology;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.PixelProvider;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;

public class StrahlerStreamOrder implements Operation {
	public final static short noDataValue = (short) Float.NaN;
	public final static short riversStartValue = Short.MAX_VALUE;

	private PixelProvider ppSlopesDirections;
	private PixelProvider ppSlopesAccumulations;
	private short[] strahlerStreamOrder;
	private int riverThreshold;
	private int ncols;
	private int nrows;

	public StrahlerStreamOrder(final GeoRaster grSlopesAccumulations,
			final int riverThreshold) throws OperationException {
		try {
			ppSlopesAccumulations = grSlopesAccumulations.getPixelProvider();
		} catch (IOException e) {
			throw new OperationException(e);
		}
		this.riverThreshold = riverThreshold;
	}

	public GeoRaster execute(final GeoRaster grSlopesDirections)
			throws OperationException {
		try {
			final long startTime = System.currentTimeMillis();
			ppSlopesDirections = grSlopesDirections.getPixelProvider();
			final RasterMetadata rasterMetadata = grSlopesDirections
					.getMetadata();
			nrows = rasterMetadata.getNRows();
			ncols = rasterMetadata.getNCols();
			int maxStrahlerStreamOrder = computeStrahlerStreamOrders();
			final GeoRaster grStrahlerStreamOrder = GeoRasterFactory
					.createGeoRaster(strahlerStreamOrder, ncols, nrows,
							rasterMetadata);
			grStrahlerStreamOrder.setNodataValue(noDataValue);
			System.out
					.printf(
							"Strahler stream order (max value = %d, river threshold = %d) in %d ms\n",
							maxStrahlerStreamOrder, riverThreshold, System
									.currentTimeMillis()
									- startTime);
			return grStrahlerStreamOrder;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private int computeStrahlerStreamOrders() throws IOException {
		short maxStrahlerStreamOrder = 1;
		strahlerStreamOrder = new short[nrows * ncols];
		Set<Integer> junctionsStack = new HashSet<Integer>();

		// 1st step: identify all the rivers' starts...
		int i = 0;
		for (int r = 0; r < nrows; r++) {
			for (int c = 0; c < ncols; c++, i++) {
				if (SlopesComputations.isARiverStart(ppSlopesAccumulations,
						ppSlopesDirections, riverThreshold, ncols, nrows, i)) {
					strahlerStreamOrder[i] = riversStartValue;
					junctionsStack.add(i);
				} else {
					strahlerStreamOrder[i] = noDataValue;
				}
			}
		}

		// 2nd step:
		short step = 1;
		do {
			System.out.printf("%d junctions in step number %d\n",
					junctionsStack.size(), step);
			final Set<Integer> nextJunctionsStack = new HashSet<Integer>();
			for (int riverStart : junctionsStack) {
				// final Short colorTag = step;
				final Short colorTag = getStrahlerStreamOrderTag(riverStart);
				if (null != colorTag) {
					maxStrahlerStreamOrder = max(maxStrahlerStreamOrder,
							colorTag);
					// towards the next junction...
					tagUntilNextJunction(riverStart, colorTag,
							nextJunctionsStack);
				}
			}
			junctionsStack = nextJunctionsStack;
			step++;
		} while (0 < junctionsStack.size());

		return maxStrahlerStreamOrder;
	}

	private short max(final short a, final short b) {
		return (a > b) ? a : b;
	}

	private void tagUntilNextJunction(final int startIdx, final short colorTag,
			final Set<Integer> nextJunctionsStack) throws IOException {
		Integer idx = startIdx;
		int rIdx = idx / ncols;
		int cIdx = idx % ncols;

		do {
			strahlerStreamOrder[idx] = colorTag;
			final Integer next = nextCellIsARiversJunction(idx, cIdx, rIdx,
					nextJunctionsStack);
			if (null == next) {
				// new rivers junction
				idx = null;
			} else {
				idx = next;
				rIdx = idx / ncols;
				cIdx = idx % ncols;
			}
		} while (null != idx);
	}

	private Integer nextCellIsARiversJunction(final int idx, final int cIdx,
			final int rIdx, final Set<Integer> nextJunctionsStack)
			throws IOException {
		final Integer next = SlopesComputations
				.fromCellSlopeDirectionToNextCellIndex(ppSlopesDirections,
						ncols, nrows, idx, cIdx, rIdx);
		if (null != next) {
			final Set<Integer> contributiveArea = SlopesComputations
					.fromCellSlopeDirectionIdxToContributiveArea(
							ppSlopesDirections, ncols, nrows, next);
			contributiveArea.remove(idx);
			for (int contributor : contributiveArea) {
				final int rContributor = contributor / ncols;
				final int cContributor = contributor % ncols;
				if (riverThreshold <= ppSlopesAccumulations.getPixel(
						cContributor, rContributor)) {
					// next cell is a junction cell
					nextJunctionsStack.add(next);
					return null;
				}
			}
		}
		return next;
	}

	private Short getStrahlerStreamOrderTag(final int idx) throws IOException {
		if (riversStartValue == strahlerStreamOrder[idx]) {
			return 1;
		} else {
			final Set<Integer> contributiveArea = SlopesComputations
					.fromCellSlopeDirectionIdxToContributiveArea(
							ppSlopesDirections, ncols, nrows, idx);
			final SortedMap<Short, Short> tm = new TreeMap<Short, Short>();
			for (int contributor : contributiveArea) {
				final int rContributor = contributor / ncols;
				final int cContributor = contributor % ncols;
				if (riverThreshold <= ppSlopesAccumulations.getPixel(
						cContributor, rContributor)) {
					final short sso = strahlerStreamOrder[contributor];
					if (tm.containsKey(sso)) {
						tm.put(sso, (short) (tm.get(sso) + 1));
					} else {
						tm.put(sso, (short) 1);
					}
				}
			}
			if (noDataValue == tm.firstKey()) {
				// the Strahler stream order of at least one contributor has not
				// been yet calculated... do not do anything !
				return null;
			} else if (1 == tm.get(tm.lastKey())) {
				// the Strahler stream order of the junction branch is equal to
				// the unique greatest Strahler stream order of their
				// contributors
				return tm.lastKey();
			} else {
				return (short) (tm.lastKey() + 1);
			}
		}
	}
}