package org.grap.utilities;

import ij.gui.PolygonRoi;
import ij.gui.Roi;

import org.grap.model.GeoRaster;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public class JTSConverter {
	private final static GeometryFactory geometryFactory = new GeometryFactory();

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

	public static PolygonRoi toPolygonRoi(final GeoRaster geoRaster,
			final Polygon polygon) {
		PolygonRoi polygonRoi = null;

		final Coordinate[] coordinates = polygon.getExteriorRing()
				.getCoordinates();
		final int[] coordsX = new int[coordinates.length];
		final int[] coordsY = new int[coordinates.length];

		for (int i = 0; i < coordinates.length; i++) {
			final Coordinate arrayPixelsCoords = geoRaster.getPixelCoords(
					coordinates[i].x, coordinates[i].y);
			coordsX[i] = (int) arrayPixelsCoords.x;
			coordsY[i] = (int) arrayPixelsCoords.y;
		}
		polygonRoi = new PolygonRoi(coordsX, coordsY, coordsX.length,
				Roi.POLYGON);
		return polygonRoi;

		// TODO: take also into account the holes !
	}

	public static Geometry roiToJTS(final GeoRaster geoRaster, final Roi roi) {
		Geometry geometry = null;

		if (roi instanceof PolygonRoi) {
			java.awt.Polygon p = roi.getPolygon();
			final Coordinate[] coordinates = new Coordinate[p.npoints];

			for (int i = 0; i < p.npoints; i++) {
				final int x = p.xpoints[i];
				final int y = p.ypoints[i];
				final Coordinate xycoords = geoRaster.pixelToWorldCoord(x, y);
				coordinates[i] = xycoords;
			}
			geometry = geometryFactory.createPolygon(geometryFactory
					.createLinearRing(coordinates), null);
		}
		return geometry;
	}
}