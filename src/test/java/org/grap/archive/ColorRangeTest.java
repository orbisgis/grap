package org.grap.archive;

import ij.ImagePlus;
import ij.LookUpTable;
import ij.plugin.LutLoader;
import ij.process.ImageProcessor;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

public class ColorRangeTest {

	public static void main(String[] args) throws FileNotFoundException,
			IOException {

		final LutLoader lutViewer = new LutLoader();

		lutViewer.run("/home/bocher/ImageJ/luts/erwan.lut");

		final LookUpTable lut = lutViewer.createLut();

		final ColorModel colormodel = lut.getColorModel();

		final String fileName = "..//datas2tests//grid//mntzee_500.asc";

		final GeoRaster geoRaster = GeoRasterFactory.read(fileName);

		final ImagePlus imp = geoRaster.getImagePlus();

		final ImageProcessor ip = imp.getProcessor();

		System.out.println("min " + ip.getMin() + " max " + ip.getMax());

		final float rang[] = new float[] { (float) ip.getMin(), 400, 1000,
				(float) ip.getMax() };
		final byte[] red = new byte[] { (byte) 255, (byte) 128, (byte) 0xff };
		final byte[] green = new byte[] { 0, 0, 0 };
		final byte[] blue = new byte[] { 0, 0, 0 };

		final ColorModel cm = new IndexColorModel(8, rang.length - 1, red,
				green, blue);

		// ColorModel cm = new IndexColorModel(8, 2, new byte[] { 0,
		// (byte) 255 }, new byte[] { 0, 0 }, new byte[] { (byte) 255,
		// 0 });

		ip.setColorModel(cm);

		imp.show();

	}

}
