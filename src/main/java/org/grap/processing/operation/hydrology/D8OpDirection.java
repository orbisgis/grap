/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.grap.processing.operation.hydrology;

import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.cellularAutomata.CAD8Direction;
import org.grap.processing.cellularAutomata.cam.CANFactory;
import org.grap.processing.cellularAutomata.cam.ICA;
import org.grap.processing.cellularAutomata.cam.ICAN;
import org.orbisgis.progress.ProgressMonitor;

public class D8OpDirection extends D8OpAbstractMultiThreads implements
		Operation {
	GeoRaster sequential(final GeoRaster grDEM, ProgressMonitor pm)
			throws OperationException {
		try {
			final HydrologyUtilities hydrologyUtilities = new HydrologyUtilities(
					grDEM);
			final RasterMetadata rasterMetadata = grDEM.getMetadata();
			final int nrows = rasterMetadata.getNRows();
			final int ncols = rasterMetadata.getNCols();
			final float[] slopesDirections = new float[nrows * ncols];
			int i = 0;
			for (int y = 0; y < nrows; y++) {

				if (y / 100 == y / 100.0) {
					if (pm.isCancelled()) {
						break;
					} else {
						pm.progressTo((int) (100 * y / nrows));
					}
				}

				for (int x = 0; x < ncols; x++, i++) {
					slopesDirections[i] = hydrologyUtilities.getD8Direction(x,
							y);
				}
			}

			final GeoRaster grSlopesDirections = GeoRasterFactory
					.createGeoRaster(slopesDirections, rasterMetadata);
			grSlopesDirections.setNodataValue(hydrologyUtilities.ndv);
			return grSlopesDirections;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	GeoRaster parallel(final GeoRaster grDEM) throws OperationException {
		try {
			final HydrologyUtilities hydrologyUtilities = new HydrologyUtilities(
					grDEM);
			final RasterMetadata rasterMetadata = grDEM.getMetadata();
			final int nrows = rasterMetadata.getNRows();
			final int ncols = rasterMetadata.getNCols();

			final ICA ca = new CAD8Direction(hydrologyUtilities, nrows, ncols);
			final ICAN ccan = CANFactory.createCAN(ca);
			ccan.getStableState();

			final GeoRaster grSlopesDirections = GeoRasterFactory
					.createGeoRaster((float[]) ccan.getCANValues(),
							rasterMetadata);
			grSlopesDirections.setNodataValue(hydrologyUtilities.ndv);
			return grSlopesDirections;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}
}
