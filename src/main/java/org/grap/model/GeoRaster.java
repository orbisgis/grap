package org.grap.model;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.io.FileSaver;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.awt.Image;
import java.io.IOException;

import org.grap.io.WorldFile;
import org.grap.lut.LutGenerator;
import org.grap.processing.Operation;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * A GeoRaster object is composed of an ImageJ ImagePlus object and some spatial
 * fields such as : a projection system, an envelop, a pixel size...
 */
public class GeoRaster {
	private ImagePlus imagePlus;

	private RasterMetadata rasterMetadata;

	// constructors
	public GeoRaster(final ImagePlus imagePlus,
			final RasterMetadata rasterMetadata) {
		this.imagePlus = imagePlus;
		this.rasterMetadata = rasterMetadata;
	}

	public GeoRaster(final Image image, final RasterMetadata rasterMetadata) {
		this(new ImagePlus("", image), rasterMetadata);
	}

	// getters
	public RasterMetadata getRasterMetadata() {
		return rasterMetadata;
	}

	public ImagePlus getImagePlus() {
		return imagePlus;
	}

	// public methods
	public void setRange(final double min, final double max) {
		imagePlus.getProcessor().setThreshold(min, max, ImageProcessor.RED_LUT);
		WindowManager.setTempCurrentImage(imagePlus);
		IJ.run("NaN Background");
	}

	public void setNodataValue(final float value) {
		final float[] pixels = (float[]) imagePlus.getProcessor().getPixels();
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] == value) {
				pixels[i] = Float.NaN;
			}
		}
		imagePlus = new ImagePlus("", new FloatProcessor(getRasterMetadata()
				.getNCols(), getRasterMetadata().getNRows(), pixels, imagePlus
				.getProcessor().getColorModel()));
	}

	public Coordinate pixelToWorldCoord(final int xpixel, final int ypixel) {
		final double xWorld = ((xpixel * rasterMetadata.getPixelSize_X()) + rasterMetadata
				.getXllcorner());
		final double yWorld = ((ypixel * rasterMetadata.getPixelSize_Y()) + rasterMetadata
				.getYllcorner());
		return new Coordinate(xWorld, yWorld);
	}

	public Coordinate getPixelCoords(final double mouseX, final double mouseY) {
		final int x = new Double((mouseX - rasterMetadata.getXllcorner())
				/ rasterMetadata.getPixelSize_X()).intValue();
		final int y = new Double((mouseY - rasterMetadata.getYllcorner())
				/ rasterMetadata.getPixelSize_Y()).intValue();
		return new Coordinate(Math.abs(x), Math.abs(y));
	}

	public void save(final String dest) throws IOException {
		final int dotIndex = dest.lastIndexOf('.');
		final String localFileNamePrefix = dest.substring(0, dotIndex);
		final String localFileNameExtension = dest.substring(dotIndex + 1);
		final FileSaver fileSaver = new FileSaver(imagePlus);

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
		imagePlus.show();
	}

	public void setLUT(final String LUTName) {
		imagePlus.getProcessor()
				.setColorModel(LutGenerator.colorModel(LUTName));
	}

	public GeoRaster doOperation(final Operation operation) {
		return operation.execute(this);
	}

	public int getType() {
		// ImagePlus.COLOR_256, ImagePlus.COLOR_RGB, ImagePlus.GRAY8,
		// ImagePlus.GRAY16, ImagePlus.GRAY32
		return imagePlus.getType();
	}
}