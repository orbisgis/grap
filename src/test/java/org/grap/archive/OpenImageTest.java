package org.grap.archive;

import ij.ImagePlus;
import ij.io.Opener;
import ij.plugin.TextReader;
import ij.process.ImageProcessor;

import org.grap.lut.LutGenerator;

public class OpenImageTest {
	public static void main(String[] args) {
		final String src1 = "../../datas2tests/geotif/440606.tif";
		final Opener opener = new Opener();
		final ImagePlus imp1 = opener.openImage(src1);
		imp1.getProcessor().setColorModel(LutGenerator.colorModel("fire"));
		imp1.show();
		System.out.println(imp1.getType() == ImagePlus.GRAY8);

		final String src2 = "../../datas2tests/grid/ijsample.asc";
		final TextReader textReader = new TextReader();
		final ImageProcessor ip2 = textReader.open(src2);
		final ImagePlus imp2 = new ImagePlus("", ip2);

		ip2.setColorModel(LutGenerator.colorModel("fire"));
		imp2.show();
		System.out.println(imp2.getType() == ImagePlus.GRAY32);

		int[] v = imp2.getPixel(300, 300);
		System.out.println(Float.intBitsToFloat(v[0]));
	}
}