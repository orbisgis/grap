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
import ij.io.FileSaver;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.geom.Point2D;
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

	public Point2D fromPixelGridCoordToRealWorldCoord(final int xpixel, final int ypixel) {
		return rasterMetadata.toWorld(xpixel, ypixel);
	}

	public Point2D fromRealWorldCoordToPixelGridCoord(final double mouseX, final double mouseY) {
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
			/*
			 * TODO I comment this because this doesn't work. After solving the
			 * bug of transparencies this must be removed final IndexColorModel
			 * cm = (IndexColorModel) processor .getColorModel(); byte[] reds =
			 * new byte[256]; byte[] greens = new byte[256]; byte[] blues = new
			 * byte[256]; byte[] alphas = new byte[256]; cm.getReds(reds);
			 * cm.getGreens(greens); cm.getBlues(blues); cm.getAlphas(alphas);
			 * alphas[0] = 0; cachedValues.colorModel = new IndexColorModel(8,
			 * 256, reds, greens, blues, alphas);
			 */
			cachedValues.colorModel = processor.getColorModel();

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