package org.grap.processing.operation;

import java.awt.Rectangle;

import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;

import com.vividsolutions.jts.geom.LinearRing;

public class Crop implements Operation {
	private LinearRing ring;

	private Rectangle rectangle;

	public Crop(final LinearRing ring) {
		this.ring = ring;
	}

	public Crop(final Rectangle rectangle) {
		this.rectangle = rectangle;
	}

	public GeoRaster execute(final GeoRaster geoRaster)
			throws OperationException {
		if (null != ring) {
			return geoRaster.crop(ring);
		} else if (null != rectangle) {
			return geoRaster.crop(rectangle);
		} else {
			throw new RuntimeException("No polygon or rectangle specified");
		}
	}
}