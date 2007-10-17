package org.grap.model;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.awt.image.ColorModel;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.grap.io.GeoreferencingException;

public class GeoRasterFactory {

	public static GeoRaster createGeoRaster(final String fileName)
			throws FileNotFoundException, IOException, GeoreferencingException {
		return createGeoRaster(fileName, GeoProcessorType.FLOAT);
	}

	public static GeoRaster createGeoRaster(final String fileName,
			final GeoProcessorType geoProcessorType)
			throws FileNotFoundException, IOException, GeoreferencingException {
		return new DefaultGeoRaster(fileName, geoProcessorType);
	}

	public static GeoRaster createNullGeoRaster() {
		return NullGeoRaster.instance;
	}

	public static GeoRaster createGeoRaster(final byte[] pixels,
			final int ncols, final int nrows, final ColorModel cm,
			final RasterMetadata rasterMetadata) {
		final ImageProcessor ip = new ByteProcessor(ncols, nrows, pixels, cm);
		return createGeoRaster(ip, rasterMetadata);
	}

	public static GeoRaster createGeoRaster(final float[] pixels,
			final int ncols, final int nrows, final ColorModel cm,
			final RasterMetadata rasterMetadata) {
		final ImageProcessor ip = new FloatProcessor(ncols, nrows, pixels, cm);
		return createGeoRaster(ip, rasterMetadata);
	}

	public static GeoRaster createGeoRaster(final short[] pixels,
			final int ncols, final int nrows, final ColorModel cm,
			final RasterMetadata rasterMetadata) {
		final ImageProcessor ip = new ShortProcessor(ncols, nrows, pixels, cm);
		return createGeoRaster(ip, rasterMetadata);
	}

	public static GeoRaster createGeoRaster(
			final ImageProcessor imageProcessor,
			final RasterMetadata rasterMetadata) {
		final ImagePlus imagePlus = new ImagePlus("", imageProcessor);
		return new DefaultGeoRaster(imagePlus, rasterMetadata);
	}

	public static GeoRaster createGeoRaster(final byte[] pixels,
			final int ncols, final int nrows,
			final RasterMetadata rasterMetadata) {
		return createGeoRaster(pixels, ncols, nrows, null, rasterMetadata);
	}

	public static GeoRaster createGeoRaster(final short[] pixels,
			final int ncols, final int nrows,
			final RasterMetadata rasterMetadata) {
		return createGeoRaster(pixels, ncols, nrows, null, rasterMetadata);
	}

	public static GeoRaster createGeoRaster(final float[] pixels,
			final int ncols, final int nrows,
			final RasterMetadata rasterMetadata) {
		return createGeoRaster(pixels, ncols, nrows, null, rasterMetadata);
	}
}