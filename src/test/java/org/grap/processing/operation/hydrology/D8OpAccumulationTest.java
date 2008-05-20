package org.grap.processing.operation.hydrology;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;

public class D8OpAccumulationTest extends D8Commons {
	public void testExecute() throws Exception {
		assertTrue(test("TauDEM/d8direction_1.asc","TauDEM/d8accumulation_1.asc"));
		assertTrue(test("TauDEM/d8direction.asc","TauDEM/d8accumulation.asc"));
	}

	private boolean test(String inFile, String refFile) throws Exception {
		GeoRaster direction = GeoRasterFactory.createGeoRaster(rep + inFile);
		Operation d8OpAccumulation = new D8OpAccumulation();
		GeoRaster accumulationCalc = direction.doOperation(d8OpAccumulation);

		// compare to the reference
		GeoRaster accumulationRef = GeoRasterFactory.createGeoRaster(rep + refFile);
		return equals(accumulationRef, accumulationCalc, false);
	}
}