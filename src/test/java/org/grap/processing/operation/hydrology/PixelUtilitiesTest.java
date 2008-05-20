package org.grap.processing.operation.hydrology;

import junit.framework.TestCase;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.operation.hydrology.HydrologyUtilities;

public class PixelUtilitiesTest extends TestCase {
	private static final double EPSILON = 1.0E-5;
	private float[][] arrayOfDEMs;
	private RasterMetadata[] arrayOfRMDs;

	protected void setUp() throws Exception {
		super.setUp();

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

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetMaxSlopeDirection() throws Exception {
		for (int i = 0; i < arrayOfRMDs.length; i++) {
			for (int j = 0; j < arrayOfDEMs.length; j++) {
				GeoRaster grDEM = GeoRasterFactory.createGeoRaster(
						arrayOfDEMs[j], 3, 3, arrayOfRMDs[i]);
				HydrologyUtilities hydrologyUtilities = new HydrologyUtilities(grDEM);

				switch (j) {
				case 0:
					assertEquals(HydrologyUtilities.noDataValueForDirection, hydrologyUtilities
							.getD8Direction(1, 1));
					break;
				case 1:
					assertEquals(HydrologyUtilities.indecisionDirection,
							hydrologyUtilities.getD8Direction(1, 1));
					break;
				case 2:
				case 3:
					assertEquals(16, hydrologyUtilities.getD8Direction(1, 1));
					break;
				default:
					fail();
				}
			}
		}
	}

	public void testGetMaxSlopeAngleInDegrees() throws Exception {
		for (int i = 0; i < arrayOfRMDs.length; i++) {
			for (int j = 0; j < arrayOfDEMs.length; j++) {
				GeoRaster grDEM = GeoRasterFactory.createGeoRaster(
						arrayOfDEMs[j], 3, 3, arrayOfRMDs[i]);
				HydrologyUtilities hydrologyUtilities = new HydrologyUtilities(grDEM);

				switch (j) {
				case 0:
					assertTrue(Float.isNaN(hydrologyUtilities
							.getSlopeInDegrees(1, 1)));
					break;
				case 1:
					assertEquals(
							hydrologyUtilities.getSlopeInDegrees(1, 1), 0f);
					break;
				case 2:
				case 3:
					if (0 == i) {
						assertTrue(Math.abs(hydrologyUtilities
								.getSlopeInDegrees(1, 1)
								- 180 * Math.atan(5) / Math.PI) < EPSILON);
					} else if (1 == i) {
						assertEquals(hydrologyUtilities.getSlopeInDegrees(
								1, 1), 45f);
					}
					break;
				default:
					fail();
				}
			}
		}
	}
}