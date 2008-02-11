package org.grap.lut;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.ImageProcessor;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

public class LutDisplay {
	private ColorModel colorModel;

	public LutDisplay(final ColorModel colorModel) {
		this.colorModel = colorModel;
	}

	public ImagePlus getImagePlus() {
		final int w = 256;
		final int h = 20;
		final ImagePlus imagePlus = NewImage.createByteImage("Lut", w, h, 1, 0);

		if (colorModel instanceof IndexColorModel) {
			final ImageProcessor imageProcessor = imagePlus.getProcessor();
			final byte[] pixels = (byte[]) imageProcessor.getPixels();

			int j = 0;
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					pixels[j++] = (byte) x;
				}
			}
			imagePlus.getProcessor().setColorModel(colorModel);
			imagePlus.updateAndDraw();
		}
		
		return imagePlus;
	}
}