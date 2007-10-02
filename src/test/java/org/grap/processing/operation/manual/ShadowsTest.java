package org.grap.processing.operation.manual;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.grap.processing.Orientations;
import org.grap.processing.operation.Shadows;

public class ShadowsTest {
	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		final String fileName = "../../datas2tests/grid/sample.asc";

		final GeoRaster grDEM = GeoRasterFactory.read(fileName);
		final Operation shadows = new Shadows(Orientations.NORTH);
		final GeoRaster result = grDEM.doOperation(shadows);
		result.setRange(0, 1500);
		result.setLUT("fire");
		result.show();
	}
}