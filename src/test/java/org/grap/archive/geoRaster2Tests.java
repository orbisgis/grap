package org.grap.archive;

import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

public class geoRaster2Tests {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {

		// String src = "..//datas2tests//grid//sample.asc";

		// String src = "..//datas2tests//geotif//F016_024.tif";

		String src = "/data/erwan/dev/eclipse/datas2tests/geotif/sami.tif";

		GeoRaster geoRaster2 = GeoRasterFactory.createGeoRaster(src);
		geoRaster2.open();
		geoRaster2.show();

		System.out.println(geoRaster2.getMetadata().getPixelSize_X());

		System.out.println(geoRaster2.getMetadata().getEnvelope().toString());

		System.out.println(geoRaster2.getHeight());

	}

}
