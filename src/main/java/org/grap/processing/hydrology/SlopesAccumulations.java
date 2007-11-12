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

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.GrapImagePlus;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;

public class SlopesAccumulations implements Operation {
	private GrapImagePlus gipSlopesDirections;
	private float[] slopesAccumulations;
	private int ncols;
	private int nrows;

	public GeoRaster execute(final GeoRaster grSlopesDirections)
			throws OperationException, GeoreferencingException {
		try {
			final long startTime = System.currentTimeMillis();
			gipSlopesDirections = grSlopesDirections.getGrapImagePlus();
			final RasterMetadata rasterMetadata = grSlopesDirections
					.getMetadata();
			nrows = rasterMetadata.getNRows();
			ncols = rasterMetadata.getNCols();
			int nbOfOutlets = accumulateSlopes();
			final GeoRaster grSlopesAccumulations = GeoRasterFactory
					.createGeoRaster(slopesAccumulations, ncols, nrows,
							rasterMetadata);
			grSlopesAccumulations.setNodataValue(Float.NaN);
			System.out.printf("Slopes accumulations in %d ms (%d outlet(s))\n",
					System.currentTimeMillis() - startTime, nbOfOutlets);
			return grSlopesAccumulations;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private int accumulateSlopes() throws IOException {
		// slopes accumulations' array initialization
		slopesAccumulations = new float[nrows * ncols];
		for (int i = 0; i < slopesAccumulations.length; i++) {
			slopesAccumulations[i] = -1;
		}

		int nbOfOutlets = 0;
		int i = 0;

		for (int r = 0; r < nrows; r++) {
			for (int c = 0; c < ncols; c++, i++) {
				if (Float.isNaN(gipSlopesDirections.getPixelValue(c, r))) {
					slopesAccumulations[i] = Float.NaN;
				} else if (-1 == slopesAccumulations[i]) {
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
		float acc = -1;

		do {
			final int r = curCellIdx / ncols;
			final int c = curCellIdx % ncols;

			if (Float.isNaN(gipSlopesDirections.getPixelValue(c, r))) {
				return isProbablyANewOutlet ? 1 : 0;
			} else {
				if (-1 == slopesAccumulations[curCellIdx]) {
					// current cell value has not been yet modified...
					slopesAccumulations[curCellIdx] = acc + 1;
					acc++;
				} else {
					// join an already identified river...
					if (isProbablyANewOutlet) {
						// junction point
						isProbablyANewOutlet = false;
					}
					slopesAccumulations[curCellIdx] += acc + 1;
				}
				curCellIdx = SlopesComputations
						.fromCellSlopeDirectionToNextCellIndex(
								gipSlopesDirections, ncols, nrows, curCellIdx,
								c, r);
			}
		} while (null != curCellIdx);
		return isProbablyANewOutlet ? 1 : 0;
	}
}