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

	public static FileReader create(final String fileName,
			final String fileNameExtension,
			final GeoProcessorType geoProcessorType)
			throws FileNotFoundException, IOException {
		if (fileNameExtension.endsWith("asc")) {
			return new EsriGRIDReader(fileName, geoProcessorType);
		} else if (fileNameExtension.endsWith("xyz")) {
			throw new Error("need to be implemented");
		} else if (worldFileExtensions.contains(fileNameExtension)) {
			return new WorldImageReader(fileName);
		} else {
			throw new Error("Unknown filename extension !");
		}
	}
}