package org.grap.processing.operation;

import ij.ImagePlus;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;

public class Smooth implements Operation {

	private GeoRaster geoRaster;

	public Smooth(GeoRaster geoRaster) {

		this.geoRaster = geoRaster;

	}

	public GeoRaster execute() {

		ImagePlus imp = geoRaster.getImagePlus();
		RasterMetadata rasterMetadata = geoRaster.getMetadata();

		imp.getProcessor().smooth();

		return new GeoRaster(imp, rasterMetadata);
	}

}
