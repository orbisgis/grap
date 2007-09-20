package org.grap.processing;

import org.grap.model.GeoRaster;

public interface Operation {
	public GeoRaster execute();

	public GeoRaster execute(final GeoRaster raster);
}