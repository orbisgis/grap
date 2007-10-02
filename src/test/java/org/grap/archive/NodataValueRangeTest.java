package org.grap.archive;

import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

import com.vividsolutions.jts.io.ParseException;

public class NodataValueRangeTest {

	/**
	 * @param args
	 * @throws NoninvertibleTransformException
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void main(String[] args)
			throws NoninvertibleTransformException, ParseException, IOException {
		// String fileName = "..//datas2tests//geotif//440607.tif";

		String fileName = "..//datas2tests//grid//sample.asc";

		final GeoRaster geoRaster = GeoRasterFactory.read(fileName);

		System.out.println(geoRaster.getImagePlus().getProcessor().getMin());

		geoRaster.setRange(0, 1000);

		System.out.println(geoRaster.getImagePlus().getProcessor().getMin());

		geoRaster.getImagePlus().show();

		geoRaster.save("..//datas2tests//geotif//sample.png");

	}

}
