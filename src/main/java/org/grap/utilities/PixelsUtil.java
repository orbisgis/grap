package org.grap.utilities;

import java.awt.geom.Point2D;

import org.grap.model.GeoRaster;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

public class PixelsUtil {

	
	public static LineString toPixel(final GeoRaster geoRaster, final LineString lineString) {
		final Coordinate[] realWorldCoords = lineString.getCoordinates();
		final Coordinate[] pixelGridCoords = new Coordinate[realWorldCoords.length];
		for (int i = 0; i < pixelGridCoords.length; i++) {
			final Point2D p = geoRaster.fromRealWorldCoordToPixelGridCoord(
					realWorldCoords[i].x, realWorldCoords[i].y);
			pixelGridCoords[i] = new Coordinate(p.getX(), p.getY());
		}
		return new GeometryFactory().createLineString(pixelGridCoords);
	}
	
	public static MultiLineString toPixel(final GeoRaster geoRaster, final MultiLineString mls) {

		LineString[] lineStrings = new LineString[mls.getNumGeometries()];
		for (int k = 0; k < mls.getNumGeometries(); k++) {
			LineString ls = (LineString) mls.getGeometryN(k);
			
			lineStrings[k] = toPixel(geoRaster, ls);
		}
		return new GeometryFactory().createMultiLineString(lineStrings);
	}
}
