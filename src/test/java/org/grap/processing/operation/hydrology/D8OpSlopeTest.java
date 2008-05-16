package org.grap.processing.operation.hydrology;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;

public class D8OpSlopeTest extends D8Commons {
	public void testExecute() throws Exception {
		GeoRaster dem = GeoRasterFactory
				.createGeoRaster("../../datatestjunit/hydrology/dem.asc");
		Operation d8OpSlope = new D8OpSlope();
		GeoRaster slopesCalc = dem.doOperation(d8OpSlope);

		// compare to the reference
		GeoRaster slopesRef = GeoRasterFactory
				.createGeoRaster("../../datatestjunit/hydrology/slope.asc");
		assertTrue(equals(slopesRef, slopesCalc, true));
	}
}