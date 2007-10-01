package org.grap.archive;

import java.io.IOException;

import org.grap.model.GeoRaster;

public class geoRasterTests {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		String src = "..//datas2tests//grid//sample.asc";
		String dest = "..//datas2tests//geotif//sample.png";

		GeoRaster geoRaster = new GeoRaster(src);
		geoRaster.open();

		System.out.println(geoRaster.getMetadata().getEnvelope().toString());

		geoRaster.setLUT("ice");

		geoRaster.save(dest);

	}

}
