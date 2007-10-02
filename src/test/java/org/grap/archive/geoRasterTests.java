package org.grap.archive;

import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

public class geoRasterTests {
	public static void main(String[] args) throws IOException {
		final String src = "..//datas2tests//grid//sample.asc";
		final String dest = "..//datas2tests//geotif//sample.png";

		final GeoRaster geoRaster = GeoRasterFactory.read(src);

		System.out.println(geoRaster.getRasterMetadata().getEnvelope()
				.toString());

		geoRaster.setLUT("ice");
		geoRaster.save(dest);
	}
}