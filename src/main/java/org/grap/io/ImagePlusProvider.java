package org.grap.io;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.image.ColorModel;
import java.io.IOException;

public interface ImagePlusProvider {
	public abstract ImagePlus getImagePlus() throws IOException;

	public abstract void setLUT(final ColorModel colorModel) throws IOException;

	public abstract ImageProcessor getProcessor() throws IOException;

	public abstract double getMin() throws IOException;

	public abstract int getWidth() throws IOException;

	public abstract int getHeight() throws IOException;

	public abstract ColorModel getColorModel() throws IOException;

	public abstract double getMax() throws IOException;

	public abstract int getType() throws IOException;
}