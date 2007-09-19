package org.grap.processing.cellularAutomata;

import org.grap.processing.cellularAutomata.parallelImpl.CAN;

public class MainPar {
	public static void main(String[] args) {
		final float[] DEM = new float[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, };
		final int nrows = 5;
		final int ncols = 5;

		final IShortCA ca1 = new CASlopesDirections(DEM, nrows, ncols);

		final CAN ccan1 = new CAN(ca1);
		ccan1.getStableState();
		ccan1.print();

		final IFloatCA ca2 = new CASlopesInPercent(DEM, nrows, ncols);
		final CAN ccan2 = new CAN(ca2);
		ccan2.getStableState();
		ccan2.print();

		final short[] slopesDirections = (short[]) ccan1.getCANValues();

		final IFloatCA ca3 = new CASlopesAccumulation(slopesDirections, nrows,
				ncols);
		final CAN ccan3 = new CAN(ca3);
		ccan3.getStableState();
		ccan3.print();

		final IFloatCA ca4 = new CAGetAllSubWatershed(slopesDirections, nrows,
				ncols);
		final CAN ccan4 = new CAN(ca4);
		ccan4.getStableState();
		ccan4.print();
	}
}