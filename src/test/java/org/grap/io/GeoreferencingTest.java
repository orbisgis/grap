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

public class GeoreferencingTest extends TestCase {
	private static GeoRaster sampleRaster;
	private final static double upperLeftX = 1234.56;
	private final static double upperLeftY = 987.65;
	private final static float pixelSize_X = 2.5f;
	private final static float pixelSize_Y = -10.5f;
	private final static int nrows = 33;
	private final static int ncols = 57;

	static {
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
		final int halfPixelSize_X = (int) pixelSize_X / 2;
		final int halfPixelSize_Y = (int) Math.abs(pixelSize_Y) / 2;

		for (int r = 0; r < nrows; r++) {
			final double y = upperLeftY + r * pixelSize_Y;
			for (int c = 0; c < ncols; c++) {
				final double x = upperLeftX + c * pixelSize_X;
				for (int aleaR = -halfPixelSize_Y + 1; aleaR <= halfPixelSize_Y; aleaR++) {
					for (int aleaC = -halfPixelSize_X; aleaC < halfPixelSize_X; aleaC++) {
						final Point2D p = sampleRaster.getPixelCoords(
								x + aleaC, y + aleaR);
						assertTrue(c == Math.round(p.getX()));
						assertTrue(r == Math.round(p.getY()));
					}
				}
			}
		}
	}

	public void testToWorld() throws Exception {
		for (int r = 0; r < nrows; r++) {
			final double y = upperLeftY + r * pixelSize_Y;
			for (int c = 0; c < ncols; c++) {
				final double x = upperLeftX + c * pixelSize_X;
				final Point2D p = sampleRaster.pixelToWorldCoord(c, r);
				assertTrue(x == p.getX());
				assertTrue(y == p.getY());
			}
		}
	}
}