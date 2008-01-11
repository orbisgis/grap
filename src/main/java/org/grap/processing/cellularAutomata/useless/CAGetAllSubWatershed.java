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
package org.grap.processing.cellularAutomata.useless;

import org.grap.processing.cellularAutomata.cam.ICAFloat;

public class CAGetAllSubWatershed implements ICAFloat {
	private int nrows;

	private int ncols;

	private short[] slopesDirections;

	public CAGetAllSubWatershed(final short[] slopesDirections,
			final int nrows, final int ncols) {
		this.nrows = nrows;
		this.ncols = ncols;
		this.slopesDirections = slopesDirections;
	}

	public int getNCols() {
		return ncols;
	}

	public int getNRows() {
		return nrows;
	}

	public float init(final int r, final int c, final int i) {
		return i;
	}

	public float localTransition(final float[] rac, final int r, final int c,
			final int i) {
		switch (getSlopeDirection(r, c, i)) {
		case 1:
			return getRacValue(rac[i], rac, r, c + 1, i + 1);
		case 2:
			return getRacValue(rac[i], rac, r + 1, c + 1, i + ncols + 1);
		case 4:
			return getRacValue(rac[i], rac, r + 1, c, i + ncols);
		case 8:
			return getRacValue(rac[i], rac, r + 1, c - 1, i + ncols - 1);
		case 16:
			return getRacValue(rac[i], rac, r, c - 1, i - 1);
		case 32:
			return getRacValue(rac[i], rac, r - 1, c - 1, i - ncols - 1);
		case 64:
			return getRacValue(rac[i], rac, r - 1, c, i - ncols);
		case 128:
			return getRacValue(rac[i], rac, r - 1, c + 1, i - ncols + 1);
		}
		return -1;
	}

	private int getSlopeDirection(final int r, final int c, final int i) {
		return ((0 > r) || (nrows <= r) || (0 > c) || (ncols <= c)) ? -1
				: slopesDirections[i];
	}

	private float getRacValue(final float currentValue, final float[] rac,
			final int r, final int c, final int i) {
		if ((0 > r) || (nrows <= r) || (0 > c) || (ncols <= c)) {
			return Float.NaN;
		} else if (Float.isNaN(rac[i]) || (-1 == rac[i])) {
			return currentValue;
		} else {
			return rac[i];
		}
	}
}