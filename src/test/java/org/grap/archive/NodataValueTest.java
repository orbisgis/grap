package org.grap.archive;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.TextReader;
import ij.process.ImageProcessor;

public class NodataValueTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Opener opener = new Opener();
		// ImagePlus imp = opener.openImage(src );

		String src = "..//datas2tests//grid//ijsample.asc";

		TextReader textReader = new TextReader();
		ImageProcessor ip = textReader.open(src);

		System.out.println(ip.getMin());
		System.out.println(ip.getMax());

		// Cette option permet d'ajuster les valeurs affichées.

		// ip.setMinAndMax(0, 500);

		ip.setThreshold(0.0d, 500.0d, ImageProcessor.NO_LUT_UPDATE);

		// ip.setBackgroundValue(550d);
		ImagePlus imp = new ImagePlus("", ip);

		WindowManager.setTempCurrentImage(imp);
		IJ.run("NaN Background");

		imp.show();

		System.out.println(imp.getProcessor().getf(0, 0));

		int pixelx = 10;
		int pixely = 10;

		float v = ip.getPixelValue(pixelx, pixely);

		System.out.println(v);

	}

}
