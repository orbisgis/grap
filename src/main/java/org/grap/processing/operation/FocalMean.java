package org.grap.processing.operation;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;
import org.grap.processing.ComplexOperation;
import org.grap.processing.Operation;

public class FocalMean extends ComplexOperation implements Operation {
	public FocalMean() {
	}

	public FocalMean(GeoRaster geoRaster, Object object) {
		super(geoRaster, object);
	}

	public GeoRaster execute() {
		ImagePlus imp = geoRaster.getImagePlus();
		RasterMetadata rasterMetadata = geoRaster.getMetadata();

		if ((object != null) && (object instanceof Integer)) {
			Integer window = (Integer) object;
			ImageProcessor ip = imp.getProcessor();

			if (window == 3) {
				ip.convolve(buildKernel(window), window, window);
			} else if (window == 5) {
				ip.convolve(buildKernel(window), window, window);
			} else if (window == 7) {
				ip.convolve(buildKernel(window), window, window);
			} else {
			}
		}
		return new GeoRaster(imp, rasterMetadata);
	}

	public float[] buildKernel(int size) {
		float[] kernel = new float[size * size];
		for (int i = 0; i < kernel.length; i++) {
			kernel[i] = 1;
		}
		return kernel;
	}
}