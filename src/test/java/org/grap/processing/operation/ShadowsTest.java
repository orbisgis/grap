package org.grap.processing.operation;

import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.Orientations;

public class ShadowsTest {
	public static void main(String[] args) {
		final String src = "../../datas2tests/grid/sample.asc";

		final GeoRaster geoRaster = new GeoRaster(src);
		geoRaster.open();
		final Operation shadows = new Shadows(Orientations.NORTH);
		final GeoRaster result = geoRaster.doOperation(shadows);
		result.setRange(0, 1500);
		result.setLUT("fire");
		result.show();
	}
}