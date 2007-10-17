package org.grap.model;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.io.IOException;

import org.grap.processing.Operation;

import com.vividsolutions.jts.geom.LinearRing;

class NullGeoRaster implements GeoRaster {
	static GeoRaster instance = new NullGeoRaster();

	/*
	 * We don't want instances of this class to be created by the user
	 */
	private NullGeoRaster() {
	}

	public GeoRaster doOperation(Operation operation) {
		return instance;
	}

	public ImagePlus getImagePlus() {
		return null;
	}

	public RasterMetadata getMetadata() {
		return null;
	}

	public Point2D getPixelCoords(double mouseX, double mouseY) {
		return null;
	}

	public int getType() {
		return 0;
	}

	public void open() throws IOException {
	}

	public Point2D pixelToWorldCoord(int xpixel, int ypixel) {
		return null;
	}

	public void save(String dest) throws IOException {
	}

	public void setLUT(ColorModel LUTName) {
	}

	public void setNodataValue(float value) {
	}

	public void setRange(double min, double max) {
	}

	public void show() {
	}

	public boolean isEmpty() {
		return true;
	}

	public int getPixel(int x, int y) {
		return 0;
	}

	public ImageProcessor convertToShort(boolean b) {
		return null;
	}

	public GeoRaster convolve(float[] kernel, int focalMeanSizeX,
			int focalMeanSizeY) {
		return instance;
	}

	public GeoRaster convolve3x3(int[] kernel) {
		return instance;
	}

	public GeoRaster crop(Rectangle2D roi) {
		return instance;
	}

	public GeoRaster erode() {
		return instance;
	}

	public GeoRaster smoth() {
		return instance;
	}

	public GeoRaster crop(LinearRing roi) {
		return instance;
	}

	public ColorModel getColorModel() {
		return null;
	}

	public int getHeight() throws IOException {
		return 0;
	}

	public double getMax() throws IOException {
		return 0;
	}

	public double getMin() throws IOException {
		return 0;
	}

	public int getWidth() throws IOException {
		return 0;
	}

	public PixelProvider getPixelProvider() throws IOException {
		return null;
	}
}