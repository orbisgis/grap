/*
 * The GRAP library (GeoRAster Processing) is a middleware dedicated
 * to the processing of various kinds of geographic raster data. It
 * provides a complete and robust API to manipulate ASCII Grid or
 * tiff, png, bmp, jpg (with the corresponding world file) geographic
 * images. GRAP is produced  by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GRAP.
 *
 * GRAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAP. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
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