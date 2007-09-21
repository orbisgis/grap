package org.grap.processing.operation;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.Orientations;

public class Shadows implements Operation {
	private Orientations orientation;

	public Shadows(final Orientations orientation) {
		this.orientation = orientation;
	}

	public GeoRaster execute(final GeoRaster geoRaster) {
		final ImagePlus imp = geoRaster.getImagePlus();
		final RasterMetadata rasterMetadata = geoRaster.getMetadata();
		final ImageProcessor ip = imp.getProcessor();

		switch (orientation) {
		case NORTH:
			ip.convolve3x3(new int[] { 1, 2, 1, 0, 1, 0, -1, -2, -1 });
			break;
		case SOUTH:
			ip.convolve3x3(new int[] { -1, -2, -1, 0, 1, 0, 1, 2, 1 });
			break;
		case EAST:
			ip.convolve3x3(new int[] { -1, 0, 1, -2, 1, 2, -1, 0, 1 });
			break;
		case WEST:
			ip.convolve3x3(new int[] { 1, 0, -1, 2, 1, -2, 1, 0, -1 });
			break;
		case NORTHWEST:
			ip.convolve3x3(new int[] { 2, 1, 0, 1, 1, -1, 0, -1, -2 });
			break;
		case SOUTHEAST:
			ip.convolve3x3(new int[] { -2, -1, 0, -1, 1, 1, 0, 1, 2 });
			break;
		case NORTHEAST:
			ip.convolve3x3(new int[] { 0, 1, 2, -1, 1, 1, -2, -1, 0 });
			break;
		case SOUTHWEST:
			ip.convolve3x3(new int[] { 0, -1, -2, 1, 1, -1, 2, 1, 0 });
			break;
		}
		return new GeoRaster(imp, rasterMetadata);
	}
}