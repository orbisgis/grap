package org.grap.processing;

import org.grap.model.GeoRaster;

public abstract class ComplexOperation {
	protected GeoRaster geoRaster;

	protected Object object;

	public ComplexOperation() {
	}

	public ComplexOperation(GeoRaster geoRaster, Object object) {
		this.geoRaster = geoRaster;
		this.object = object;
	}
}