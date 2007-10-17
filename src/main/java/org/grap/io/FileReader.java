package org.grap.io;

import ij.ImagePlus;

import java.io.IOException;

import org.grap.model.RasterMetadata;

public interface FileReader {
	public RasterMetadata readRasterMetadata() throws IOException,
			GeoreferencingException;

	public ImagePlus readImagePlus() throws IOException,
			GeoreferencingException;
}