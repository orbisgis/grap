package org.grap.model;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.Image;
import java.io.IOException;

public class DefaultPixelProvider implements PixelProvider {
	private ImageProcessor processor;
	private int type;
	private float noDataValue;
	private Image image;

	public DefaultPixelProvider(final Image image,
			final ImageProcessor processor, int type, float noDataValue) {
		this.image = image;
		this.processor = processor;
		this.type = type;
		this.noDataValue = noDataValue;
	}

	public short[] getShortPixels() throws IOException {
		short[] ret;
		if (type == ImagePlus.GRAY8) {
			ret = (short[]) processor.convertToShort(false).getPixels();
		} else if (type == ImagePlus.GRAY16) {
			ret = (short[]) processor.getPixels();
		} else if (type == ImagePlus.GRAY32) {
			ret = (short[]) processor.convertToShort(false).getPixels();
		} else if (type == ImagePlus.COLOR_256) {
			ret = (short[]) processor.convertToShort(false).getPixels();
		} else if (type == ImagePlus.COLOR_RGB) {
			ret = (short[]) processor.convertToShort(false).getPixels();
		} else {
			throw new IllegalStateException("Unsupported raster type: " + type);
		}
		if (noDataValue != Float.NaN) {
			for (int i = 0; i < ret.length; i++) {
				if (ret[i] == noDataValue) {
					ret[i] = (short) Float.NaN;
				}
			}
		}
		return ret;
	}

	public byte[] getBytePixels() throws IOException {
		byte[] ret;
		if (type == ImagePlus.GRAY8) {
			ret = (byte[]) processor.getPixels();
		} else if (type == ImagePlus.GRAY16) {
			ret = (byte[]) processor.convertToByte(false).getPixels();
		} else if (type == ImagePlus.GRAY32) {
			ret = (byte[]) processor.convertToByte(false).getPixels();
		} else if (type == ImagePlus.COLOR_256) {
			ret = (byte[]) processor.convertToByte(false).getPixels();
		} else if (type == ImagePlus.COLOR_RGB) {
			ret = (byte[]) processor.convertToByte(false).getPixels();
		} else {
			throw new IllegalStateException("Unsupported raster type: " + type);
		}
		if (noDataValue != Float.NaN) {
			for (int i = 0; i < ret.length; i++) {
				if (ret[i] == noDataValue) {
					ret[i] = (short) Float.NaN;
				}
			}
		}
		return ret;
	}

	public float getPixel(int x, int y) throws IOException {
		float f = processor.getPixelValue(x, y);
		if (f == noDataValue) {
			return Float.NaN;
		} else {
			return f;
		}
	}

	public Object getPixels() throws IOException {
		return processor.getPixels();
	}

	public float[] getFloatPixels() throws IOException {
		float[] ret;
		if (type == ImagePlus.GRAY8) {
			ret = (float[]) processor.convertToFloat().getPixels();
		} else if (type == ImagePlus.GRAY16) {
			ret = (float[]) processor.convertToFloat().getPixels();
		} else if (type == ImagePlus.GRAY32) {
			ret = (float[]) processor.getPixels();
		} else if (type == ImagePlus.COLOR_256) {
			ret = (float[]) processor.convertToFloat().getPixels();
			ret = (float[]) processor.getPixels();
		} else if (type == ImagePlus.COLOR_RGB) {
			ret = (float[]) processor.convertToFloat().getPixels();
		} else {
			throw new IllegalStateException("Unsupported raster type: " + type);
		}
		if (noDataValue != Float.NaN) {
			for (int i = 0; i < ret.length; i++) {
				if (ret[i] == noDataValue) {
					ret[i] = Float.NaN;
				}
			}
		}
		return ret;
	}

	public Image getImage() {
		return image;
	}
}