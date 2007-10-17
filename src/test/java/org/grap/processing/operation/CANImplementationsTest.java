package org.grap.processing.operation;

import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.processing.cellularAutomata.CAGetAllSubWatershed;
import org.grap.processing.cellularAutomata.CASlopesAccumulation;
import org.grap.processing.cellularAutomata.CASlopesDirections;
import org.grap.processing.cellularAutomata.CASlopesInPercent;
import org.grap.processing.cellularAutomata.cam.ICA;
import org.grap.processing.cellularAutomata.cam.ICAFloat;
import org.grap.processing.cellularAutomata.cam.ICAN;
import org.grap.processing.cellularAutomata.cam.ICAShort;
import org.grap.processing.cellularAutomata.parallelImpl.PCAN;
import org.grap.processing.cellularAutomata.seqImpl.SCAN;

public class CANImplementationsTest extends GrapTest {
	private GeoRaster geoRasterSrc;
	private short[] slopesDirections = null;

	protected void setUp() throws Exception {
		super.setUp();
		geoRasterSrc = sampleDEM;
	}

	public void testSeqAndParImplementations() throws Exception {
		// load the DEM
		// geoRasterSrc.open();
		// final float[] DEM = geoRasterSrc.getPixelProvider().getFloatPixels();
		// final int nrows = geoRasterSrc.getHeight();
		// final int ncols = geoRasterSrc.getWidth();

		final float[] DEM = new float[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, };
		final int nrows = 5;
		final int ncols = 5;

		compareParAndSeqImpl(new CASlopesDirections(DEM, nrows, ncols));
		compareParAndSeqImpl(new CASlopesInPercent(DEM, nrows, ncols));
		compareParAndSeqImpl(new CASlopesAccumulation(slopesDirections, nrows,
				ncols));
		compareParAndSeqImpl(new CAGetAllSubWatershed(slopesDirections, nrows,
				ncols));
	}

	private void compareParAndSeqImpl(final ICA ca) {
		final ICAN scan = new SCAN(ca);
		final ICAN pcan = new PCAN(ca);

		System.out.println(ca.getClass().getSimpleName());
		final int scanNbOfIter = scan.getStableState();
		final int pcanNbOfIter = pcan.getStableState();
		// TODO assertTrue(scanNbOfIter == pcanNbOfIter);

		if (ca instanceof ICAShort) {
			final short[] seq = (short[]) scan.getCANValues();
			final short[] par = (short[]) pcan.getCANValues();
			assertTrue(seq.length == par.length);
			for (int i = 0; i < seq.length; i++) {
				assertTrue(seq[i] == par[i]);
			}
		} else if (ca instanceof ICAFloat) {
			final float[] seq = (float[]) scan.getCANValues();
			final float[] par = (float[]) pcan.getCANValues();
			assertTrue(seq.length == par.length);
			for (int i = 0; i < seq.length; i++) {
				assertTrue(seq[i] == par[i]);
			}
		} else {
			assertTrue(false);
		}

		if (ca instanceof CASlopesDirections) {
			slopesDirections = (short[]) scan.getCANValues();
		}
	}
}