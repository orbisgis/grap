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

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.PolygonRoi;
import ij.io.FileSaver;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.grap.io.FileReader;
import org.grap.io.FileReaderFactory;
import org.grap.io.GeoreferencingException;
import org.grap.io.WorldFile;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.utilities.EnvelopeUtil;
import org.grap.utilities.JTSConverter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

/**
 * A GeoRaster object is composed of an ImageJ ImagePlus object and some spatial
 * fields such as : a projection system, an envelop, a pixel size...
 */
public class DefaultGeoRaster implements GeoRaster {
	private RasterMetadata rasterMetadata;
	private FileReader fileReader;
	private CachedValues cachedValues;
	private GrapImagePlus cachedGrapImagePlus;

	// internal class
	private class CachedValues {
		private Double maxThreshold;
		private Double minThreshold;
		private ColorModel colorModel;
		private int type;
		private double min;
		private double max;
		private int width;
		private int height;
	}

	// constructors
	DefaultGeoRaster(final String fileName) throws FileNotFoundException,
			IOException {
		this(fileName, GeoProcessorType.FLOAT);
	}

	DefaultGeoRaster(final String fileName,
			final GeoProcessorType geoProcessorType)
			throws FileNotFoundException, IOException {
		fileReader = FileReaderFactory.create(fileName, geoProcessorType);
	}

	DefaultGeoRaster(final String fileName,
			final GeoProcessorType geoProcessorType, float pixelsize)
			throws FileNotFoundException, IOException {
		fileReader = FileReaderFactory.create(fileName, geoProcessorType,
				pixelsize);
	}

	DefaultGeoRaster(final ImagePlus imagePlus, final RasterMetadata metadata) {
		cachedGrapImagePlus = new GrapImagePlus("", imagePlus.getProcessor());
		this.rasterMetadata = metadata;
	}

	// public methods
	public void open() throws GeoreferencingException, IOException {
		if (null != fileReader) {
			rasterMetadata = fileReader.readRasterMetadata();
		} else {
			// Ignore open for results in memory
		}
	}

	public RasterMetadata getMetadata() {
		return rasterMetadata;
	}

	public void setRangeValues(final double min, final double max)
			throws IOException, GeoreferencingException {
		getCachedValues(null).minThreshold = min;
		getCachedValues(null).maxThreshold = max;
	}

	public void setRangeColors(final double[] ranges, final Color[] colors)
			throws OperationException, IOException, GeoreferencingException {
		checkRangeColors(ranges, colors);

		// TODO : is it really necessary ?
		setRangeValues(ranges[0], ranges[ranges.length - 1]);

		final int nbOfColors = 256;
		final byte[] reds = new byte[nbOfColors];
		final byte[] greens = new byte[nbOfColors];
		final byte[] blues = new byte[nbOfColors];
		final byte[] alpha = new byte[nbOfColors];
		final double delta = (ranges[ranges.length - 1] - ranges[0])
				/ (nbOfColors - 1);
		double x = ranges[0] + delta;

		for (int i = 1, j = 0; i < nbOfColors; i++, x += delta) {
			while (!((x >= ranges[j]) && (x < ranges[j + 1]))
					&& (colors.length > j + 1)) {
				j++;
			}
			reds[i] = (byte) colors[j].getRed();
			greens[i] = (byte) colors[j].getGreen();
			blues[i] = (byte) colors[j].getBlue();
			alpha[i] = 1;
		}
		// default color for NaN pixels :
		reds[0] = (byte) Color.BLACK.getRed();
		greens[0] = (byte) Color.BLACK.getGreen();
		blues[0] = (byte) Color.BLACK.getBlue();
		alpha[0] = 0;

		try {
			setLUT(new IndexColorModel(8, nbOfColors, reds, greens, blues,
					alpha));
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	public void setNodataValue(final float value) {
		rasterMetadata.setNoData(value);
	}

	public Point2D pixelToWorldCoord(final int xpixel, final int ypixel) {
		return rasterMetadata.toWorld(xpixel, ypixel);
	}

	public Point2D getPixelCoords(final double mouseX, final double mouseY) {
		return rasterMetadata.toPixel(mouseX, mouseY);
	}

	public void save(final String dest) throws IOException,
			GeoreferencingException {
		final int dotIndex = dest.lastIndexOf('.');
		final String localFileNamePrefix = dest.substring(0, dotIndex);
		final String localFileNameExtension = dest.substring(dotIndex + 1);
		final FileSaver fileSaver = new FileSaver(getGrapImagePlus());

		final String tmp = localFileNameExtension.toLowerCase();
		if (tmp.endsWith("tif") || (tmp.endsWith("tiff"))) {
			fileSaver.saveAsTiff(dest);
			WorldFile.save(localFileNamePrefix + ".tfw", rasterMetadata);
		} else if (tmp.endsWith("png")) {
			fileSaver.saveAsPng(dest);
			WorldFile.save(localFileNamePrefix + ".pgw", rasterMetadata);
		} else if (tmp.endsWith("jpg") || (tmp.endsWith("jpeg"))) {
			fileSaver.saveAsJpeg(dest);
			WorldFile.save(localFileNamePrefix + ".jgw", rasterMetadata);
		} else if (tmp.endsWith("gif")) {
			fileSaver.saveAsGif(dest);
			WorldFile.save(localFileNamePrefix + ".gfw", rasterMetadata);
		} else if (tmp.endsWith("bmp")) {
			fileSaver.saveAsGif(dest);
			WorldFile.save(localFileNamePrefix + ".bpw", rasterMetadata);
		} else {
			throw new RuntimeException("Cannot write in format: "
					+ localFileNameExtension);
		}
	}

	public void show() throws IOException, GeoreferencingException {
		getGrapImagePlus().show();
	}

	public void setLUT(final ColorModel colorModel) throws IOException,
			GeoreferencingException {
		getCachedValues(null).colorModel = colorModel;
	}

	public GeoRaster doOperation(final Operation operation)
			throws OperationException, GeoreferencingException {
		return operation.execute(this);
	}

	public int getType() throws IOException, GeoreferencingException {
		return getCachedValues(null).type;
	}

	public boolean isEmpty() {
		return false;
	}

	public GeoRaster convolve(final float[] kernel, final int kernelWidth,
			final int kernelHeight) throws OperationException,
			GeoreferencingException {
		try {
			final ImageProcessor dup = getGrapImagePlus().getProcessor()
					.duplicate();
			dup.convolve(kernel, kernelWidth, kernelHeight);
			return createGeoRaster(dup, rasterMetadata.duplicate());
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	public GeoRaster convolve3x3(final int[] kernel) throws OperationException,
			GeoreferencingException {
		try {
			final ImageProcessor dup = getGrapImagePlus().getProcessor()
					.duplicate();
			dup.convolve3x3(kernel);
			return createGeoRaster(dup, rasterMetadata.duplicate());
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private GeoRaster createGeoRaster(final ImageProcessor dup,
			final RasterMetadata rasterMetadata) throws IOException,
			GeoreferencingException {
		final int width = dup.getWidth();
		final int height = dup.getHeight();
		final ColorModel cm = dup.getColorModel();
		final int type = getType();
		switch (type) {
		case ImagePlus.GRAY8:
		case ImagePlus.COLOR_256:
			final byte[] bytePixels = (byte[]) dup.getPixels();
			return GeoRasterFactory.createGeoRaster(bytePixels, width, height,
					cm, rasterMetadata);
		case ImagePlus.GRAY16:
			final short[] shortPixels = (short[]) dup.getPixels();
			return GeoRasterFactory.createGeoRaster(shortPixels, width, height,
					cm, rasterMetadata);
		case ImagePlus.GRAY32:
		case ImagePlus.COLOR_RGB:
			final float[] floatPixels = (float[]) dup.getPixels();
			return GeoRasterFactory.createGeoRaster(floatPixels, width, height,
					cm, rasterMetadata);
		default:
			throw new IllegalStateException("Unknown type: " + type);
		}
	}

	private LinearRing toPixel(final LinearRing ring) {
		final Coordinate[] coords = ring.getCoordinates();
		final Coordinate[] transformedCoords = new Coordinate[coords.length];
		for (int i = 0; i < transformedCoords.length; i++) {
			final Point2D p = rasterMetadata.toPixel(coords[i].x, coords[i].y);
			transformedCoords[i] = new Coordinate(p.getX(), p.getY());
		}

		return new GeometryFactory().createLinearRing(transformedCoords);
	}

	public GeoRaster crop(final LinearRing ring) throws OperationException,
			GeoreferencingException {
		try {
			final Geometry rasterEnvelope = new GeometryFactory()
					.createPolygon((LinearRing) EnvelopeUtil
							.toGeometry(rasterMetadata.getEnvelope()), null);

			if (rasterEnvelope.intersects(ring)) {
				final PolygonRoi roi = JTSConverter.toPolygonRoi(toPixel(ring));

				final ImageProcessor processor = getGrapImagePlus()
						.getProcessor();
				processor.setRoi(roi);
				final ImageProcessor result = processor.crop();

				Envelope newEnvelope = toWorld(roi.getBoundingRect());

				final double originX = newEnvelope.getMinX();
				final double originY = newEnvelope.getMaxY();
				final RasterMetadata metadataResult = new RasterMetadata(
						originX, originY, rasterMetadata.getPixelSize_X(),
						rasterMetadata.getPixelSize_Y(), result.getWidth(),
						result.getHeight(), rasterMetadata.getRotation_X(),
						rasterMetadata.getRotation_Y());

				return createGeoRaster(result, metadataResult);
			} else {
				return GeoRasterFactory.createNullGeoRaster();
			}
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	public GeoRaster crop(final Rectangle2D roi) throws OperationException,
			GeoreferencingException {
		try {
			final Envelope roiEnv = new Envelope(new Coordinate(roi.getMinX(),
					roi.getMinY()),
					new Coordinate(roi.getMaxX(), roi.getMaxY()));
			if (roiEnv.intersects(rasterMetadata.getEnvelope())) {

				final Rectangle2D pixelRoi = getRectangleInPixels(roi);
				final ImageProcessor processor = getGrapImagePlus()
						.getProcessor();
				processor.setRoi((int) pixelRoi.getMinX(), (int) pixelRoi
						.getMinY(), (int) pixelRoi.getWidth(), (int) pixelRoi
						.getHeight());
				final ImageProcessor result = processor.crop();

				final Envelope newEnvelope = toWorld(pixelRoi);
				final double originX = newEnvelope.getMinX();
				final double originY = newEnvelope.getMaxY();

				final RasterMetadata metadataResult = new RasterMetadata(
						originX, originY, rasterMetadata.getPixelSize_X(),
						rasterMetadata.getPixelSize_Y(), result.getWidth(),
						result.getHeight(), rasterMetadata.getRotation_X(),
						rasterMetadata.getRotation_Y());

				return createGeoRaster(result, metadataResult);
			} else {
				return GeoRasterFactory.createNullGeoRaster();
			}
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private Rectangle2D getRectangleInPixels(final Rectangle2D rectangle) {
		final Point2D min = getPixelCoords(rectangle.getMinX(), rectangle
				.getMinY());
		final Point2D max = getPixelCoords(rectangle.getMaxX(), rectangle
				.getMaxY());
		final int minx = (int) Math.min(min.getX(), max.getX());
		final int maxx = (int) Math
				.ceil(Math.max(min.getX(), max.getX()) + 0.5);
		final int miny = (int) Math.min(min.getY(), max.getY());
		final int maxy = (int) Math
				.ceil(Math.max(min.getY(), max.getY()) + 0.5);
		return new Rectangle(minx, miny, maxx - minx, maxy - miny);
	}

	private Envelope toWorld(Rectangle2D rectangle) {
		final Point2D min = getMetadata().toWorld((int) rectangle.getMinX(),
				(int) rectangle.getMinY());
		final Point2D max = getMetadata().toWorld((int) rectangle.getMaxX(),
				(int) rectangle.getMaxY());
		final double minx = Math.min(min.getX(), max.getX());
		final double maxx = Math.max(min.getX(), max.getX());
		final double miny = Math.min(min.getY(), max.getY());
		final double maxy = Math.max(min.getY(), max.getY());
		return new Envelope(new Coordinate(minx, miny), new Coordinate(maxx,
				maxy));
	}

	public GeoRaster erode() throws OperationException, GeoreferencingException {
		try {
			final ImageProcessor dup = getGrapImagePlus().getProcessor()
					.duplicate();
			dup.erode();
			return createGeoRaster(dup, rasterMetadata.duplicate());
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	public GeoRaster smooth() throws OperationException,
			GeoreferencingException {
		try {
			final ImageProcessor dup = getGrapImagePlus().getProcessor()
					.duplicate();
			dup.smooth();
			return createGeoRaster(dup, rasterMetadata.duplicate());
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	public double getMax() throws IOException, GeoreferencingException {
		return getCachedValues(null).max;
	}

	public double getMin() throws IOException, GeoreferencingException {
		return getCachedValues(null).min;
	}

	public int getHeight() throws IOException, GeoreferencingException {
		return getCachedValues(null).height;
	}

	public int getWidth() throws IOException, GeoreferencingException {
		return getCachedValues(null).width;
	}

	public ColorModel getColorModel() throws IOException,
			GeoreferencingException {
		return getCachedValues(null).colorModel;
	}

	public GrapImagePlus getGrapImagePlus() throws IOException,
			GeoreferencingException {
		final GrapImagePlus grapImagePlus = (null == cachedGrapImagePlus) ? fileReader
				.readGrapImagePlus()
				: cachedGrapImagePlus;
		getCachedValues(grapImagePlus);
		if (null != rasterMetadata) {
			grapImagePlus.setNoDataValue(rasterMetadata.getNoDataValue());
		}
		if (null != getCachedValues(null).colorModel) {
			grapImagePlus.getProcessor().setColorModel(
					getCachedValues(null).colorModel);
		}
		grapImagePlus.setGrapType(getCachedValues(null).type);

		if ((ImagePlus.COLOR_RGB != getType())
				&& (null != getCachedValues(null).minThreshold)
				&& (null != getCachedValues(null).maxThreshold)) {
			grapImagePlus.getProcessor().setThreshold(
					getCachedValues(null).minThreshold,
					getCachedValues(null).maxThreshold,
					ImageProcessor.NO_LUT_UPDATE);
			WindowManager.setTempCurrentImage(grapImagePlus);
			IJ.run("NaN Background");
		}
		return grapImagePlus;
	}

	// private method
	private CachedValues getCachedValues(ImagePlus img) throws IOException,
			GeoreferencingException {
		if (null == cachedValues) {
			cachedValues = new CachedValues();
			ImagePlus ip = img;
			if (ip == null) {
				ip = getGrapImagePlus();
			}
			final ImageProcessor processor = ip.getProcessor();
			final IndexColorModel cm = (IndexColorModel) processor
					.getColorModel();
			byte[] reds = new byte[256];
			byte[] greens = new byte[256];
			byte[] blues = new byte[256];
			byte[] alphas = new byte[256];
			cm.getReds(reds);
			cm.getGreens(greens);
			cm.getBlues(blues);
			cm.getAlphas(alphas);
			alphas[0] = 0;
			cachedValues.colorModel = new IndexColorModel(8, 256, reds, greens,
					blues, alphas);
			// cachedValues.colorModel = processor.getColorModel();

			cachedValues.min = processor.getMin();
			cachedValues.max = processor.getMax();
			cachedValues.height = ip.getHeight();
			cachedValues.width = ip.getWidth();
			cachedValues.type = ip.getType();
			cachedValues.minThreshold = null;
			cachedValues.maxThreshold = null;
		}
		return cachedValues;
	}

	private void checkRangeColors(final double[] ranges, final Color[] colors)
			throws OperationException {
		if (ranges.length != colors.length + 1) {
			throw new OperationException(
					"Ranges.length not equal to Colors.length + 1 !");
		}
		for (int i = 1; i < ranges.length; i++) {
			if (ranges[i - 1] > ranges[i]) {
				throw new OperationException(
						"Ranges array needs to be sorted !");
			}
		}
		if (colors.length > 256) {
			throw new OperationException(
					"Colors.length must be less than 256 !");
		}
	}
}