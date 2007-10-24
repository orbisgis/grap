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

import ij.ImagePlus;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;

public class EfficientAllWatersheds implements Operation {
	private short[] slopesDirections;
	private float[] watersheds;
	private int ncols;
	private int nrows;

	public GeoRaster execute(final GeoRaster geoRaster)
			throws OperationException {
		try {
			final long startTime = System.currentTimeMillis();
			final RasterMetadata rasterMetadata = geoRaster.getMetadata();
			nrows = rasterMetadata.getNRows();
			ncols = rasterMetadata.getNCols();

			if (ImagePlus.GRAY8 == geoRaster.getType()) {
				slopesDirections = (short[]) geoRaster.getPixelProvider()
						.getShortPixels();
			} else if (ImagePlus.GRAY16 == geoRaster.getType()) {
				slopesDirections = (short[]) geoRaster.getPixelProvider()
						.getPixels();
			} else {
				throw new RuntimeException(
						"The DEM directions must be a GRAY16 or a GRAY32 image !");
			}
			int nbOfWatersheds = computeAllWatersheds();
			System.out.printf("%d watersheds in %d ms\n", nbOfWatersheds,
					System.currentTimeMillis() - startTime);
			return GeoRasterFactory.createGeoRaster(watersheds, ncols, nrows,
					rasterMetadata);
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private int computeAllWatersheds() {
		watersheds = new float[slopesDirections.length];
		float newDefaultColor = 1;

		for (int i = 0; i < watersheds.length; i++) {
			if (0f == watersheds[i]) {
				// current cell value has not been modified...
				Float color = null;
				final List<Integer> pathStack = new LinkedList<Integer>();
				color = findOutlet(i, pathStack);
				if (null == color) {
					color = newDefaultColor;
					newDefaultColor++;
				}
				colourize(pathStack, color);
			}
		}
		return (int) (newDefaultColor - 1);
	}

	private Float findOutlet(final int i, final List<Integer> pathStack) {
		Integer curCellIdx = i;
		do {
			if (0f == watersheds[curCellIdx]) {
				pathStack.add(curCellIdx);
				curCellIdx = fromCellSlopeDirectionToNextCellIndex(curCellIdx);
			} else {
				// current watershed's cell as already been modified : it is a
				// break condition !
				return watersheds[curCellIdx];
			}
		} while (null != curCellIdx);
		return null;
	}

	private void colourize(final List<Integer> pathStack, final float color) {
		for (Integer cellItem : pathStack) {
			watersheds[cellItem] = color;
		}
	}

	private Integer fromCellSlopeDirectionToNextCellIndex(final int i) {
		switch (slopesDirections[i]) {
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