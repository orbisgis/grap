package org.grap.processing.operation.manual;

import org.grap.lut.LutGenerator;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.grap.processing.operation.others.Orientations;
import org.grap.processing.operation.others.Shadows;

public class ShadowsTest {
	public static void main(String[] args) throws Exception {
		final String src = "../../datas2tests/grid/sample.asc";

		final GeoRaster geoRaster = GeoRasterFactory.createGeoRaster(src);
		geoRaster.open();
		final Operation shadows = new Shadows(Orientations.NORTH);
		final GeoRaster result = geoRaster.doOperation(shadows);
		result.setRangeValues(0, 500);
		result.getImagePlus().getProcessor().setColorModel(
				LutGenerator.colorModel("fire"));
		result.show();
	}
}