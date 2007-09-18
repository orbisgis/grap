package org.grap.model;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.io.FileSaver;
import ij.process.ImageProcessor;

import java.awt.Image;
import java.io.IOException;

import org.grap.io.EsriGRIDReader;
import org.grap.io.WorldFile;
import org.grap.io.WorldImageReader;
import org.grap.lut.LutGenerator;
import org.grap.processing.OperationFactory;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * A GeoRaster object is composed of an ImageJ ImageProcessor object and some
 * spatial fields such as : a projection system, an envelop, a pixel size and,
 * at least, a set of pixels with values that may be Nan...
 */
public class GeoRaster {
	// Un objet georaster est un objet ImageProcessor d'imageJ etendu de
	// caracteristiques
	// spatiales
	// Un georaster est d�fini par :

	// Un systeme de projection
	// Une enveloppe
	// Une résolution planimetrique (pixel size) exprimée en coordonnées
	// géographiques
	// Un ensemble de pixels avec des valeurs qui peuvent etre NaN.

	private ImagePlus imp;

	// public static ImageProcessor ip;

	private RasterMetadata rasterMetadata;

	private String fileNamePrefix;

	private String fileNameExtension;

	private Coordinate pixelsCoords;

	private Coordinate pixelToWorldCoord;

	private String fileName;
	
	OperationFactory opf = new OperationFactory();
	

	public GeoRaster(final String fileName) {
		this.fileName = fileName;

		final int dotIndex = fileName.lastIndexOf('.');
		fileNamePrefix = fileName.substring(0, dotIndex);
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
		readImage(fileName);
		readMetadata(fileName);
	}

	private void readMetadata(final String fileName) {
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

	public ImagePlus readImage(final String fileName) {
		try {
			if (fileNameExtension.toLowerCase().endsWith("asc")) {
				final EsriGRIDReader esriAsciiReader = new EsriGRIDReader(
						fileName);
				imp = esriAsciiReader.getFloatImagePlus();
				// ip = imp.getProcessor();
			} else {
				final WorldImageReader worldImageReader = new WorldImageReader(
						fileName);
				imp = worldImageReader.getImagePlus();
				// ip = imp.getProcessor();
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

	public void setRange(double min, double max) {
		imp.getProcessor().setThreshold(min, max, ImageProcessor.RED_LUT);
		WindowManager.setTempCurrentImage(imp);
		IJ.run("NaN Background");
	}

	public void setNodataValue(final float value) {
		final float[] pixels = (float[]) imp.getProcessor().getPixels();
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] == value)
				pixels[i] = Float.NaN;
		}
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
		fileNamePrefix = dest.substring(0, dotIndex);
		fileNameExtension = dest.substring(dotIndex + 1);
		final FileSaver fileSaver = new FileSaver(imp);

		final String tmp = fileNameExtension.toLowerCase();
		if (tmp.endsWith("tif") || (tmp.endsWith("tiff"))) {
			fileSaver.saveAsTiff(dest);
			WorldFile.save(fileNamePrefix + ".tfw", rasterMetadata);
		} else if (tmp.endsWith("png")) {
			fileSaver.saveAsPng(dest);
			WorldFile.save(fileNamePrefix + ".pgw", rasterMetadata);
		} else if (tmp.endsWith("jpg") || (tmp.endsWith("jpeg"))) {
			fileSaver.saveAsJpeg(dest);
			WorldFile.save(fileNamePrefix + ".jgw", rasterMetadata);
		} else if (tmp.endsWith("gif")) {
			fileSaver.saveAsGif(dest);
			WorldFile.save(fileNamePrefix + ".gfw", rasterMetadata);
		} else if (tmp.endsWith("bmp")) {
			fileSaver.saveAsGif(dest);
			WorldFile.save(fileNamePrefix + ".bpw", rasterMetadata);
		} else {
		}
	}

	public void show() {
		imp.show();
	}

	public void setLUT(String LUTName) {
		imp.getProcessor().setColorModel(LutGenerator.colorModel(LUTName));
	}
}