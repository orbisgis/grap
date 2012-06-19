/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.grap.processing.cellularAutomata.parallelImpl;

public class BreakCondition implements Runnable {
	private boolean[] collectOfModificationsValues;

	private boolean globalStop;

	public BreakCondition(final int numberOfThreads) {
		collectOfModificationsValues = new boolean[numberOfThreads];
	}

	public void setModificationValue(final boolean modified,
			final int currentThreadIdx) {
		collectOfModificationsValues[currentThreadIdx] = modified;
	}

	public boolean doIContinue() {
		return globalStop;
	}

	public synchronized void run() {
		globalStop = false;
		for (boolean modified : collectOfModificationsValues) {
			if (modified) {
				globalStop = true;
				break;
			}
		}
	}
}