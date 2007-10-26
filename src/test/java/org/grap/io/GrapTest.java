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
				1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
				1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
				1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
				1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
				1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
				1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
				1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
				1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
				1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
				1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
		};
	}

	public static void compareGeoRasterAndArray(final GeoRaster geoRaster,
			final short[] sArray) throws IOException {
		assertTrue(geoRaster.getWidth() * geoRaster.getHeight() == sArray.length);
		final PixelProvider pixelProvider = geoRaster.getPixelProvider();
		for (int r = 0; r < geoRaster.getHeight(); r++) {
			for (int c = 0; c < geoRaster.getWidth(); c++) {
				System.out.printf("%d %d\t%.0f == %d\n", c, r, pixelProvider
						.getPixel(c, r), sArray[r * 10 + c]);
//				assertTrue(pixelProvider.getPixel(c, r) == sArray[r * 10 + c]);
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