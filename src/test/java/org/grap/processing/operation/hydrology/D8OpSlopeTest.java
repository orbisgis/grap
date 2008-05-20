package org.grap.processing.operation.hydrology;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;

public class D8OpSlopeTest extends D8Commons {
	public void testExecute() throws Exception {
		assertTrue(test("dem_1.asc", "TauDEM/d8slope_1.asc"));
		assertTrue(test("dem.asc", "TauDEM/d8slope.asc"));
	}

	private boolean test(String inFile, String refFile) throws Exception {
		GeoRaster dem = GeoRasterFactory.createGeoRaster(rep + inFile);
		Operation d8OpSlope = new D8OpSlope();
		GeoRaster slopesCalc = dem.doOperation(d8OpSlope);

		// compare to the reference
		GeoRaster slopesRef = GeoRasterFactory.createGeoRaster(rep + refFile);
		return equals(slopesRef, slopesCalc, true);
	}
}