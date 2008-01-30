/*
 * The GRAP library (GeoRAster Processing) is a middleware dedicated
 * to the processing of various kinds of geographic raster data. It
 * provides a complete and robust API to manipulate ASCII Grid or
 * tiff, png, bmp, jpg (with the corresponding world file) geographic
 * images. GRAP is produced  by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
package org.grap.io;

import java.awt.geom.Point2D;

import junit.framework.TestCase;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public class GeoreferencingTest extends TestCase {
	private static GeoRaster sampleRaster;

	static {
		final double upperLeftX = 1234.56;
		final double upperLeftY = 987.65;
		final float pixelSize_X = 2.5f;
		final float pixelSize_Y = -10.5f;
		final int nrows = 33;
		final int ncols = 57;

		final byte[] values = new byte[nrows * ncols];
		for (int i = 0; i < nrows * ncols; i++) {
			values[i] = (byte) i;
		}

		final RasterMetadata rmd = new RasterMetadata(upperLeftX, upperLeftY,
				pixelSize_X, pixelSize_Y, ncols, nrows);
		sampleRaster = GeoRasterFactory.createGeoRaster(values, ncols, nrows,
				rmd);
	}

	public void testToPixel() throws Exception {
		final RasterMetadata md = sampleRaster.getMetadata();

		final int halfPixelSize_X = Math.round(md.getPixelSize_X() / 2);
		final int halfPixelSize_Y = Math
				.round(Math.abs(md.getPixelSize_Y()) / 2);

		for (int r = 0; r < md.getNRows(); r++) {
			final double y = md.getYulcorner() + r * md.getPixelSize_Y();
			for (int c = 0; c < md.getNCols(); c++) {
				final double x = md.getXulcorner() + c * md.getPixelSize_X();
				for (int aleaR = -halfPixelSize_Y + 1; aleaR <= halfPixelSize_Y; aleaR++) {
					for (int aleaC = -halfPixelSize_X; aleaC < halfPixelSize_X; aleaC++) {
						final Point2D p = sampleRaster
								.fromRealWorldCoordToPixelGridCoord(x + aleaC,
										y + aleaR);
						assertTrue(c == p.getX());
						assertTrue(r == p.getY());
					}
				}
			}
		}
	}

	public void testToWorld() throws Exception {
		final RasterMetadata md = sampleRaster.getMetadata();

		for (int r = 0; r < md.getNRows(); r++) {
			final double y = md.getYulcorner() + r * md.getPixelSize_Y();
			for (int c = 0; c < md.getNCols(); c++) {
				final double x = md.getXulcorner() + c * md.getPixelSize_X();
				final Point2D p = sampleRaster
						.fromPixelGridCoordToRealWorldCoord(c, r);
				assertTrue(x == p.getX());
				assertTrue(y == p.getY());
			}
		}
	}

	public void testFromPixelToWorld() throws Exception {
		final String src = "../../datas2tests/grid/3x3.asc";
		final GeoRaster geoRaster = GeoRasterFactory.createGeoRaster(src);
		geoRaster.open();

		final WKTReader wkt = new WKTReader();
		final Geometry point = wkt.read("POINT ( 290004.9 2259994.9)");

		System.out.println("The Point for the test : " + point.toText());

		final int halfPixelSize_X = (int) geoRaster.getMetadata()
				.getPixelSize_X() / 2;
		final int halfPixelSize_Y = (int) Math.abs(geoRaster.getMetadata()
				.getPixelSize_Y()) / 2;

		Point2D worldCoord = geoRaster.fromPixelGridCoordToRealWorldCoord(1, 1);

		System.out.println("Pixel world coordinates : " + worldCoord);
	}
}