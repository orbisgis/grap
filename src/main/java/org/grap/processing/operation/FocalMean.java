package org.grap.processing.operation;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;

public class FocalMean implements Operation {
	private int focalMeanSize;

	public FocalMean(final int focalMeanSize) {
		this.focalMeanSize = focalMeanSize;
	}

	public GeoRaster execute(final GeoRaster geoRaster) {
		final ImagePlus imp = geoRaster.getImagePlus();
		final RasterMetadata rasterMetadata = geoRaster.getRasterMetadata();
		final ImageProcessor ip = imp.getProcessor();

		if ((3 == focalMeanSize) || (5 == focalMeanSize)
				|| (7 == focalMeanSize)) {
			ip.convolve(buildKernel(focalMeanSize), focalMeanSize,
					focalMeanSize);
		} else {
			throw new RuntimeException("Bad focal mean size (only 3, 5 or 7) !");
		}
		return new GeoRaster(imp, rasterMetadata);
	}

	private float[] buildKernel(final int size) {
		final float[] kernel = new float[size * size];
		for (int i = 0; i < kernel.length; i++) {
			kernel[i] = 1;
		}
		return kernel;
	}
}