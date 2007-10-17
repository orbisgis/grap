package org.grap.utilities;

import ij.gui.PolygonRoi;
import ij.gui.Roi;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LinearRing;

public class JTSConverter {
	// private final static GeometryFactory geometryFactory = new
	// GeometryFactory();

	// public static Roi toRoi(final GeoRaster geoRaster, final Geometry
	// geometry) {
	// Roi roi = null;
	//
	// if (geometry instanceof Polygon) {
	// roi = toPolygonRoi(geoRaster, (Polygon) geometry);
	// } else {
	// // TODO
	// }
	// return roi;
	// }

	public static PolygonRoi toPolygonRoi(final LinearRing ring) {
		final Coordinate[] coordinates = ring.getCoordinates();
		final int[] coordsX = new int[coordinates.length];
		final int[] coordsY = new int[coordinates.length];

		for (int i = 0; i < coordinates.length; i++) {
			coordsX[i] = (int) coordinates[i].x;
			coordsY[i] = (int) coordinates[i].y;
		}
		return new PolygonRoi(coordsX, coordsY, coordsX.length, Roi.POLYGON);
	}

	// public static Geometry roiToJTS(final Roi roi) {
	// Geometry geometry = null;
	//
	// if (roi instanceof PolygonRoi) {
	// final Polygon p = roi.getPolygon();
	// final Coordinate[] coordinates = new Coordinate[p.npoints];
	//
	// for (int i = 0; i < p.npoints; i++) {
	// coordinates[i] = new Coordinate(p.xpoints[i], p.ypoints[i]);
	// }
	// geometry = geometryFactory.createPolygon(geometryFactory
	// .createLinearRing(coordinates), null);
	// }
	// return geometry;
	// }
}