package org.grap.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.io.IOException;

import org.grap.io.GeoreferencingException;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;

import com.vividsolutions.jts.geom.LinearRing;

public interface GeoRaster {
	public abstract void open() throws GeoreferencingException, IOException;

	public abstract RasterMetadata getMetadata();

	public abstract void setRange(final double min, final double max);

	public abstract void setNodataValue(final float value);

	public abstract Point2D pixelToWorldCoord(final int xpixel, final int ypixel);

	public abstract Point2D getPixelCoords(final double mouseX,
			final double mouseY);

	public abstract void save(final String dest) throws IOException;

	public abstract void show() throws IOException;

	public abstract void setLUT(final ColorModel colorModel) throws IOException;

	public abstract GeoRaster doOperation(final Operation operation)
			throws OperationException;

	/**
	 * @return ImagePlus.COLOR_256, ImagePlus.COLOR_RGB, ImagePlus.GRAY8,
	 *         ImagePlus.GRAY16, ImagePlus.GRAY32
	 * 
	 * @throws IOException
	 */
	public abstract int getType() throws IOException;

	public abstract boolean isEmpty();

	public abstract PixelProvider getPixelProvider() throws IOException;

	/**
	 * 
	 * @param roi
	 *            expressed in real world coordinates.
	 * @return
	 * @throws OperationException
	 */
	public abstract GeoRaster crop(LinearRing roi) throws OperationException;

	/**
	 * 
	 * @param roi
	 *            expressed in real world coordinates.
	 * @return
	 * @throws OperationException
	 */
	public abstract GeoRaster crop(Rectangle2D roi) throws OperationException;

	public abstract GeoRaster erode() throws OperationException;

	public abstract GeoRaster convolve(float[] kernel, int focalMeanSizeX,
			int focalMeanSizeY) throws OperationException;

	public abstract GeoRaster convolve3x3(int[] kernel)
			throws OperationException;

	public abstract GeoRaster smoth() throws OperationException;

	public abstract double getMin() throws IOException;

	public abstract double getMax() throws IOException;

	public abstract int getWidth() throws IOException;

	public abstract int getHeight() throws IOException;

	public abstract ColorModel getColorModel() throws IOException;
}