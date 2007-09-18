package org.grap.processing.operation;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;
import org.grap.processing.ComplexOperation;
import org.grap.processing.Operation;

public class Shadows extends ComplexOperation implements Operation {
	private final static int north = 1;

	private final static int northeast = 2;

	private final static int east = 3;

	private final static int southeast = 4;

	private final static int south = 5;

	private final static int southwest = 6;

	private final static int west = 7;

	private final static int northwest = 8;

	public Shadows(GeoRaster geoRaster, Object object) {
		super(geoRaster, object);
	}

	public GeoRaster execute() {
		final ImagePlus imp = geoRaster.getImagePlus();
		final RasterMetadata rasterMetadata = geoRaster.getMetadata();

		if ((object != null) && (object instanceof Integer)) {
			final Integer orientation = (Integer) object;
			final ImageProcessor ip = imp.getProcessor();

			switch (orientation) {
			case north:
				ip.convolve3x3(new int[] { 1, 2, 1, 0, 1, 0, -1, -2, -1 });
				break;
			case south:
				ip.convolve3x3(new int[] { -1, -2, -1, 0, 1, 0, 1, 2, 1 });
				break;
			case east:
				ip.convolve3x3(new int[] { -1, 0, 1, -2, 1, 2, -1, 0, 1 });
				break;
			case west:
				ip.convolve3x3(new int[] { 1, 0, -1, 2, 1, -2, 1, 0, -1 });
				break;
			case northwest:
				ip.convolve3x3(new int[] { 2, 1, 0, 1, 1, -1, 0, -1, -2 });
				break;
			case southeast:
				ip.convolve3x3(new int[] { -2, -1, 0, -1, 1, 1, 0, 1, 2 });
				break;
			case northeast:
				ip.convolve3x3(new int[] { 0, 1, 2, -1, 1, 1, -2, -1, 0 });
				break;
			case southwest:
				ip.convolve3x3(new int[] { 0, -1, -2, 1, 1, -1, 2, 1, 0 });
				break;
			}
		}
		return new GeoRaster(imp, rasterMetadata);
	}
}