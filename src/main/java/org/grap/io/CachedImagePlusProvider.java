package org.grap.io;

import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ImageProcessor;

import java.awt.image.ColorModel;

public class CachedImagePlusProvider implements ImagePlusProvider {
	private ImagePlus imagePlus;
	private ImageProcessor processor;

	public CachedImagePlusProvider(final String fileName) {
		this.imagePlus = new Opener().openImage(fileName);
		this.processor = imagePlus.getProcessor();
	}

	public CachedImagePlusProvider(final ImagePlus imagePlus) {
		this.imagePlus = imagePlus;
		this.processor = imagePlus.getProcessor();
	}

	public ImagePlus getImagePlus() {
		return imagePlus;
	}

	public void setLUT(final ColorModel colorModel) {
		imagePlus.getProcessor().setColorModel(colorModel);
	}

	public ImageProcessor getProcessor() {
		return processor;
	}

	public ColorModel getColorModel() {
		return processor.getColorModel();
	}

	public int getHeight() {
		return imagePlus.getHeight();
	}

	public double getMin() {
		return processor.getMin();
	}

	public int getWidth() {
		return imagePlus.getWidth();
	}

	public double getMax() {
		return processor.getMax();
	}

	public int getType() {
		return imagePlus.getType();
	}
}