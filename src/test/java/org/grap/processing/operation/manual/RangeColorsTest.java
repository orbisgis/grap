package org.grap.processing.operation.manual;

import java.awt.Color;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

public class RangeColorsTest {
	public static void main(String[] args) throws Exception {
		String src = "../../datas2tests/grid/sample.asc";

		GeoRaster geoRaster = GeoRasterFactory.createGeoRaster(src);
		geoRaster.open();
		geoRaster.setRangeColors(new double[] { 70, 200, 400, 600, 1300 },
				new Color[] { Color.BLUE, Color.CYAN, Color.GREEN, Color.RED });
		geoRaster.show();
	}
}