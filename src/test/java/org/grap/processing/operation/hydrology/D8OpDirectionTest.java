package org.grap.processing.operation.hydrology;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;

public class D8OpDirectionTest extends D8Commons {
	public void testExecute() throws Exception {
		GeoRaster dem = GeoRasterFactory.createGeoRaster(rep + "dem.asc");
		GeoRaster directionRef = GeoRasterFactory.createGeoRaster(rep
				+ "d8direction_saga.asc");

		Operation d8OpDirection = new D8OpDirection();
		GeoRaster directionCalc = dem.doOperation(d8OpDirection);

		assertTrue(equals(directionRef, directionCalc));
	}
}