package org.grap;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

public class Analysis {

	public static void main(String[] args) throws Throwable {
		GeoRaster gr = GeoRasterFactory
				.createGeoRaster("../../datas2tests/geotif/440606.tif");
		gr.open();

		long l1 = System.currentTimeMillis();
		gr.setRangeValues(0, 40);

//		gr.setNodataValue(30);
		gr.getImagePlus().getProcessor().getPixels();
		long l2 = System.currentTimeMillis();
		System.out.println("Time: " + ((l2 - l1) / 1000.0));
	}
}
