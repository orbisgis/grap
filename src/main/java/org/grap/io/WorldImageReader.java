package org.grap.io;

import ij.ImagePlus;
import ij.io.Opener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;

public class WorldImageReader implements FileReader {
	private String fileName;

	private RasterMetadata rasterMetadata;

	private ImagePlus imagePlus;

	private static Map<String, String[]> worldFileExtensions;

	private File worldFile;

	static {
		worldFileExtensions = new HashMap<String, String[]>();
		worldFileExtensions.put("tif", new String[] { "tfw" });
		worldFileExtensions.put("tiff", new String[] { "tfw", "tiffw" });
		worldFileExtensions.put("jpg", new String[] { "jpw", "jgw", "jpgw",
				"jpegw" });
		worldFileExtensions.put("jpeg", new String[] { "jpw", "jgw", "jpgw",
				"jpegw" });
		worldFileExtensions.put("gif", new String[] { "gfw", "gifw" });
		worldFileExtensions.put("bmp", new String[] { "bmw", "bmpw" });
		worldFileExtensions.put("png", new String[] { "pgw", "pngw" });
	}

	// constructor
	public WorldImageReader(final String fileName) {
		this.fileName = fileName;
	}

	// private methods
	private boolean isThereAnyWorldFile(final String fileNamePrefix,
			final String fileNameExtension) throws IOException {
		worldFile = null;
		for (String extension : worldFileExtensions.get(fileNameExtension
				.toLowerCase())) {
			if (new File(fileNamePrefix + "." + extension).exists()) {
				worldFile = new File(fileNamePrefix + "." + extension);
				return true;
			} else if (new File(fileNamePrefix + "." + extension.toUpperCase())
					.exists()) {
				worldFile = new File(fileNamePrefix + "."
						+ extension.toUpperCase());
				return true;
			}
		}
		return false;
	}

	// public methods
	public RasterMetadata getRasterMetadata() throws IOException {
		if (null == rasterMetadata) {
			final int dotIndex = fileName.lastIndexOf('.');
			final String fileNamePrefix = fileName.substring(0, dotIndex);
			final String fileNameExtension = fileName.substring(dotIndex + 1);

			if (isThereAnyWorldFile(fileNamePrefix, fileNameExtension) == true) {
				final WorldFile wf = WorldFile.read(worldFile);

				rasterMetadata = new RasterMetadata();
				rasterMetadata.setXOrigin(wf.getXUpperLeft());
				rasterMetadata.setYOrigin(wf.getYUpperLeft());
				rasterMetadata.setPixelSize_X(wf.getXSize());
				rasterMetadata.setPixelSize_Y(Math.abs(wf.getYSize()));
				rasterMetadata.setXRotation(wf.getColRotation());
				rasterMetadata.setYRotation(wf.getRowRotation());

				imagePlus = new Opener().openImage(fileName);

				rasterMetadata.setNCols(imagePlus.getWidth());
				rasterMetadata.setNRows(imagePlus.getHeight());
				rasterMetadata.computeEnvelope();
			}
		}
		return rasterMetadata;
	}

	public GeoRaster read() throws IOException {
		getRasterMetadata();
		return new GeoRaster(imagePlus, rasterMetadata);
	}
}