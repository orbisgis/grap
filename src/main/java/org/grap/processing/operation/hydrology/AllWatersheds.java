/*
 * The GRAP library (GeoRAster Processing) is a middleware dedicated
 * to the processing of various kinds of geographic raster data. It
 * provides a complete and robust API to manipulate ASCII Grid or
 * tiff, png, bmp, jpg (with the corresponding world file) geographic
 * images. GRAP is produced  by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
package org.grap.processing.operation.hydrology;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.GrapImagePlus;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.IProgressMonitor;

public class AllWatersheds implements Operation {
	private GrapImagePlus gipSlopesDirections;
	private float[] watersheds;
	private int ncols;
	private int nrows;

	public GeoRaster execute(final GeoRaster grSlopesDirections, IProgressMonitor pm)
			throws OperationException {
		try {
			final long startTime = System.currentTimeMillis();
			gipSlopesDirections = grSlopesDirections.getGrapImagePlus();
			final RasterMetadata rasterMetadata = grSlopesDirections
					.getMetadata();
			nrows = rasterMetadata.getNRows();
			ncols = rasterMetadata.getNCols();
			int nbOfWatersheds = computeAllWatersheds();
			final GeoRaster grAllWatersheds = GeoRasterFactory.createGeoRaster(
					watersheds, ncols, nrows, rasterMetadata);
			grAllWatersheds.setNodataValue(Float.NaN);
			System.out.printf("%d watersheds in %d ms\n", nbOfWatersheds,
					System.currentTimeMillis() - startTime);
			return grAllWatersheds;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private int computeAllWatersheds() throws IOException {
		watersheds = new float[nrows * ncols];
		float newDefaultColor = 1;

		int i = 0;
		for (int r = 0; r < nrows; r++) {
			for (int c = 0; c < ncols; c++, i++) {
				if (Float.isNaN(gipSlopesDirections.getPixelValue(c, r))) {
					watersheds[i] = Float.NaN;
				} else if (0 == watersheds[i]) {
					// current cell value has not been yet modified...
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
		}
		return (int) (newDefaultColor - 1);
	}

	private Float findOutlet(final int i, final List<Integer> pathStack)
			throws IOException {
		Integer curCellIdx = i;
		do {
			final int r = curCellIdx / ncols;
			final int c = curCellIdx % ncols;

			if (Float.isNaN(gipSlopesDirections.getPixelValue(c, r))) {
				return null;
			} else {
				if (0 == watersheds[curCellIdx]) {
					pathStack.add(curCellIdx);
					curCellIdx = SlopesUtilities
							.fromCellSlopeDirectionToNextCellIndex(
									gipSlopesDirections, ncols, nrows,
									curCellIdx, c, r);
				} else {
					// current watershed's cell as already been modified : it is
					// a break condition !
					return watersheds[curCellIdx];
				}
			}
		} while (null != curCellIdx);
		return null;
	}

	private void colourize(final List<Integer> pathStack, final float color) {
		for (Integer cellItem : pathStack) {
			watersheds[cellItem] = color;
		}
	}
}