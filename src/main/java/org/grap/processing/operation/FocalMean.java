package org.grap.processing.operation;

import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;

public class FocalMean implements Operation {
	private int focalMeanSize;

	public FocalMean(final int focalMeanSize) {
		this.focalMeanSize = focalMeanSize;
	}

	public GeoRaster execute(final GeoRaster geoRaster)
			throws OperationException {
		if ((3 == focalMeanSize) || (5 == focalMeanSize)
				|| (7 == focalMeanSize)) {
			return geoRaster.convolve(buildKernel(focalMeanSize),
					focalMeanSize, focalMeanSize);
		} else {
			throw new RuntimeException("Bad focal mean size (only 3, 5 or 7) !");
		}
	}

	private float[] buildKernel(final int size) {
		final float[] kernel = new float[size * size];
		for (int i = 0; i < kernel.length; i++) {
			kernel[i] = 1;
		}
		return kernel;
	}
}