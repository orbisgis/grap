package org.grap.io;

import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.Opener;
import ij.io.TiffDecoder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.grap.model.RasterMetadata;

public class WorldImageReader implements FileReader {
	private static Map<String, String[]> worldFileExtensions;

	private String fileName;

	private File worldFile;

	private boolean isTiff = false;

	private String fileNamePrefix;

	private String fileNameExtension;

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

		final int dotIndex = fileName.lastIndexOf('.');
		fileNamePrefix = fileName.substring(0, dotIndex);
		fileNameExtension = fileName.substring(dotIndex + 1).toLowerCase();

		if (fileNameExtension.equals("tif") || fileNameExtension.equals("tiff")) {
			isTiff = true;
		}
	}

	// private method
	private boolean isThereAnyWorldFile() throws IOException {
		worldFile = null;

		for (String extension : worldFileExtensions.get(fileNameExtension)) {
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
	public RasterMetadata readRasterMetadata() throws IOException,
			GeoreferencingException {
		final File file = new File(fileName);
		final InputStream inputStream = new BufferedInputStream(
				new FileInputStream(file));

		// read image's dimensions
		int ncols;
		int nrows;
		if (isTiff) {
			final TiffDecoder tiffDecoder = new TiffDecoder(inputStream,
					fileName);
			final FileInfo[] fileInfo = tiffDecoder.getTiffInfo();
			ncols = fileInfo[0].width;
			nrows = fileInfo[0].height;
		} else {
			final ImageInfo imageInfo = new ImageInfo();
			imageInfo.setInput(inputStream);
			if (imageInfo.check()) {
				ncols = imageInfo.getWidth();
				nrows = imageInfo.getHeight();
			} else {
				throw new RuntimeException("Unsupported image file format.");
			}
		}

		// read other image's metadata
		double xOrigin = 0;
		double yOrigin = 0;
		float xSize = 1;
		float ySize = 1;
		double xRotation = 0;
		double yRotation = 0;
		if (isThereAnyWorldFile() == true) {
			final WorldFile wf = WorldFile.read(worldFile);

			xOrigin = wf.getXUpperLeft();
			yOrigin = wf.getYUpperLeft();
			xSize = wf.getXSize();
			ySize = wf.getYSize();
			xRotation = wf.getColRotation();
			yRotation = wf.getRowRotation();
		} else {
			throw new GeoreferencingException("Could not find world file for "
					+ fileName);
		}
		final RasterMetadata rasterMetadata = new RasterMetadata(xOrigin,
				yOrigin, xSize, ySize, ncols, nrows, xRotation, yRotation);
		return rasterMetadata;
	}

	public ImagePlus readImagePlus() throws IOException {
		return new Opener().openImage(fileName);
	}
}