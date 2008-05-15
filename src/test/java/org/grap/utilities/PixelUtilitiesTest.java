package org.grap.utilities;

import junit.framework.TestCase;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

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
				PixelUtilities pixelUtilities = new PixelUtilities(grDEM);

				switch (j) {
				case 0:
					assertEquals(PixelUtilities.noDataValueForDirection, pixelUtilities
							.getMaxSlopeDirection(1, 1));
					break;
				case 1:
					assertEquals(PixelUtilities.indecisionDirection,
							pixelUtilities.getMaxSlopeDirection(1, 1));
					break;
				case 2:
				case 3:
					assertEquals(16, pixelUtilities.getMaxSlopeDirection(1, 1));
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
				PixelUtilities pixelUtilities = new PixelUtilities(grDEM);

				switch (j) {
				case 0:
					assertTrue(Float.isNaN(pixelUtilities
							.getMaxSlopeAngleInDegrees(1, 1)));
					break;
				case 1:
					assertEquals(
							pixelUtilities.getMaxSlopeAngleInDegrees(1, 1), 0f);
					break;
				case 2:
				case 3:
					if (0 == i) {
						assertTrue(Math.abs(pixelUtilities
								.getMaxSlopeAngleInDegrees(1, 1)
								- 180 * Math.atan(5) / Math.PI) < EPSILON);
					} else if (1 == i) {
						assertEquals(pixelUtilities.getMaxSlopeAngleInDegrees(
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