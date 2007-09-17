package org.grap.processing.operation;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;
import org.grap.processing.ComplexOperation;
import org.grap.processing.Operation;

public class Shadows extends ComplexOperation implements Operation {
	static int north = 1, northeast = 2, east = 3, southeast = 4, south = 5,
			southwest = 6, west = 7, northwest = 8;

	public Shadows() {
	}

	public Shadows(GeoRaster geoRaster, Object object) {
		super(geoRaster, object);
	}

	public GeoRaster execute() {
		ImagePlus imp = geoRaster.getImagePlus();
		RasterMetadata rasterMetadata = geoRaster.getMetadata();

		if ((object != null) && (object instanceof Integer)) {
			Integer orientation = (Integer) object;
			ImageProcessor ip = imp.getProcessor();

			if (orientation == north) {
				int[] kernel = { 1, 2, 1, 0, 1, 0, -1, -2, -1 };
				ip.convolve3x3(kernel);
			} else if (orientation == south) {
				int[] kernel = { -1, -2, -1, 0, 1, 0, 1, 2, 1 };
				ip.convolve3x3(kernel);
			} else if (orientation == east) {
				int[] kernel = { -1, 0, 1, -2, 1, 2, -1, 0, 1 };
				ip.convolve3x3(kernel);
			} else if (orientation == west) {
				int[] kernel = { 1, 0, -1, 2, 1, -2, 1, 0, -1 };
				ip.convolve3x3(kernel);
			} else if (orientation == northwest) {
				int[] kernel = { 2, 1, 0, 1, 1, -1, 0, -1, -2 };
				ip.convolve3x3(kernel);
			} else if (orientation == southeast) {
				int[] kernel = { -2, -1, 0, -1, 1, 1, 0, 1, 2 };
				ip.convolve3x3(kernel);
			} else if (orientation == northeast) {
				int[] kernel = { 0, 1, 2, -1, 1, 1, -2, -1, 0 };
				ip.convolve3x3(kernel);
			} else if (orientation == southwest) {
				int[] kernel = { 0, -1, -2, 1, 1, -1, 2, 1, 0 };
				ip.convolve3x3(kernel);
			} else {
			}
		}
		return new GeoRaster(imp, rasterMetadata);
	}
}