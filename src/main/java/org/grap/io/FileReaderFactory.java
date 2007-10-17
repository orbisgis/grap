package org.grap.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.grap.model.GeoProcessorType;

public class FileReaderFactory {
	private static Set<String> worldFileExtensions;
	static {
		worldFileExtensions = new HashSet<String>();
		worldFileExtensions.add("tif");
		worldFileExtensions.add("tiff");
		worldFileExtensions.add("jpg");
		worldFileExtensions.add("jpeg");
		worldFileExtensions.add("gif");
		worldFileExtensions.add("bmp");
		worldFileExtensions.add("png");
	}

	public static FileReader create(final String fileName)
			throws FileNotFoundException, IOException {
		return create(fileName, GeoProcessorType.FLOAT);
	}

	public static FileReader create(final String fileName,
			final GeoProcessorType geoProcessorType)
			throws FileNotFoundException, IOException {
		final String fileNameExtension = getFileNameExtension(fileName);

		if (fileNameExtension.startsWith("asc")) {
			return new EsriGRIDReader(fileName, geoProcessorType);
		} else if (worldFileExtensions.contains(fileNameExtension)) {
			return new WorldImageReader(fileName);
		} else if (fileNameExtension.endsWith("xyz")) {
			throw new RuntimeException("need to be implemented");
		} else {
			throw new RuntimeException("Unknown filename extension !");
		}
	}

	private static String getFileNameExtension(final String fileName) {
		final int dotIndex = fileName.lastIndexOf('.');
		return fileName.substring(dotIndex + 1).toLowerCase();
	}
}