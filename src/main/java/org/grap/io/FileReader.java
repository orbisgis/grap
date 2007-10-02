package org.grap.io;

import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;

public interface FileReader {
	public RasterMetadata getRasterMetadata() throws IOException;

	public GeoRaster read() throws IOException;
}