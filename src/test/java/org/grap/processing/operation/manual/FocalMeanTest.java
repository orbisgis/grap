package org.grap.processing.operation.manual;

import org.grap.TestUtils;
import org.grap.lut.LutGenerator;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.operation.FocalMean;

public class FocalMeanTest {
	public static void main(String[] args) throws Exception {
		final String src = "../../datas2tests/geotif/440706.tif";
		// final String src = "/tmp/mypng.png";

		final GeoRaster geoRaster = GeoRasterFactory.createGeoRaster(src);
		geoRaster.open();
		final GeoRaster result = geoRaster.doOperation(new FocalMean(3));
		TestUtils.printFreeMemory();
		result.setLUT(LutGenerator.colorModel("fire"));
		TestUtils.printFreeMemory();
		result.show();
	}
}