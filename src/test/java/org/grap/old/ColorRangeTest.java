package org.grap.archive;

import ij.ImagePlus;
import ij.LookUpTable;
import ij.plugin.LutLoader;
import ij.process.ImageProcessor;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

import org.grap.model.GeoRaster;

public class ColorRangeTest {

	public static void main(String[] args) {

		LutLoader lutViewer = new LutLoader();

		lutViewer.run("/home/bocher/ImageJ/luts/erwan.lut");

		LookUpTable lut = lutViewer.createLut();

		ColorModel colormodel = lut.getColorModel();

		String src = "..//datas2tests//grid//mntzee_500.asc";

		GeoRaster geoRaster = new GeoRaster(src);

		geoRaster.open();

		ImagePlus imp = geoRaster.getImagePlus();

		ImageProcessor ip = imp.getProcessor();

		System.out.println("min " + ip.getMin() + " max " + ip.getMax());

		float rang[] = new float[] { (float) ip.getMin(), 400, 1000,
				(float) ip.getMax() };
		byte[] red = new byte[] { (byte) 255, (byte) 128, (byte) 0xff };
		byte[] green = new byte[] { 0, 0, 0 };
		byte[] blue = new byte[] { 0, 0, 0 };

		ColorModel cm = new IndexColorModel(8, rang.length - 1, red, green,
				blue);

		// ColorModel cm = new IndexColorModel(8, 2, new byte[] { 0,
		// (byte) 255 }, new byte[] { 0, 0 }, new byte[] { (byte) 255,
		// 0 });

		ip.setColorModel(cm);

		imp.show();

	}

}
