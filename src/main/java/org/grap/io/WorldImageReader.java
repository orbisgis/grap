package org.grap.io;

import ij.ImagePlus;
import ij.io.Opener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.grap.model.RasterMetadata;

public class WorldImageReader {
	private String fileName;

	private RasterMetadata rasterMetadata = new RasterMetadata(0.0F, 0.0F,
			0.0F, 0.0F, 0);

	private ImagePlus imp;

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

	public WorldImageReader(final String fileName) {
		this.fileName = fileName;
		readFile(fileName);
	}

	public RasterMetadata readFile(final String fileName) {
		final int dotIndex = fileName.lastIndexOf('.');
		final String fileNamePrefix = fileName.substring(0, dotIndex);
		final String fileNameExtension = fileName.substring(dotIndex + 1);

		try {
			if (isThereAnyWorldFile(fileNamePrefix, fileNameExtension) == true) {
				final WorldFile wf = WorldFile.read(worldFile);

				rasterMetadata.setXOrigin(wf.getXUpperLeft());
				rasterMetadata.setYOrigin(wf.getYUpperLeft());
				rasterMetadata.setPixelSize_X(wf.getXSize());
				rasterMetadata.setPixelSize_Y(Math.abs(wf.getYSize()));
				rasterMetadata.setXRotation(wf.getColRotation());
				rasterMetadata.setYRotation(wf.getRowRotation());

				final Opener opener = new Opener();
				imp = opener.openImage(fileName);

				rasterMetadata.setNCols(imp.getWidth());
				rasterMetadata.setNRows(imp.getHeight());
				rasterMetadata.computeEnvelope();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rasterMetadata;
	}

	public boolean isThereAnyWorldFile(final String fileNamePrefix,
			final String fileNameExtension) throws IOException {
		boolean worldfileExist = false;
		worldFile = null;

		for (String extension : worldFileExtensions.get(fileNameExtension
				.toLowerCase())) {
			if (new File(fileNamePrefix + "." + extension).exists()) {
				worldFile = new File(fileNamePrefix + "." + extension);
				worldfileExist = true;
			} else if (new File(fileNamePrefix + "." + extension.toUpperCase())
					.exists()) {
				worldFile = new File(fileNamePrefix + "."
						+ extension.toUpperCase());
				worldfileExist = true;
			}
		}
		return worldfileExist;
	}

	public ImagePlus getImagePlus() {
		if (imp == null) {
			readFile(fileName);
		}
		return imp;
	}

	public RasterMetadata getRasterMetadata() {
		return rasterMetadata;
	}
}