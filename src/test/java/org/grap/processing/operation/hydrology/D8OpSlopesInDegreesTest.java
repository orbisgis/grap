package org.grap.processing.operation.hydrology;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;

public class D8OpSlopesInDegreesTest extends D8Commons {
	public void testExecute() throws Exception {
		GeoRaster dem = GeoRasterFactory
				.createGeoRaster("../../datatestjunit/hydrology/dem.asc");
		GeoRaster slopesInDegreesRef = GeoRasterFactory
				.createGeoRaster("../../datatestjunit/hydrology/slopedegrees.asc");

		Operation d8OpSlopesInDegrees = new D8OpSlopeInDegrees();
		GeoRaster slopesInDegreesCalc = dem.doOperation(d8OpSlopesInDegrees);

		assertTrue(equals(slopesInDegreesRef, slopesInDegreesCalc));
	}
}