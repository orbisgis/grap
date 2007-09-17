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

	public static Roi ToROI(Geometry geom) {
		if (geom instanceof Polygon) {
			roi = toPolygonRoi(geom);
		}
		return roi;
	}

	/**
	 * todo take into account hole and shell
	 * 
	 */
	public static PolygonRoi toPolygonRoi(Geometry geom) {
		if (geom instanceof Polygon) {
			Polygon p = (Polygon) geom;
			Coordinate[] coordinates = p.getExteriorRing().getCoordinates();
			int[] coordsX = new int[coordinates.length];
			int[] coordsY = new int[coordinates.length];

			for (int i = 0; i < coordinates.length; i++) {

				Coordinate arrayPixelsCoords = GeoRaster.getPixelCoords(
						coordinates[i].x, coordinates[i].y);

				coordsX[i] = (int) arrayPixelsCoords.x;
				coordsY[i] = (int) arrayPixelsCoords.y;
				// System.out.println( "x =" + coordsX[i] + " y = "+
				// coordsY[i]);
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

	public static Geometry RoitoJTS(Roi roi) {
		Geometry geomResult = null;
		if (roi instanceof PolygonRoi) {
			java.awt.Polygon p = roi.getPolygon();
			Coordinate[] coordinates = new Coordinate[p.npoints];

			for (int i = 0; i < p.npoints; i++) {
				int x = p.xpoints[i];
				int y = p.ypoints[i];
				Coordinate xycoords = GeoRaster.pixelToWorldCoord(x, y);
				coordinates[i] = xycoords;
			}
			GeometryFactory factory = new GeometryFactory();
			geomResult = factory.createPolygon(factory
					.createLinearRing(coordinates), null);
		}
		return geomResult;
	}
}