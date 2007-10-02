package org.grap.archive;

import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

public class GeoRaster2Tests {
	public static void main(String[] args) throws IOException {
		// String fileName = "..//datas2tests//grid//sample.asc";
		// String fileName = "..//datas2tests//geotif//F016_024.tif";
		String fileName = "/data/erwan/dev/eclipse/datas2tests/geotif/sami.tif";

		final GeoRaster geoRaster = GeoRasterFactory.read(fileName);
		geoRaster.getImagePlus().show();

		System.out.println(geoRaster.getRasterMetadata().getPixelSize_X());
		System.out.println(geoRaster.getRasterMetadata().getEnvelope()
				.toString());
		System.out.println(geoRaster.getImagePlus().getHeight());
	}
}