package org.grap.utilities;

import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;

import org.grap.model.GeoRaster;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public class JTSConverter {
	private static ShapeRoi shapeRoi;

	public static PolygonRoi polygonRoi;

	private static Roi roi;

	public static Roi toRoi(final GeoRaster geoRaster, final Geometry geom) {
		if (geom instanceof Polygon) {
			roi = toPolygonRoi(geoRaster, geom);
		}
		return roi;
	}

	/**
	 * todo take into account hole and shell
	 * 
	 */
	public static PolygonRoi toPolygonRoi(final GeoRaster geoRaster,
			final Geometry geom) {
		if (geom instanceof Polygon) {
			final Polygon p = (Polygon) geom;
			final Coordinate[] coordinates = p.getExteriorRing()
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
		}
		return polygonRoi;
	}

	/**
	 * todo take into account hole and shell
	 * 
	 */

	public static Geometry roiToJTS(final GeoRaster geoRaster, final Roi roi) {
		Geometry geomResult = null;
		if (roi instanceof PolygonRoi) {
			java.awt.Polygon p = roi.getPolygon();
			final Coordinate[] coordinates = new Coordinate[p.npoints];

			for (int i = 0; i < p.npoints; i++) {
				final int x = p.xpoints[i];
				final int y = p.ypoints[i];
				final Coordinate xycoords = geoRaster.pixelToWorldCoord(x, y);
				coordinates[i] = xycoords;
			}
			final GeometryFactory factory = new GeometryFactory();
			geomResult = factory.createPolygon(factory
					.createLinearRing(coordinates), null);
		}
		return geomResult;
	}
}