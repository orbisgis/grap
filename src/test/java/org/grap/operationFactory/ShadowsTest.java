package org.grap.operationFactory;

import ij.measure.Calibration;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationFactory;

import com.vividsolutions.jts.geom.Geometry;

public class ShadowsTest {

	private static RasterMetadata rasterMetadata;

	private static double[] pixelToWorldCoord;

	private static Calibration cal1;

	public static void main(String[] args) {

		String src = "..//datas2tests//grid//sample.asc";

		Geometry geom = null;
		GeoRaster geoRaster = new GeoRaster(src);

		geoRaster.open();

		OperationFactory opf = new OperationFactory();

		Operation operation = opf.shadows(geoRaster, 1);

		GeoRaster result = operation.execute();
		result.setRange(0, 1500);

		result.setLUT("ice");

		result.show();

	}

}
