/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.grap.processing.operation;

import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.junit.Test;

import static org.junit.Assert.*;

public class Mathtest extends GrapTest {
	private final static float EPSILON = 1E-4f;

        @Test
	public void testAddValue() throws Exception {
		// load the DEM
		final GeoRaster geoRasterSrc = GeoRasterFactory
				.createGeoRaster(externalData + "grid/sample.asc");
		geoRasterSrc.open();

		int addedValue = 20;
		final Operation addValue = new GeoRasterMath(addedValue,
				GeoRasterMath.ADD);
		final GeoRaster gResult = geoRasterSrc.doOperation(addValue);

		float[] pixelsSrc = geoRasterSrc.getFloatPixels();

		float[] pixelsResult = gResult.getFloatPixels();

		for (int i = 0; i < pixelsResult.length; i++) {
			if (Float.isNaN(pixelsSrc[i])) {
				assertTrue(Float.isNaN(pixelsResult[i]));
			} else {
				assertTrue(Math.abs(pixelsSrc[i]
						- (pixelsResult[i] - addedValue)) < EPSILON);
			}
		}
	}

        @Test
	public void testSubtractValue() throws Exception {
		// load the DEM
		final GeoRaster geoRasterSrc = GeoRasterFactory
				.createGeoRaster(externalData + "grid/sample.asc");
		geoRasterSrc.open();

		int addedValue = 20;
		final Operation addValue = new GeoRasterMath(addedValue,
				GeoRasterMath.SUBSTRACT);
		final GeoRaster gResult = geoRasterSrc.doOperation(addValue);

		float[] pixelsSrc = geoRasterSrc.getFloatPixels();

		float[] pixelsResult = gResult.getFloatPixels();

		for (int i = 0; i < pixelsResult.length; i++) {
			if (Float.isNaN(pixelsSrc[i])) {
				assertTrue(Float.isNaN(pixelsResult[i]));
			} else {
				assertTrue(Math.abs(pixelsSrc[i]
						- (pixelsResult[i] + addedValue)) < EPSILON);
			}
		}
	}

        @Test
	public void testMultiplyValue() throws Exception {
		// load the DEM
		final GeoRaster geoRasterSrc = GeoRasterFactory
				.createGeoRaster(externalData + "grid/sample.asc");
		geoRasterSrc.open();

		int addedValue = 20;
		final Operation addValue = new GeoRasterMath(addedValue,
				GeoRasterMath.MULTIPLY);
		final GeoRaster gResult = geoRasterSrc.doOperation(addValue);

		float[] pixelsSrc = geoRasterSrc.getFloatPixels();

		float[] pixelsResult = gResult.getFloatPixels();

		for (int i = 0; i < pixelsResult.length; i++) {
			if (Float.isNaN(pixelsSrc[i])) {
				assertTrue(Float.isNaN(pixelsResult[i]));
			} else {
				assertTrue(Math.abs(pixelsSrc[i]
						- (pixelsResult[i] / addedValue)) < EPSILON);
			}
		}
	}

        @Test
	public void testDivideValue() throws Exception {
		// load the DEM
		final GeoRaster geoRasterSrc = GeoRasterFactory
				.createGeoRaster(externalData + "grid/sample.asc");
		geoRasterSrc.open();

		int addedValue = 20;
		final Operation addValue = new GeoRasterMath(addedValue,
				GeoRasterMath.DIVIDE);
		final GeoRaster gResult = geoRasterSrc.doOperation(addValue);

		float[] pixelsSrc = geoRasterSrc.getFloatPixels();

		float[] pixelsResult = gResult.getFloatPixels();

		for (int i = 0; i < pixelsResult.length; i++) {
			if (Float.isNaN(pixelsSrc[i])) {
				assertTrue(Float.isNaN(pixelsResult[i]));
			} else {
				assertTrue(Math.abs(pixelsSrc[i]
						- (pixelsResult[i] * addedValue)) < EPSILON);
			}
		}
	}

        @Test
	public void testABSValue() throws Exception {
		// load the DEM
		final GeoRaster geoRasterSrc = GeoRasterFactory
				.createGeoRaster(externalData + "grid/sample.asc");
		geoRasterSrc.open();

		final Operation addValue = new GeoRasterMath(0d, GeoRasterMath.ABS);
		final GeoRaster gResult = geoRasterSrc.doOperation(addValue);

		float[] pixelsResult = gResult.getFloatPixels();

		for (int i = 0; i < pixelsResult.length; i++) {
			if (Float.isNaN(pixelsResult[i])) {
				assertTrue(Float.isNaN(pixelsResult[i]));
			} else {
				assertTrue(pixelsResult[i] > 0);
			}
		}
	}
}