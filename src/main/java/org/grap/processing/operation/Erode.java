package org.grap.processing.operation;

import ij.ImagePlus;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;

public class Erode implements Operation {
	public GeoRaster execute(final GeoRaster geoRaster) {
		final ImagePlus imp = geoRaster.getImagePlus();
		final RasterMetadata rasterMetadata = geoRaster.getRasterMetadata();
		imp.getProcessor().erode();
		imp.show();
		return new GeoRaster(imp, rasterMetadata);
	}
}