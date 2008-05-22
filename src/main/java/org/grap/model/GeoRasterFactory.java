/*
 * The GRAP library (GeoRAster Processing) is a middleware dedicated
 * to the processing of various kinds of geographic raster data. It
 * provides a complete and robust API to manipulate ASCII Grid or
 * tiff, png, bmp, jpg (with the corresponding world file) geographic
 * images. GRAP is produced  by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GRAP.
 *
 * GRAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAP. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.grap.model;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.awt.image.ColorModel;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GeoRasterFactory {

	public static GeoRaster createGeoRaster(final String fileName)
			throws FileNotFoundException, IOException {
		return createGeoRaster(fileName, GeoProcessorType.FLOAT);
	}

	public static GeoRaster createGeoRaster(final String fileName,
			final GeoProcessorType geoProcessorType)
			throws FileNotFoundException, IOException {
		return new DefaultGeoRaster(fileName, geoProcessorType);
	}

	public static GeoRaster createGeoRaster(final String fileName,
			final GeoProcessorType geoProcessorType, float pixelsize)
			throws FileNotFoundException, IOException {
		return new DefaultGeoRaster(fileName, geoProcessorType, pixelsize);
	}

	public static GeoRaster createNullGeoRaster() {
		return NullGeoRaster.instance;
	}

	public static GeoRaster createGeoRaster(final byte[] pixels,
			final ColorModel cm, final RasterMetadata rasterMetadata) {
		final ImageProcessor ip = new ByteProcessor(rasterMetadata.getNCols(),
				rasterMetadata.getNRows(), pixels, cm);
		return createGeoRaster(ip, rasterMetadata);
	}

	public static GeoRaster createGeoRaster(final float[] pixels,
			final ColorModel cm, final RasterMetadata rasterMetadata) {
		final ImageProcessor ip = new FloatProcessor(rasterMetadata.getNCols(),
				rasterMetadata.getNRows(), pixels, cm);
		return createGeoRaster(ip, rasterMetadata);
	}

	public static GeoRaster createGeoRaster(final short[] pixels,
			final ColorModel cm, final RasterMetadata rasterMetadata) {
		final ImageProcessor ip = new ShortProcessor(rasterMetadata.getNCols(),
				rasterMetadata.getNRows(), pixels, cm);
		return createGeoRaster(ip, rasterMetadata);
	}

	public static GeoRaster createGeoRaster(int[] pixels,
			RasterMetadata rasterMetadata) {
		final ImageProcessor ip = new ColorProcessor(rasterMetadata.getNCols(),
				rasterMetadata.getNRows(), pixels);
		return createGeoRaster(ip, rasterMetadata);
	}

	public static GeoRaster createGeoRaster(
			final ImageProcessor imageProcessor,
			final RasterMetadata rasterMetadata) {
		final ImagePlus imagePlus = new ImagePlus("", imageProcessor);
		return new DefaultGeoRaster(imagePlus, rasterMetadata);
	}

	public static GeoRaster createGeoRaster(final ImagePlus imagePlus,
			final RasterMetadata rasterMetadata) {
		return new DefaultGeoRaster(imagePlus, rasterMetadata);
	}

	public static GeoRaster createGeoRaster(final byte[] pixels,
			final RasterMetadata rasterMetadata) {
		return createGeoRaster(pixels, null, rasterMetadata);
	}

	public static GeoRaster createGeoRaster(final short[] pixels,
			final RasterMetadata rasterMetadata) {
		return createGeoRaster(pixels, null, rasterMetadata);
	}

	public static GeoRaster createGeoRaster(final float[] pixels,
			final RasterMetadata rasterMetadata) {
		return createGeoRaster(pixels, null, rasterMetadata);
	}

	public static GeoRaster createGeoRaster(int[] intPixels,
			RasterMetadata metadata, int imageType, double min, double max) {
		final ImagePlus imagePlus = new ImagePlus("", new ColorProcessor(
				metadata.getNCols(), metadata.getNRows(), intPixels));
		return new DefaultGeoRaster(imagePlus, metadata, imageType, min, max);
	}

	public static GeoRaster createGeoRaster(byte[] bytePixels,
			RasterMetadata metadata, int imageType, double min, double max) {
		final ImagePlus imagePlus = new ImagePlus("", new ByteProcessor(
				metadata.getNCols(), metadata.getNRows(), bytePixels, null));
		return new DefaultGeoRaster(imagePlus, metadata, imageType, min, max);
	}

	public static GeoRaster createGeoRaster(short[] shortPixels,
			RasterMetadata metadata, int imageType, double min, double max) {
		final ImagePlus imagePlus = new ImagePlus("", new ShortProcessor(
				metadata.getNCols(), metadata.getNRows(), shortPixels, null));
		return new DefaultGeoRaster(imagePlus, metadata, imageType, min, max);
	}

	public static GeoRaster createGeoRaster(float[] floatPixels,
			RasterMetadata metadata, int imageType, double min, double max) {
		final ImagePlus imagePlus = new ImagePlus("", new FloatProcessor(
				metadata.getNCols(), metadata.getNRows(), floatPixels, null));
		return new DefaultGeoRaster(imagePlus, metadata, imageType, min, max);
	}
}