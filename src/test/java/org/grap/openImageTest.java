package org.grap;

import ij.ImagePlus;
import ij.plugin.TextReader;
import ij.process.ImageProcessor;

public class openImageTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Opener opener = new Opener();
		// ImagePlus imp = opener.openImage(src );

		String src = "..//datas2tests//grid//ijsample.asc";

		TextReader textReader = new TextReader();
		ImageProcessor ip = textReader.open(src);
		ImagePlus imp = new ImagePlus("", ip);
		imp.show();

		int pixelx = 300;
		int pixely = 300;

		int[] v = imp.getPixel(pixelx, pixely);

		System.out.println(Float.intBitsToFloat(v[0]));

	}

}
