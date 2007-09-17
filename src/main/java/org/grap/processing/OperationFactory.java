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
	public Operation crop(GeoRaster geoRaster, Geometry geom) {
		return new Crop(geoRaster, geom);
	}

	public Operation crop(GeoRaster geoRaster, Rectangle rectangle) {
		return new Crop(geoRaster, rectangle);
	}

	public Operation smooth(GeoRaster geoRaster) {
		return new Smooth(geoRaster);
	}

	public Operation erode(GeoRaster geoRaster) {
		return new Erode(geoRaster);
	}

	public Operation shadows(GeoRaster geoRaster, int orientation) {
		return new Shadows(geoRaster, orientation);
	}

	public Operation focalMean(GeoRaster geoRaster, int window) {
		return new FocalMean(geoRaster, window);
	}

	public Operation slope(GeoRaster geoRaster) {
		return new Slope(geoRaster);
	}
}