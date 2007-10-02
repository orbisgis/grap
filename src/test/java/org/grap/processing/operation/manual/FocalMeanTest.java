package org.grap.processing.operation.manual;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.grap.processing.operation.FocalMean;

public class FocalMeanTest {
	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		final String fileName = "../../datas2tests/grid/sample.asc";

		final GeoRaster grDEM = GeoRasterFactory.read(fileName);
		final Operation focalMean = new FocalMean(7);
		final GeoRaster result = grDEM.doOperation(focalMean);
		result.setLUT("fire");
		result.show();
	}
}