package org.grap.operationFactory;

import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationFactory;

import com.vividsolutions.jts.io.ParseException;

public class SlopeTest {

	/**
	 * @param args
	 * @throws NoninvertibleTransformException
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void main(String[] args)
			throws NoninvertibleTransformException, ParseException, IOException {

		String fileName = "../..//datas2tests//grid//sample.asc";
		// String fileName = "../..//datas2tests//grid//mntzee_500.asc";
		// String fileName = "../..//datas2tests//grid//saipan-5.asc";
		String dest = "/tmp/slope.tif";
		GeoRaster geoRaster = new GeoRaster(fileName);
		geoRaster.open();

		// geoRaster.show();

		OperationFactory opf = new OperationFactory();

		Operation operation = opf.slope(geoRaster);

		GeoRaster result = operation.execute();

		result.setLUT("fire");

		System.out.println(result.getImagePlus().getProcessor().getPixelValue(
				200, 200));
		result.show();

		result.save(dest);
	}
}