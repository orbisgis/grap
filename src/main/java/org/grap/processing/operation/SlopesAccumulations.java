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
package org.grap.processing.operation;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.PixelProvider;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;

public class SlopesAccumulations implements Operation {
	private PixelProvider ppSlopesDirections;
	private float[] slopesAccumulation;
	private int ncols;
	private int nrows;

	public GeoRaster execute(final GeoRaster grSlopesDirections)
			throws OperationException {
		try {
			final long startTime = System.currentTimeMillis();
			ppSlopesDirections = grSlopesDirections.getPixelProvider();
			final RasterMetadata rasterMetadata = grSlopesDirections
					.getMetadata();
			nrows = rasterMetadata.getNRows();
			ncols = rasterMetadata.getNCols();
			int nbOfOutlets = accumulateSlopes();
			System.out.printf("%d outlets in %d ms\n", nbOfOutlets, System
					.currentTimeMillis()
					- startTime);

			return GeoRasterFactory.createGeoRaster(slopesAccumulation, ncols,
					nrows, grSlopesDirections.getMetadata());
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private int accumulateSlopes() throws IOException {
		slopesAccumulation = new float[nrows * ncols];
		int nbOfOutlets = 0;
		int i = 0;

		for (int r = 0; r < nrows; r++) {
			for (int c = 0; c < ncols; c++, i++) {
				if (Float.isNaN(ppSlopesDirections.getPixel(c, r))) {
					slopesAccumulation[i] = Float.NaN;
				} else if (0 == slopesAccumulation[i]) {
					// current cell value has not been yet modified...
					nbOfOutlets += findOutletAndAccumulateSlopes(i);
				}
			}
		}
		return nbOfOutlets;
	}

	private int findOutletAndAccumulateSlopes(final int i) throws IOException {
		boolean isProbablyANewOutlet = true;
		Integer curCellIdx = i;
		float acc = 0;

		do {
			final int r = curCellIdx / ncols;
			final int c = curCellIdx % ncols;

			if (Float.isNaN(ppSlopesDirections.getPixel(c, r))) {
				return isProbablyANewOutlet ? 1 : 0;
			} else {
				if (0 == slopesAccumulation[curCellIdx]) {
					slopesAccumulation[curCellIdx] = acc + 1;
					acc = slopesAccumulation[curCellIdx];
				} else {
					// join an already identified river...
					if (isProbablyANewOutlet) {
						// junction point
						isProbablyANewOutlet = false;
						slopesAccumulation[curCellIdx] += acc;
						acc = slopesAccumulation[curCellIdx];
					} else {
						slopesAccumulation[curCellIdx] = acc + 1;
						acc = slopesAccumulation[curCellIdx];
					}
				}
				curCellIdx = fromCellSlopeDirectionToNextCellIndex(curCellIdx);
			}
		} while (null != curCellIdx);
		return isProbablyANewOutlet ? 1 : 0;
	}

	private Integer fromCellSlopeDirectionToNextCellIndex(final int i)
			throws IOException {
		final int r = i / ncols;
		final int c = i % ncols;
		switch ((short) ppSlopesDirections.getPixel(c, r)) {
		case 1:
			return getCellIndex(i + 1);
		case 2:
			return getCellIndex(i + ncols + 1);
		case 4:
			return getCellIndex(i + ncols);
		case 8:
			return getCellIndex(i + ncols - 1);
		case 16:
			return getCellIndex(i - 1);
		case 32:
			return getCellIndex(i - ncols - 1);
		case 64:
			return getCellIndex(i - ncols);
		case 128:
			return getCellIndex(i - ncols + 1);
		}
		return null;
	}

	private Integer getCellIndex(final int i) {
		final int r = i / ncols;
		final int c = i % ncols;
		return ((0 > r) || (nrows <= r) || (0 > c) || (ncols <= c)) ? null : i;
	}
}