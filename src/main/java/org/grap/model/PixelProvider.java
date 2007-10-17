package org.grap.model;

import java.awt.Image;
import java.io.IOException;

public interface PixelProvider {

	public abstract short[] getShortPixels() throws IOException;

	public abstract float[] getFloatPixels() throws IOException;

	public abstract byte[] getBytePixels() throws IOException;

	public abstract Object getPixels() throws IOException;

	public abstract float getPixel(int x, int y) throws IOException;

	public abstract Image getImage();
}