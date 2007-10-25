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
package org.grap.processing.operation.manual;

import org.grap.processing.cellularAutomata.CASlopesAccumulation;
import org.grap.processing.cellularAutomata.CASlopesDirections;
import org.grap.processing.cellularAutomata.CASlopesInPercent;
import org.grap.processing.cellularAutomata.cam.ICA;
import org.grap.processing.cellularAutomata.cam.ICAN;
import org.grap.processing.cellularAutomata.parallelImpl.PCAN;
import org.grap.processing.cellularAutomata.useless.CAGetAllSubWatershed;

public class MainPar {
	public static void main(String[] args) {
		final float[] DEM = new float[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, };
		final int nrows = 5;
		final int ncols = 5;

		final ICA ca1 = new CASlopesDirections(DEM, nrows, ncols);
		final ICAN ccan1 = new PCAN(ca1);
		ccan1.getStableState();
		ccan1.print(ca1.getClass().getSimpleName());

		final ICA ca2 = new CASlopesInPercent(DEM, nrows, ncols);
		final ICAN ccan2 = new PCAN(ca2);
		ccan2.getStableState();
		ccan2.print(ca2.getClass().getSimpleName());

		final short[] slopesDirections = (short[]) ccan1.getCANValues();

		final ICA ca3 = new CASlopesAccumulation(slopesDirections, nrows, ncols);
		final ICAN ccan3 = new PCAN(ca3);
		ccan3.getStableState();
		ccan3.print(ca3.getClass().getSimpleName());

		final ICA ca4 = new CAGetAllSubWatershed(slopesDirections, nrows, ncols);
		final ICAN ccan4 = new PCAN(ca4);
		ccan4.getStableState();
		ccan4.print(ca4.getClass().getSimpleName());
	}
}