package org.grap.model;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.grap.io.FileReader;
import org.grap.io.FileReaderFactory;

public class GeoRasterFactory {

	private static String getFileNameExtension(final String fileName) {
		final int dotIndex = fileName.lastIndexOf('.');
		return fileName.substring(dotIndex + 1).toLowerCase();
	}

	public static GeoRaster read(final String fileName)
			throws FileNotFoundException, IOException {
		return read(fileName, GeoProcessorType.FLOAT);
	}

	public static GeoRaster read(final String fileName,
			final GeoProcessorType geoProcessorType)
			throws FileNotFoundException, IOException {
		if ((GeoProcessorType.BYTE == geoProcessorType)
				|| (GeoProcessorType.SHORT == geoProcessorType)
				|| (GeoProcessorType.FLOAT == geoProcessorType)) {
			final String fileNameExtension = getFileNameExtension(fileName);
			final FileReader reader = FileReaderFactory.create(fileName,
					fileNameExtension, geoProcessorType);
			return reader.read();
		} else {
			throw new RuntimeException(
					"GeoRaster ImageProcessor's type must be Byte, Short or Float !");
		}
	}
}