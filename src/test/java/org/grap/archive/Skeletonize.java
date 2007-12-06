package org.grap.archive;

import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ByteProcessor;

import java.awt.Image;

public class Skeletonize {
	public static void main(String[] args) {
		Image imgresult;
		ImagePlus imp = new Opener()
				.openImage("../../datas2tests/toBeSkeletonize.tif");
		imp
				.setProcessor(imp.getTitle(), imp.getProcessor().convertToByte(
						true));
		
		ByteProcessor byteprocessor = (ByteProcessor) imp.getProcessor();
		byteprocessor.skeletonize();

		// the skeletonized image (should be updated)
		imgresult = imp.getImage();

		// The method above returns you a cached image
		// which is usually updated after editing the source processor,
		// but not always.
		// You can force it to update with:

		imp.updateAndDraw();
		imgresult = imp.getImage();

		// Or just get a new, fresh image directly:
		imgresult = byteprocessor.createImage();
		new ImagePlus("", imgresult).show();
	}
}