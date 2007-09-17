package org.grap.processing;

import org.grap.model.GeoRaster;

public abstract class BasicOperation {
	protected GeoRaster geoRaster;

	public BasicOperation() {
	}

	public BasicOperation(GeoRaster geoRaster) {
		this.geoRaster = geoRaster;
	}
}