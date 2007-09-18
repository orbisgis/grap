package org.grap.processing;

import java.awt.Rectangle;

import org.grap.model.GeoRaster;
import org.grap.processing.operation.Crop;
import org.grap.processing.operation.Erode;
import org.grap.processing.operation.FocalMean;
import org.grap.processing.operation.Shadows;
import org.grap.processing.operation.Slope;
import org.grap.processing.operation.Smooth;

import com.vividsolutions.jts.geom.Geometry;

public class OperationFactory {
	public static Operation crop(final GeoRaster geoRaster, final Geometry geom) {
		return new Crop(geoRaster, geom);
	}

	public Operation crop(final GeoRaster geoRaster, final Rectangle rectangle) {
		return new Crop(geoRaster, rectangle);
	}

	public Operation smooth(final GeoRaster geoRaster) {
		return new Smooth(geoRaster);
	}

	public Operation erode(final GeoRaster geoRaster) {
		return new Erode(geoRaster);
	}

	public Operation shadows(final GeoRaster geoRaster, final int orientation) {
		return new Shadows(geoRaster, orientation);
	}

	public Operation focalMean(final GeoRaster geoRaster, final int window) {
		return new FocalMean(geoRaster, window);
	}

	public Operation slope(final GeoRaster geoRaster) {
		return new Slope(geoRaster);
	}
}