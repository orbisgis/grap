package org.grap.archive;

import java.io.IOException;

import org.grap.lut.LutGenerator;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

public class geoRasterTests {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {

		String src = "..//datas2tests//grid//sample.asc";
		String dest = "..//datas2tests//geotif//sample.png";

		GeoRaster geoRaster = GeoRasterFactory.createGeoRaster(src);
		geoRaster.open();

		System.out.println(geoRaster.getMetadata().getEnvelope().toString());

		geoRaster.setLUT(LutGenerator.colorModel("ice"));

		geoRaster.save(dest);

	}

}
