package org.grap.operationFactory;

import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationFactory;

public class FocalMeanTest {

	public static void main(String[] args) {

		String src = "..//datas2tests//grid//sample.asc";

		GeoRaster geoRaster = new GeoRaster(src);

		geoRaster.open();

		OperationFactory opf = new OperationFactory();

		Operation operation = opf.focalMean(geoRaster, 7);

		GeoRaster result = operation.execute();

		result.setLUT("ice");

		result.show();

	}

}
