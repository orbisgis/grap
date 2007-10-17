package org.grap.processing.operation;

import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;

public class Erode implements Operation {
	public GeoRaster execute(final GeoRaster geoRaster)
			throws OperationException {
		return geoRaster.erode();
	}
}