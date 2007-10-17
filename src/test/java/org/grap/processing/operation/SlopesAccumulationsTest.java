package org.grap.processing.operation;

import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;

public class SlopesAccumulationsTest extends GrapTest {
	private GeoRaster geoRasterSrc;

	protected void setUp() throws Exception {
		super.setUp();
		geoRasterSrc = sampleDEM;
	}

	public void testSlopesAccumulations() throws Exception {
		// load the DEM
		geoRasterSrc.open();

		// compute the slopes directions
		final Operation slopesDirections = new SlopesDirections();
		final GeoRaster grSlopesDirections = geoRasterSrc
				.doOperation(slopesDirections);

		// compute the slopes accumulations
		final Operation slopesAccumulations = new SlopesAccumulations();
		final GeoRaster grSlopesAccumulations = grSlopesDirections
				.doOperation(slopesAccumulations);

		// compare the computed accumulations with previous ones
		compareGeoRasterAndArray(grSlopesAccumulations,
				slopesAccumulationForDEM);
	}
}