package org.grap.processing.cellularAutomata;

public class Main2 {
	public static void main(String[] args) {
		final float[] DEM = new float[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, };
		final int nrows = 5;
		final int ncols = 5;

		final IShortCA ca1 = new CASlopesDirections(DEM, nrows, ncols);

		final ACAN can1 = new ShortCAN(ca1);
		can1.getStableState();
		can1.print();

		final CAN ccan1 = new CAN(ca1);
		ccan1.getStableState();
		ccan1.print();

//		final IFloatCA ca2 = new CASlopesInPercent(DEM, nrows, ncols);
//		final ACAN can2 = new FloatCAN(ca2);
//		can2.getStableState();
//		can2.print();

		final short[] slopesDirections = (short[]) ccan1.getCANValues();

//		final IFloatCA ca3 = new CASlopesAccumulation(slopesDirections, nrows,
//				ncols);
//		final ACAN can3 = new FloatCAN(ca3);
//		can3.getStableState();
//		can3.print();

		final IFloatCA ca4 = new CAGetAllSubWatershed(slopesDirections, nrows,
				ncols);
		final ACAN can4 = new FloatCAN(ca4);
		can4.getStableState();
		can4.print();

		final CAN ccan4 = new CAN(ca4);
		ccan4.getStableState();
		ccan4.print();
	}
}