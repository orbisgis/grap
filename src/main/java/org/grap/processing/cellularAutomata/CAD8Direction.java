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
package org.grap.processing.cellularAutomata;

import org.grap.processing.cellularAutomata.cam.ICAFloat;
import org.grap.processing.operation.hydrology.HydrologyUtilities;

public class CAD8Direction implements ICAFloat {
	private int nrows;
	private int ncols;
	private HydrologyUtilities pixelUtilities;

	public CAD8Direction(final HydrologyUtilities pixelUtilities, final int nrows,
			final int ncols) {
		this.nrows = nrows;
		this.ncols = ncols;
		this.pixelUtilities = pixelUtilities;
	}

	public int getNCols() {
		return ncols;
	}

	public int getNRows() {
		return nrows;
	}

	public float init(int r, int c, int i) {
		return pixelUtilities.getD8Direction(c, r);
	}

	public float localTransition(float[] rac, int r, int c, int i) {
		/* remain unchanged : one step of computation (init) */
		return rac[i];
	}
}