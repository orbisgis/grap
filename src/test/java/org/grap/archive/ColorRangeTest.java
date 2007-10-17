package org.grap.archive;

import ij.plugin.LutLoader;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

public class ColorRangeTest {

	public static void main(String[] args) throws Exception {

		LutLoader lutViewer = new LutLoader();

		lutViewer.run("/home/bocher/ImageJ/luts/erwan.lut");

		String src = "../../datas2tests/grid/mntzee_500.asc";

		GeoRaster geoRaster = GeoRasterFactory.createGeoRaster(src);

		geoRaster.open();

		System.out.println("min " + geoRaster.getMin() + " max "
				+ geoRaster.getMax());

		float rang[] = new float[] { (float) geoRaster.getMin(), 400, 1000,
				(float) geoRaster.getMax() };
		byte[] red = new byte[] { (byte) 255, (byte) 128, (byte) 0xff };
		byte[] green = new byte[] { 0, 0, 0 };
		byte[] blue = new byte[] { 0, 0, 0 };

		ColorModel cm = new IndexColorModel(8, rang.length - 1, red, green,
				blue);

		// ColorModel cm = new IndexColorModel(8, 2, new byte[] { 0,
		// (byte) 255 }, new byte[] { 0, 0 }, new byte[] { (byte) 255,
		// 0 });

		geoRaster.setLUT(cm);

		geoRaster.show();

	}

}
