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

import org.grap.model.GeoRaster;
import org.grap.processing.OperationException;
import org.orbisgis.progress.ProgressMonitor;

public abstract class D8OpAbstractMultiThreads extends D8OpAbstract {
	private static final boolean PARALLEL = false;

	@Override
	public GeoRaster evaluateResult(GeoRaster geoRaster, ProgressMonitor pm)
			throws OperationException {
		if (PARALLEL) {
			return parallel(geoRaster);
		} else {
			return sequential(geoRaster, pm);
		}
	}

	abstract GeoRaster sequential(GeoRaster geoRaster, ProgressMonitor pm)
			throws OperationException;

	abstract GeoRaster parallel(GeoRaster geoRaster) throws OperationException;
}
