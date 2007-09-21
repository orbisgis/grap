package org.grap.model;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.io.FileSaver;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.awt.Image;
import java.io.IOException;

import org.grap.io.EsriGRIDReader;
import org.grap.io.WorldFile;
import org.grap.io.WorldImageReader;
import org.grap.lut.LutGenerator;
import org.grap.processing.Operation;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * A GeoRaster object is composed of an ImageJ ImagePlus object and some spatial
 * fields such as : a projection system, an envelop, a pixel size...
 */
public class GeoRaster {
	private ImagePlus imp;

	private RasterMetadata rasterMetadata;

	private String fileNameExtension;

	private Coordinate pixelsCoords;

	private Coordinate pixelToWorldCoord;

	private String fileName;

	public GeoRaster(final String fileName) {
		this.fileName = fileName;

		final int dotIndex = fileName.lastIndexOf('.');
		fileNameExtension = fileName.substring(dotIndex + 1);
	}

	public GeoRaster(final ImagePlus impResult, final RasterMetadata metadata) {
		this.imp = impResult;
		this.rasterMetadata = metadata;
	}

	public GeoRaster(final Image image, final RasterMetadata metadata) {
		this(new ImagePlus("", image), metadata);
	}

	public void open() {
		readImage();
		readMetadata();
	}

	private void readMetadata() {
		try {
			if (fileNameExtension.toLowerCase().endsWith("asc")) {
				final EsriGRIDReader esriAsciiReader = new EsriGRIDReader(
						fileName);
				rasterMetadata = esriAsciiReader.readHead();
			} else {
				final WorldImageReader worldImageReader = new WorldImageReader(
						fileName);
				rasterMetadata = worldImageReader.getRasterMetadata();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ImagePlus readImage() {
		try {
			if (fileNameExtension.toLowerCase().endsWith("asc")) {
				final EsriGRIDReader esriAsciiReader = new EsriGRIDReader(
						fileName);
				imp = esriAsciiReader.getFloatImagePlus();
			} else {
				final WorldImageReader worldImageReader = new WorldImageReader(
						fileName);
				imp = worldImageReader.getImagePlus();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imp;
	}

	// We use this method because it's more faster than read also
	// image + metadata. Just read here metadata.

	public RasterMetadata getMetadata() {
		return rasterMetadata;
	}

	public ImagePlus getImagePlus() {
		return imp;
	}

	/**
	 * 
	 * @param nodatavalue
	 * @param imp
	 */

	public void setRange(final double min, final double max) {
		imp.getProcessor().setThreshold(min, max, ImageProcessor.RED_LUT);
		WindowManager.setTempCurrentImage(imp);
		IJ.run("NaN Background");
	}

	public void setNodataValue(final float value) {
		final float[] pixels = (float[]) imp.getProcessor().getPixels();
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] == value) {
				pixels[i] = Float.NaN;
			}
		}
		imp = new ImagePlus("", new FloatProcessor(getMetadata().getNCols(),
				getMetadata().getNRows(), pixels, imp.getProcessor()
						.getColorModel()));
	}

	public Coordinate pixelToWorldCoord(final int xpixel, final int ypixel) {
		final double xWorld = ((xpixel * rasterMetadata.getPixelSize_X()) + rasterMetadata
				.getXllcorner());
		final double yWorld = ((ypixel * rasterMetadata.getPixelSize_Y()) + rasterMetadata
				.getYllcorner());

		pixelToWorldCoord = new Coordinate(xWorld, yWorld);
		return pixelToWorldCoord;
	}

	public Coordinate getPixelCoords(final double mouseX, final double mouseY) {
		final int x = new Double((mouseX - rasterMetadata.getXllcorner())
				/ rasterMetadata.getPixelSize_X()).intValue();
		final int y = new Double((mouseY - rasterMetadata.getYllcorner())
				/ rasterMetadata.getPixelSize_Y()).intValue();
		pixelsCoords = new Coordinate(Math.abs(x), Math.abs(y));

		return pixelsCoords;
	}

	public void save(String dest) throws IOException {
		final int dotIndex = dest.lastIndexOf('.');
		final String localFileNamePrefix = dest.substring(0, dotIndex);
		final String localFileNameExtension = dest.substring(dotIndex + 1);
		final FileSaver fileSaver = new FileSaver(imp);

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
			throw new RuntimeException("Unknown file name extension : "
					+ localFileNameExtension);
		}
	}

	public void show() {
		imp.show();
	}

	public void setLUT(String LUTName) {
		imp.getProcessor().setColorModel(LutGenerator.colorModel(LUTName));
	}

	public GeoRaster doOperation(final Operation operation) {
		return operation.execute(this);
	}

	public int getType() {
		// ImagePlus.COLOR_256, ImagePlus.COLOR_RGB, ImagePlus.GRAY8,
		// ImagePlus.GRAY16, ImagePlus.GRAY32
		return imp.getType();
	}
}