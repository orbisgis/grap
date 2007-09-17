package org.grap.processing.operation;

import ij.ImagePlus;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;
import org.grap.processing.BasicOperation;
import org.grap.processing.Operation;

public class Erode extends BasicOperation implements Operation {
	public Erode() {
	}

	public Erode(GeoRaster geoRaster) {
		super(geoRaster);
	}

	public GeoRaster execute() {
		ImagePlus imp = geoRaster.getImagePlus();
		RasterMetadata rasterMetadata = geoRaster.getMetadata();
		imp.getProcessor().erode();
		imp.show();
		return new GeoRaster(imp, rasterMetadata);
	}
}