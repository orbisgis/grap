package org.grap.processing.operation.hydrology;

import java.io.IOException;

import ij.ImagePlus;

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;

public abstract class D8Operation implements Operation {
	public final GeoRaster execute(final GeoRaster geoRaster)
			throws OperationException, GeoreferencingException {
		final long startTime = System.currentTimeMillis();
		try {
			if (ImagePlus.COLOR_RGB == geoRaster.getType()) {
				throw new OperationException(
						"D8Operation only handle a GRAY{8, 16 or 32} or a COLOR_256 GeoRaster image !");
			}
		} catch (IOException e) {
			throw new OperationException(e);
		}
		GeoRaster result = evaluateResult(geoRaster);

		System.out.printf("D8Operation in %d ms\n", System.currentTimeMillis()
				- startTime);
		return result;
	}

	public abstract GeoRaster evaluateResult(GeoRaster geoRaster)
			throws OperationException, GeoreferencingException;
}