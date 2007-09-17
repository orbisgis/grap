package org.grap.processing.cellularAutomata;

public class Main {
	public static void main(String[] args) {
		final float[] DEM = new float[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, };
		final int nrows = 5;
		final int ncols = 5;

		final IIntCA ca1 = new CASlopesDirections(DEM, nrows, ncols);
		final ACAN can1 = new IntCAN(ca1);
		can1.getStableState();
		can1.print();

		final IFloatCA ca2 = new CASlopesInPercent(DEM, nrows, ncols);
		final ACAN can2 = new FloatCAN(ca2);
		can2.getStableState();
		can2.print();

		final int[] slopesDirections = (int[]) can1.getValuesSnapshot();

		final IIntCA ca3 = new CASlopesAccumulation(slopesDirections, nrows,
				ncols);
		final ACAN can3 = new IntCAN(ca3);
		can3.getStableState();
		can3.print();

		final IIntCA ca4 = new CAGetAllSubWatershed(slopesDirections, nrows,
				ncols);
		final ACAN can4 = new IntCAN(ca4);
		can4.getStableState();
		can4.print();
	}
}