package org.grap.processing.operation.hydrology;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;

public class D8OpDirectionTest extends D8Commons {
	public void testExecute() throws Exception {
		assertTrue(test("dem_1.asc", "TauDEM/d8direction_1.asc"));
		assertTrue(test("dem.asc", "TauDEM/d8direction.asc"));
	}

	private boolean test(String inFile, String refFile) throws Exception {
		GeoRaster dem = GeoRasterFactory.createGeoRaster(rep + inFile);
		Operation d8OpDirection = new D8OpDirection();
		GeoRaster directionCalc = dem.doOperation(d8OpDirection);

		// compare to the reference
		GeoRaster directionRef = GeoRasterFactory
				.createGeoRaster(rep + refFile);
		return equals(directionRef, directionCalc, true);
	}
}