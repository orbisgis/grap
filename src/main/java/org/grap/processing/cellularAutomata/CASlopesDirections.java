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
package org.grap.processing.cellularAutomata;

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