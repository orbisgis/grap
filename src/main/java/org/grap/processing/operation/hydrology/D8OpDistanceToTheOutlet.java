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

import ij.ImagePlus;

import java.io.IOException;
import java.util.Stack;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;

public class D8OpDistanceToTheOutlet extends D8OpAbstract implements Operation {
	public static final float noDataValue = Float.NaN;

	private ImagePlus ipDirection;
	private float[] d8Distances;
	private int ncols;
	private int nrows;

	@Override
	public GeoRaster evaluateResult(GeoRaster geoRaster)
			throws OperationException {
		try {
			ipDirection = geoRaster.getImagePlus();
			final RasterMetadata rasterMetadata = geoRaster.getMetadata();
			nrows = rasterMetadata.getNRows();
			ncols = rasterMetadata.getNCols();
			calculateDistances();
			final GeoRaster grDistancesToTheOutlet = GeoRasterFactory
					.createGeoRaster(d8Distances, rasterMetadata);
			grDistancesToTheOutlet.setNodataValue(noDataValue);
			return grDistancesToTheOutlet;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private void calculateDistances() throws IOException {
		// distances' array initialization
		d8Distances = new float[nrows * ncols];

		int i = 0;

		for (int r = 0; r < nrows; r++) {
			for (int c = 0; c < ncols; c++, i++) {
				if ((0 == r)
						|| (nrows == r + 1)
						|| (0 == c)
						|| (ncols == c + 1)
						|| Float.isNaN(ipDirection.getProcessor()
								.getPixelValue(c, r))) {
					d8Distances[i] = noDataValue;
				} else if (0 == d8Distances[i]) {
					// current cell value has not been yet modified...
					findOutletAndCalculateDistances(i);
				}
				// print();
			}
		}
	}

	private void findOutletAndCalculateDistances(final int i)
			throws IOException {
		final Stack<Integer> stack = new Stack<Integer>();
		Integer curCellIdx = i;
		boolean goOn = true;
		int beginning = 0;

		stack.add(curCellIdx);
		do {
			final Integer nextCellIdx = SlopesUtilities
					.fromCellSlopeDirectionToNextCellIndex(ipDirection, ncols,
							nrows, curCellIdx);
			if ((null != nextCellIdx) && (0 == d8Distances[nextCellIdx])) {
				stack.add(nextCellIdx);
				curCellIdx = nextCellIdx;
			} else {
				goOn = false;
				beginning = (null == nextCellIdx) ? -1
						: (int) d8Distances[nextCellIdx];
			}
		} while (goOn);

		while (!stack.empty()) {
			d8Distances[stack.pop()] = ++beginning;
		}
	}

	void print() {
		for (int r = 0; r < nrows; r++) {
			for (int c = 0; c < ncols; c++) {
				System.out.printf("%3.0f ", d8Distances[r * ncols + c]);
			}
			System.out.println();
		}
		System.out.println("= = = = ");
	}
}