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
package org.grap.processing.cellularAutomata.useless;

import org.grap.processing.cellularAutomata.cam.ICAShort;

public class CASlopesDirections implements ICAShort {
	public final static short noDataValue = 0;

	public final static short indecision = 255;

	private int nrows;

	private int ncols;

	private float[] DEM;

	private final static float SQRT2 = (float) Math.sqrt(2d);

	public CASlopesDirections(final float[] DEM, final int nrows,
			final int ncols) {
		this.nrows = nrows;
		this.ncols = ncols;
		this.DEM = DEM;
	}

	public int getNCols() {
		return ncols;
	}

	public int getNRows() {
		return nrows;
	}

	public short init(int r, int c, int i) {
		final float currentElevation = DEM[i];

		if (Float.isNaN(currentElevation)) {
			return noDataValue;
		} else {
			final float[] tmpSlopes = new float[] {
					currentElevation - getDEMValue(r, c + 1, i + 1),
					(currentElevation - getDEMValue(r + 1, c + 1, i + ncols + 1))
							/ SQRT2,
					currentElevation - getDEMValue(r + 1, c, i + ncols),
					(currentElevation - getDEMValue(r + 1, c - 1, i + ncols - 1))
							/ SQRT2,
					currentElevation - getDEMValue(r, c - 1, i - 1),
					(currentElevation - getDEMValue(r - 1, c - 1, i - ncols - 1))
							/ SQRT2,
					currentElevation - getDEMValue(r - 1, c, i - ncols),
					(currentElevation - getDEMValue(r - 1, c + 1, i - ncols + 1))
							/ SQRT2 };

			final int idx = getIdxForMaxValue(tmpSlopes);
			if (-1 == idx) {
				return indecision;
			} else {
				return (short) (1 << idx);
			}
		}
	}

	public short localTransition(short[] rac, int r, int c, int i) {
		/* remain unchanged : one step of computation (init) */
		return rac[i];
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

	private float getDEMValue(final int r, final int c, final int i) {
		return ((0 > r) || (nrows <= r) || (0 > c) || (ncols <= c)) ? Float.NaN
				: DEM[i];
	}
}