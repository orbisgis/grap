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

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.GrapImagePlus;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;

public class GridDirection implements Operation {
	public final static short noDataValue = 0;
	public final static short indecision = -1;
	private final static float SQRT2 = (float) Math.sqrt(2d);
	private GrapImagePlus ppDEM;
	private short[] slopesDirections;
	private int ncols;
	private int nrows;

	public GeoRaster execute(final GeoRaster grDEM) throws OperationException,
			GeoreferencingException {
		try {
			final long startTime = System.currentTimeMillis();

			if ((ImagePlus.GRAY16 != grDEM.getType())
					&& (ImagePlus.GRAY32 != grDEM.getType())) {
				throw new OperationException(
						"The DEM must be a GRAY16 or a GRAY32 image !");
			}

			ppDEM = grDEM.getGrapImagePlus();
			final RasterMetadata rasterMetadata = grDEM.getMetadata();
			nrows = rasterMetadata.getNRows();
			ncols = rasterMetadata.getNCols();
			computeSlopesDirections();
			final GeoRaster grSlopesDirections = GeoRasterFactory
					.createGeoRaster(slopesDirections, ncols, nrows,
							rasterMetadata);
			grSlopesDirections.setNodataValue(noDataValue);
			System.out.printf("Slopes directions in %d ms\n", System
					.currentTimeMillis()
					- startTime);
			return grSlopesDirections;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private void computeSlopesDirections() throws IOException {
		slopesDirections = new short[nrows * ncols];
		int i = 0;

		for (int r = 0; r < nrows; r++) {
			for (int c = 0; c < ncols; c++, i++) {
				final float currentElevation = ppDEM.getPixelValue(c, r);

				if (Float.isNaN(currentElevation)) {
					slopesDirections[i] = noDataValue;
				} else {
					final float[] tmpSlopes = new float[] {
							currentElevation - getDEMValue(r, c + 1),
							(currentElevation - getDEMValue(r + 1, c + 1))
									/ SQRT2,
							currentElevation - getDEMValue(r + 1, c),
							(currentElevation - getDEMValue(r + 1, c - 1))
									/ SQRT2,
							currentElevation - getDEMValue(r, c - 1),
							(currentElevation - getDEMValue(r - 1, c - 1))
									/ SQRT2,
							currentElevation - getDEMValue(r - 1, c),
							(currentElevation - getDEMValue(r - 1, c + 1))
									/ SQRT2 };
					final int idx = getIdxForMaxValue(tmpSlopes);
					slopesDirections[i] = (-1 == idx) ? indecision
							: (short) (1 << idx);
				}
			}
		}
	}

	private static int getIdxForMaxValue(final float[] values) {
		float max = 0;
		int result = -1;
		for (int i = 0; i < values.length; i++) {
			if ((!Float.isNaN(values[i])) && (values[i] > max)) {
				result = i;
				max = values[i];
			}
		}
		return result;
	}

	private float getDEMValue(final int r, final int c) throws IOException {
		return ((0 > r) || (nrows <= r) || (0 > c) || (ncols <= c)) ? Float.NaN
				: ppDEM.getPixelValue(c, r);
	}
}