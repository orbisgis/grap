package org.grap.processing.operation.manual;

import org.grap.processing.cellularAutomata.CAGetAllSubWatershed;
import org.grap.processing.cellularAutomata.CASlopesAccumulation;
import org.grap.processing.cellularAutomata.CASlopesDirections;
import org.grap.processing.cellularAutomata.CASlopesInPercent;
import org.grap.processing.cellularAutomata.cam.ICA;
import org.grap.processing.cellularAutomata.cam.ICAN;
import org.grap.processing.cellularAutomata.seqImpl.SCAN;

public class MainSeq {
	public static void main(String[] args) {
		final float[] DEM = new float[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, };
		final int nrows = 5;
		final int ncols = 5;

		final ICA ca1 = new CASlopesDirections(DEM, nrows, ncols);
		final ICAN ccan1 = new SCAN(ca1);
		ccan1.getStableState();
		ccan1.print(ca1.getClass().getSimpleName());

		final ICA ca2 = new CASlopesInPercent(DEM, nrows, ncols);
		final ICAN ccan2 = new SCAN(ca2);
		ccan2.getStableState();
		ccan2.print(ca2.getClass().getSimpleName());

		final short[] slopesDirections = (short[]) ccan1.getCANValues();

		final ICA ca3 = new CASlopesAccumulation(slopesDirections, nrows, ncols);
		final ICAN ccan3 = new SCAN(ca3);
		ccan3.getStableState();
		ccan3.print(ca3.getClass().getSimpleName());

		final ICA ca4 = new CAGetAllSubWatershed(slopesDirections, nrows, ncols);
		final ICAN ccan4 = new SCAN(ca4);
		ccan4.getStableState();
		ccan4.print(ca4.getClass().getSimpleName());
	}
}