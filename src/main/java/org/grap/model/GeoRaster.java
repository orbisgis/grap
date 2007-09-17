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

import com.vividsolutions.jts.geom.Coordinate;

/**
 * A GeoRaster object is composed of an ImageJ ImageProcessor object and some
 * spatial fields such as : - a projection system, - an envelop, - a pixel size,
 * 
 * and, at least, a set of pixels with values that may be Nan...
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

	public static ImagePlus imp;

	// public static ImageProcessor ip;

	private static RasterMetadata rasterMetadata;

	static int dotIndex;

	static String fileNamePrefix;

	static String fileNameExtension;

	private static Coordinate pixelsCoords;

	private static Coordinate pixelToWorldCoord;

	private String fileName;

	public GeoRaster(String fileName) {
		this.fileName = fileName;

		dotIndex = fileName.lastIndexOf('.');
		fileNamePrefix = fileName.substring(0, dotIndex);
		fileNameExtension = fileName.substring(dotIndex + 1);

	}

	public GeoRaster(ImagePlus impResult, RasterMetadata metadata) {
		this.imp = impResult;
		this.rasterMetadata = metadata;
	}

	public GeoRaster(Image image, RasterMetadata metadata) {
		this.imp = new ImagePlus("", image);
		this.rasterMetadata = metadata;
	}

	public void open() {

		ReadImage(fileName);
		ReadMetadata(fileName);

	}

	private void ReadMetadata(String fileName) {

		try {
			if (fileNameExtension.toLowerCase().endsWith("asc")) {

				EsriGRIDReader esriAsciiReader = new EsriGRIDReader(fileName);

				rasterMetadata = esriAsciiReader.readHead();
			} else {

				WorldImageReader worldImageReader = new WorldImageReader(
						fileName);

				rasterMetadata = worldImageReader.getRasterMetadata();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static ImagePlus ReadImage(String fileName) {

		try {

			if (fileNameExtension.toLowerCase().endsWith("asc")) {

				EsriGRIDReader esriAsciiReader = new EsriGRIDReader(fileName);

				imp = esriAsciiReader.getFloatImagePlus();

				// ip = imp.getProcessor();

			} else {

				WorldImageReader worldImageReader = new WorldImageReader(
						fileName);
				imp = worldImageReader.getImagePlus();

				// ip = imp.getProcessor();

			}
		}

		catch (IOException e) {
			// TODO Auto-generated catch block
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

	public void setNodataValue(float value) {

		float[] pixels = (float[]) imp.getProcessor().getPixels();
		for (int i = 0; i < pixels.length; i++)
			if (pixels[i] == value)
				pixels[i] = Float.NaN;

	}

	public static Coordinate pixelToWorldCoord(final int xpixel,
			final int ypixel) {

		double xWorld = ((xpixel * rasterMetadata.pixelSize_X) + rasterMetadata.xllcorner);
		double yWorld = ((ypixel * rasterMetadata.pixelSize_Y) + rasterMetadata.yllcorner);

		pixelToWorldCoord = new Coordinate(xWorld, yWorld);
		return pixelToWorldCoord;

	}

	public static Coordinate getPixelCoords(final double mouseX,
			final double mouseY) {
		int x = new Double((mouseX - rasterMetadata.xllcorner)
				/ rasterMetadata.pixelSize_X).intValue();
		int y = new Double((mouseY - rasterMetadata.yllcorner)
				/ rasterMetadata.pixelSize_Y).intValue();
		pixelsCoords = new Coordinate(Math.abs(x), Math.abs(y));

		return pixelsCoords;
	}

	public void save(String dest) throws IOException {

		dotIndex = dest.lastIndexOf('.');
		fileNamePrefix = dest.substring(0, dotIndex);
		fileNameExtension = dest.substring(dotIndex + 1);
		FileSaver fileSaver = new FileSaver(imp);

		if (fileNameExtension.toLowerCase().endsWith("tif")
				|| (fileNameExtension.toLowerCase().endsWith("tiff"))) {

			fileSaver.saveAsTiff(dest);

			WorldFile.save(fileNamePrefix + ".tfw", rasterMetadata);

		}

		else if (fileNameExtension.toLowerCase().endsWith("png")) {

			fileSaver.saveAsPng(dest);
			WorldFile.save(fileNamePrefix + ".pgw", rasterMetadata);

		}

		else if (fileNameExtension.toLowerCase().endsWith("jpg")
				|| (fileNameExtension.toLowerCase().endsWith("jpeg"))) {

			fileSaver.saveAsJpeg(dest);

			WorldFile.save(fileNamePrefix + ".jgw", rasterMetadata);

		}

		else if (fileNameExtension.toLowerCase().endsWith("gif")) {

			fileSaver.saveAsGif(dest);

			WorldFile.save(fileNamePrefix + ".gfw", rasterMetadata);

		}

		else if (fileNameExtension.toLowerCase().endsWith("bmp")) {

			fileSaver.saveAsGif(dest);

			WorldFile.save(fileNamePrefix + ".bpw", rasterMetadata);

		}

		else {

		}

	}

	public void show() {
		imp.show();
	}

	public void setLUT(String LUTName) {

		imp.getProcessor().setColorModel(LutGenerator.colorModel(LUTName));

	}

}
