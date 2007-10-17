package org.grap.io;

import java.io.IOException;

import junit.framework.TestCase;

import org.grap.lut.LutGenerator;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.PixelProvider;
import org.grap.model.RasterMetadata;

public class GrapTest extends TestCase {

	public final static String externalData = "../../datas2tests/";

	public final static String internalData = "src/test/resources/";

	public static GeoRaster sampleRaster;

	public static GeoRaster sampleDEM;

	public static short[] slopesAccumulationForDEM;

	public static short[] allWatershedsForDEM;

	static {
		final int nrows = 10;
		final int ncols = 10;
		final byte[] values = new byte[nrows * ncols];
		for (int i = 0; i < nrows * ncols; i++) {
			values[i] = (byte) i;
		}

		final RasterMetadata rmd = new RasterMetadata(0, 15, 1, -1, ncols,
				nrows);
		sampleRaster = GeoRasterFactory.createGeoRaster(values, ncols, nrows,
				LutGenerator.colorModel("fire"), rmd);

		final short[] DEM = new short[] { //
		100, 100, 100, 100, 100, 100, 100, 0, 100, 100,//
				100, 50, 50, 50, 100, 100, 25, 10, 25, 100,//
				100, 25, 25, 25, 100, 100, 25, 11, 25, 100,//
				100, 25, 15, 25, 100, 100, 25, 12, 25, 100,//
				100, 25, 14, 25, 100, 100, 25, 13, 25, 100,//
				100, 25, 13, 25, 100, 100, 25, 14, 25, 100,//
				100, 25, 12, 25, 100, 100, 25, 15, 25, 100,//
				100, 25, 11, 25, 100, 100, 25, 25, 25, 100,//
				100, 25, 10, 25, 100, 100, 50, 50, 50, 100,//
				100, 100, 0, 100, 100, 100, 100, 100, 100, 100,//
		};

		sampleDEM = GeoRasterFactory.createGeoRaster(DEM, ncols, nrows,
				LutGenerator.colorModel("fire"), rmd);

		slopesAccumulationForDEM = new short[] {//
		1, 1, 1, 1, 1, 1, 1, 50, 1, 1,//
				1, 3, 2, 3, 1, 1, 3, 41, 3, 1,//
				1, 6, 3, 6, 1, 1, 2, 40, 2, 1,//
				1, 2, 20, 2, 1, 1, 2, 35, 2, 1,//
				1, 2, 25, 2, 1, 1, 2, 30, 2, 1,//
				1, 2, 30, 2, 1, 1, 2, 25, 2, 1,//
				1, 2, 35, 2, 1, 1, 2, 20, 2, 1,//
				1, 2, 40, 2, 1, 1, 6, 3, 6, 1,//
				1, 3, 41, 3, 1, 1, 3, 2, 3, 1,//
				1, 1, 50, 1, 1, 1, 1, 1, 1, 1,//
		};

		allWatershedsForDEM = new short[] { //
		92, 92, 92, 92, 92, 7, 7, -1, 7, 7, //
				92, 92, 92, 92, 92, 7, 7, 7, 7, 7,//
				92, 92, 92, 92, 92, 7, 7, 7, 7, 7,//
				92, 92, 92, 92, 92, 7, 7, 7, 7, 7,//
				92, 92, 92, 92, 92, 7, 7, 7, 7, 7,//
				92, 92, 92, 92, 92, 7, 7, 7, 7, 7,//
				92, 92, 92, 92, 92, 7, 7, 7, 7, 7,//
				92, 92, 92, 92, 92, 7, 7, 7, 7, 7,//
				92, 92, 92, 92, 92, 7, 7, 7, 7, 7,//
				92, 92, -1, 92, 92, 7, 7, 7, 7, 7,//
		};
	}

	public static void compareGeoRasterAndArray(final GeoRaster geoRaster,
			final short[] sArray) throws IOException {
		assertTrue(geoRaster.getWidth() * geoRaster.getHeight() == sArray.length);
		final PixelProvider pixelProvider = geoRaster.getPixelProvider();
		for (int r = 0; r < geoRaster.getHeight(); r++) {
			for (int c = 0; c < geoRaster.getWidth(); c++) {
				assertTrue(pixelProvider.getPixel(c, r) == sArray[r * 10 + c]);
			}
		}
	}

	protected boolean equals(float[] pixels, float[] tifPixels) {
		if (tifPixels.length != pixels.length) {
			return false;
		} else {
			for (int i = 0; i < tifPixels.length; i++) {
				if (Float.isNaN(tifPixels[i])) {
					if (!Float.isNaN(pixels[i])) {
						return false;
					}
				} else {
					if (tifPixels[i] != pixels[i]) {
						return false;
					}
				}
			}
		}
		return true;
	}
}