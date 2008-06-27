package org.grap.processing.operation.manual;

import java.awt.image.ColorModel;

import org.grap.lut.LutDisplay;
import org.grap.lut.LutGenerator;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

public class LUTTest {
	public static void main(String[] args) throws Exception {
		String src = "../../datas2tests/grid/sample.asc";

		ColorModel cm = LutGenerator.colorModel("fire");
		GeoRaster geoRaster = GeoRasterFactory.createGeoRaster(src);
		geoRaster.open();
		geoRaster.getImagePlus().getProcessor().setColorModel(cm);
		geoRaster.show();

		LutDisplay lutDisplay = new LutDisplay(cm);

		lutDisplay.getImagePlus().show();

	}

}