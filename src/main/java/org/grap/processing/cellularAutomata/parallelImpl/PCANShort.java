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
package org.grap.processing.cellularAutomata.parallelImpl;

import org.grap.processing.cellularAutomata.cam.ICAShort;

class PCANShort implements Runnable {
	private int ncols;

	private PCAN can;

	private short[] rac0;

	private short[] rac1;

	private ICAShort ca;

	private int startIdx;

	private int endIdx;

	private int currentThreadIdx;

	PCANShort(final PCAN can, final int startIdx, final int endIdx,
			final int currentThreadIdx) {
		this.can = can;
		rac0 = (short[]) can.getRac0();
		rac1 = (short[]) can.getRac1();
		ca = (ICAShort) can.getCa();
		ncols = ca.getNCols();
		this.startIdx = startIdx;
		this.endIdx = endIdx;
		this.currentThreadIdx = currentThreadIdx;
	}

	public void run() {
		// initialize
		for (int i = startIdx; i < endIdx; i++) {
			final int r = i / ncols;
			final int c = i % ncols;
			rac0[i] = ca.init(r, c, i);
		}
		can.synchronization();

		// get stable state
		do {
			boolean modified = false;
			if (0 == (can.getIterationsCount() % 2)) {
				for (int i = startIdx; i < endIdx; i++) {
					final int r = i / ncols;
					final int c = i % ncols;
					rac1[i] = ca.localTransition(rac0, r, c, i);
					if (rac0[i] != rac1[i]) {
						modified = true;
					}
				}
			} else {
				for (int i = startIdx; i < endIdx; i++) {
					final int r = i / ncols;
					final int c = i % ncols;
					rac0[i] = ca.localTransition(rac1, r, c, i);
					if (rac0[i] != rac1[i]) {
						modified = true;
					}
				}
			}
			can.getBreakCondition().setModificationValue(modified,
					currentThreadIdx);
			can.synchronization();
		} while (can.getBreakCondition().doIContinue());
	}
}