package org.grap.processing.operation;

import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;

public class AllWatershedsTest extends GrapTest {
	private GeoRaster geoRasterSrc;

	protected void setUp() throws Exception {
		super.setUp();
		geoRasterSrc = sampleDEM;
	}

	public void testAllWatersheds() throws Exception {
		// load the DEM
		geoRasterSrc.open();

		// compute the slopes directions
		final Operation slopesDirections = new SlopesDirections();
		final GeoRaster grSlopesDirections = geoRasterSrc
				.doOperation(slopesDirections);

		// compute all watersheds
		final Operation allWatersheds = new AllWatersheds();
		final GeoRaster grAllWatersheds = grSlopesDirections
				.doOperation(allWatersheds);

		// compare the computed watersheds with previous ones
		compareGeoRasterAndArray(grAllWatersheds, allWatershedsForDEM);
	}
}