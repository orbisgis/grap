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