package org.grap.processing.operation;

import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.Orientations;

public class Shadows implements Operation {
	private Orientations orientation;

	public Shadows(final Orientations orientation) {
		this.orientation = orientation;
	}

	public GeoRaster execute(final GeoRaster geoRaster)
			throws OperationException {

		switch (orientation) {
		case NORTH:
			return geoRaster.convolve3x3(new int[] { 1, 2, 1, 0, 1, 0, -1, -2,
					-1 });
		case SOUTH:
			return geoRaster.convolve3x3(new int[] { -1, -2, -1, 0, 1, 0, 1, 2,
					1 });
		case EAST:
			return geoRaster.convolve3x3(new int[] { -1, 0, 1, -2, 1, 2, -1, 0,
					1 });
		case WEST:
			return geoRaster.convolve3x3(new int[] { 1, 0, -1, 2, 1, -2, 1, 0,
					-1 });
		case NORTHWEST:
			return geoRaster.convolve3x3(new int[] { 2, 1, 0, 1, 1, -1, 0, -1,
					-2 });
		case SOUTHEAST:
			return geoRaster.convolve3x3(new int[] { -2, -1, 0, -1, 1, 1, 0, 1,
					2 });
		case NORTHEAST:
			return geoRaster.convolve3x3(new int[] { 0, 1, 2, -1, 1, 1, -2, -1,
					0 });
		case SOUTHWEST:
			return geoRaster.convolve3x3(new int[] { 0, -1, -2, 1, 1, -1, 2, 1,
					0 });
		default:
			throw new OperationException("Unknown orientation: " + orientation);
		}
	}
}