package org.grap.misc;

import java.awt.Color;

import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

public class SetRangeValuesTest extends GrapTest {
	private final static int RED = Color.RED.getRGB();

	public void testTransparency() throws Exception {
		final GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData + "/geotif/440606.tif");
		gr.open();

		gr.setRangeValues(0, 10);
		
		gr.show();

		
	}
}