package org.grap.io;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.image.ColorModel;
import java.io.IOException;

public class DirectImagePlusProvider implements ImagePlusProvider {
	private FileReader fileReader;

	private CachedValues values = null;

	public DirectImagePlusProvider(final FileReader fileReader) {
		this.fileReader = fileReader;
	}

	private CachedValues getCachedValues(ImagePlus img) throws IOException {
		if (values == null) {
			values = new CachedValues();
			ImagePlus ip = img;
			if (ip == null) {
				ip = getImagePlus();
			}
			ImageProcessor processor = ip.getProcessor();
			values.colorModel = processor.getColorModel();
			values.min = processor.getMin();
			values.max = processor.getMax();
			values.height = ip.getHeight();
			values.width = ip.getWidth();
			values.type = ip.getType();

		}

		return values;
	}

	public ImagePlus getImagePlus() throws IOException {
		ImagePlus imagePlus;
		try {
			imagePlus = fileReader.readImagePlus();
		} catch (GeoreferencingException e) {
			throw new IOException(e.getMessage());
		}
		CachedValues cachedValues = getCachedValues(imagePlus);
		if (cachedValues.colorModel != null) {
			imagePlus.getProcessor().setColorModel(cachedValues.colorModel);
		}
		return imagePlus;
	}

	public void setLUT(final ColorModel colorModel) throws IOException {
		getCachedValues(null).colorModel = colorModel;
	}

	public ImageProcessor getProcessor() throws IOException {
		return getImagePlus().getProcessor();
	}

	public ColorModel getColorModel() throws IOException {
		return getCachedValues(null).colorModel;
	}

	public int getHeight() throws IOException {
		return getCachedValues(null).height;
	}

	public double getMax() throws IOException {
		return getCachedValues(null).max;
	}

	public double getMin() throws IOException {
		return getCachedValues(null).min;
	}

	public int getWidth() throws IOException {
		return getCachedValues(null).width;
	}

	public int getType() throws IOException {
		return getCachedValues(null).type;
	}

	private class CachedValues {

		private ColorModel colorModel;

		private int type;

		private double min;

		private double max;

		private int width;

		private int height;

	}
}