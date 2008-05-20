package org.grap.processing.operation.hydrology;

import org.grap.model.GeoRaster;
import org.grap.processing.OperationException;

public abstract class D8OpAbstractMultiThreads extends D8OpAbstract {
	private static final boolean PARALLEL = false;

	@Override
	public GeoRaster evaluateResult(GeoRaster geoRaster)
			throws OperationException {
		if (PARALLEL) {
			return parallel(geoRaster);
		} else {
			return sequential(geoRaster);
		}
	}

	abstract GeoRaster sequential(GeoRaster geoRaster)
			throws OperationException;

	abstract GeoRaster parallel(GeoRaster geoRaster) throws OperationException;
}