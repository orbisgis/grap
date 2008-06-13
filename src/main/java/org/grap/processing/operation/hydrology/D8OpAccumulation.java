/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
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

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;

public class D8OpAccumulation extends D8OpAbstract implements Operation {
	public static final float noDataValue = Float.NaN;

	private ImagePlus gipDirection;
	private float[] d8Accumulation;
	private int ncols;
	private int nrows;

	@Override
	public GeoRaster evaluateResult(GeoRaster geoRaster)
			throws OperationException {
		try {
			gipDirection = geoRaster.getImagePlus();
			final RasterMetadata rasterMetadata = geoRaster.getMetadata();
			nrows = rasterMetadata.getNRows();
			ncols = rasterMetadata.getNCols();
			int nbOfOutlets = accumulateSlopes();
			final GeoRaster grAccumulation = GeoRasterFactory.createGeoRaster(
					d8Accumulation, rasterMetadata);
			grAccumulation.setNodataValue(noDataValue);
			System.out.printf("%d outlet(s)\n", nbOfOutlets);
			return grAccumulation;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private int accumulateSlopes() throws IOException {
		// slopes accumulations' array initialization
		d8Accumulation = new float[nrows * ncols];

		int nbOfOutlets = 0;
		int i = 0;

		for (int r = 0; r < nrows; r++) {
			for (int c = 0; c < ncols; c++, i++) {
				if ((0 == r) || (nrows == r + 1) || (0 == c)
						|| (ncols == c + 1)) {
					d8Accumulation[i] = noDataValue;
				} else if (Float.isNaN(gipDirection.getProcessor()
						.getPixelValue(c, r))) {
					d8Accumulation[i] = noDataValue;
				} else if (0 == d8Accumulation[i]) {
					// current cell value has not been yet modified...
					nbOfOutlets += findOutletAndAccumulateSlopes(i);
				}
				// print();
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

			if (Float.isNaN(gipDirection.getProcessor().getPixelValue(c, r))) {
				return isProbablyANewOutlet ? 1 : 0;
			} else {
				if (0 == d8Accumulation[curCellIdx]) {
					// current cell value has not been yet modified...
					d8Accumulation[curCellIdx] = 1 + acc;
					acc++;
				} else {
					// join an already identified river...
					if (isProbablyANewOutlet) {
						// junction point
						isProbablyANewOutlet = false;
					}
					d8Accumulation[curCellIdx] += acc;
				}
				curCellIdx = SlopesUtilities
						.fromCellSlopeDirectionToNextCellIndex(gipDirection,
								ncols, nrows, curCellIdx, c, r);
			}
		} while (null != curCellIdx);
		return isProbablyANewOutlet ? 1 : 0;
	}

	void print() {
		for (int r = 0; r < nrows; r++) {
			for (int c = 0; c < ncols; c++) {
				System.out.printf("%3.0f ", d8Accumulation[r * ncols + c]);
			}
			System.out.println();
		}
		System.out.println("= = = = ");
	}
}