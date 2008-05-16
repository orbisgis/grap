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
package org.grap.processing.operation.hydrology;

import java.io.IOException;

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.cellularAutomata.CASlope;
import org.grap.processing.cellularAutomata.cam.CANFactory;
import org.grap.processing.cellularAutomata.cam.ICA;
import org.grap.processing.cellularAutomata.cam.ICAN;
import org.grap.utilities.PixelUtilities;

public class D8OpSlope extends D8Operation implements Operation {
	private static final boolean PARALLEL = true;

	@Override
	public GeoRaster evaluateResult(GeoRaster geoRaster)
			throws OperationException, GeoreferencingException {
		if (PARALLEL) {
			return parallel(geoRaster);
		} else {
			return sequential(geoRaster);
		}
	}

	private GeoRaster sequential(final GeoRaster grDEM)
			throws OperationException, GeoreferencingException {
		try {
			final PixelUtilities pixelUtilities = new PixelUtilities(grDEM);
			final RasterMetadata rasterMetadata = grDEM.getMetadata();
			final int nrows = rasterMetadata.getNRows();
			final int ncols = rasterMetadata.getNCols();
			final float[] slopes = new float[nrows * ncols];
			int i = 0;
			for (int y = 0; y < nrows; y++) {
				for (int x = 0; x < ncols; x++, i++) {
					slopes[i] = pixelUtilities.getSlope(x, y);
				}
			}

			final GeoRaster grSlope = GeoRasterFactory.createGeoRaster(slopes,
					ncols, nrows, rasterMetadata);
			grSlope.setNodataValue(PixelUtilities.noDataValueForAngle);
			return grSlope;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private GeoRaster parallel(final GeoRaster grDEM)
			throws OperationException, GeoreferencingException {
		try {
			final PixelUtilities pixelUtilities = new PixelUtilities(grDEM);
			final RasterMetadata rasterMetadata = grDEM.getMetadata();
			final int nrows = rasterMetadata.getNRows();
			final int ncols = rasterMetadata.getNCols();

			final ICA ca = new CASlope(pixelUtilities, nrows, ncols);
			final ICAN ccan = CANFactory.createCAN(ca);
			ccan.getStableState();

			return GeoRasterFactory.createGeoRaster((float[]) ccan
					.getCANValues(), ncols, nrows, rasterMetadata);
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}
}