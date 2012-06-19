/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.grap.processing.operation.hydrology.archive;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.operation.hydrology.HydrologyUtilities;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

@Ignore("These tests shall disappear with the hydrology code in grap: they will be implemented on top of JAI.")
public class PixelUtilitiesTest {
	private static final double EPSILON = 1.0E-5;
	private float[][] arrayOfDEMs;
	private RasterMetadata[] arrayOfRMDs;

        @Before
	public void setUp() throws Exception {

		arrayOfDEMs = new float[][] { //
		new float[] { 10, 10, 10, 10, Float.NaN, 10, 10, 10, 10 }, //
				new float[] { 10, 10, 10, 10, 10, 10, 10, 10, 10 }, //
				new float[] { 10, 10, 10, 5, 10, 10, 10, 10, 10 }, //
				new float[] { 10, 9, 8, 5, 10, 7, 6, 6, 10 }, //
		};

		arrayOfRMDs = new RasterMetadata[] {//
		new RasterMetadata(0, 15, 1, -1, 3, 3), //
				new RasterMetadata(0, 15, 5, -5, 3, 3), //
		};
	}

        @Test
	public void testGetMaxSlopeDirection() throws Exception {
		for (int i = 0; i < arrayOfRMDs.length; i++) {
			for (int j = 0; j < arrayOfDEMs.length; j++) {
				GeoRaster grDEM = GeoRasterFactory.createGeoRaster(
						arrayOfDEMs[j], arrayOfRMDs[i]);
				HydrologyUtilities hydrologyUtilities = new HydrologyUtilities(
						grDEM);

				switch (j) {
				case 0:
					assertEquals(GeoRaster.FLOAT_NO_DATA_VALUE,
							hydrologyUtilities.getD8Direction(1, 1), 0);
					break;
				case 1:
					assertEquals(HydrologyUtilities.indecisionDirection,
							hydrologyUtilities.getD8Direction(1, 1), 0);
					break;
				case 2:
				case 3:
					assertEquals(16, hydrologyUtilities.getD8Direction(1, 1), 0);
					break;
				default:
					fail();
				}
			}
		}
	}

        @Test
	public void testGetMaxSlopeAngleInDegrees() throws Exception {
		for (int i = 0; i < arrayOfRMDs.length; i++) {
			for (int j = 0; j < arrayOfDEMs.length; j++) {
				GeoRaster grDEM = GeoRasterFactory.createGeoRaster(
						arrayOfDEMs[j], arrayOfRMDs[i]);
				HydrologyUtilities hydrologyUtilities = new HydrologyUtilities(
						grDEM);

				switch (j) {
				case 0:
					assertTrue(Float.isNaN(hydrologyUtilities
							.getSlopeInDegrees(1, 1)));
					break;
				case 1:
					assertEquals(hydrologyUtilities.getSlopeInDegrees(1, 1), 0f, 0);
					break;
				case 2:
				case 3:
					if (0 == i) {
						assertTrue(Math.abs(hydrologyUtilities
								.getSlopeInDegrees(1, 1)
								- 180 * Math.atan(5) / Math.PI) < EPSILON);
					} else if (1 == i) {
						assertEquals(
								hydrologyUtilities.getSlopeInDegrees(1, 1), 45f, 0);
					}
					break;
				default:
					fail();
				}
			}
		}
	}
}