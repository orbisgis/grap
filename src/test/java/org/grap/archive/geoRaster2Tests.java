package org.grap.archive;

import java.io.IOException;

import org.grap.model.GeoRaster;

public class geoRaster2Tests {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		// String src = "..//datas2tests//grid//sample.asc";

		// String src = "..//datas2tests//geotif//F016_024.tif";

		String src = "/data/erwan/dev/eclipse/datas2tests/geotif/sami.tif";

		GeoRaster geoRaster2 = new GeoRaster(src);
		geoRaster2.open();
		geoRaster2.getImagePlus().show();

		System.out.println(geoRaster2.getMetadata().getPixelSize_X());

		System.out.println(geoRaster2.getMetadata().getEnvelope().toString());

		System.out.println(geoRaster2.getImagePlus().getHeight());

	}

}
