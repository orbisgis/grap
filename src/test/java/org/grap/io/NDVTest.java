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
package org.grap.io;

import org.junit.Test;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

import static org.junit.Assert.*;

public class NDVTest extends GrapTest {

        @Test
	public void testSourceWithNDV() throws Exception {
		testNDV(otherData + "sample.asc", -9999.0f);
	}

        @Test
	public void testSourceWithoutNDV() throws Exception {
		testNDV(internalData + "geotif/440606.tif", Float.NaN);
	}

	private void testNDV(String source, float ndv)
			throws FileNotFoundException, IOException {
		GeoRaster gr = GeoRasterFactory.createGeoRaster(source);
		gr.open();
		if (!Float.isNaN(ndv)) {
			assertTrue(gr.getMetadata().getNoDataValue() == ndv);
		} else {
			assertTrue(Float.isNaN(gr.getMetadata().getNoDataValue()));
		}
	}

        @Test
	public void testSetNDVToSourceWithout() throws Exception {
		GeoRaster gr = GeoRasterFactory.createGeoRaster(internalData
				+ "geotif/440606.tif");
		gr.open();
		gr.setNodataValue(4.3f);
		assertTrue(gr.getNoDataValue() == 4.3f);
	}

        @Test
	public void testNDVWithEsriGRIDReader() throws Exception {
		final GeoRaster gr = GeoRasterFactory.createGeoRaster(otherData
				+ "sample.asc");
		gr.open();
		float[] pixels = gr.getFloatPixels();
		int originalNDV = ndvCount(pixels);
		assertTrue(0 < originalNDV);

		gr.setNodataValue(Float.MAX_VALUE);
		pixels = gr.getFloatPixels();
		assertTrue(originalNDV == ndvCount(pixels));

		gr.setNodataValue((float) gr.getMin());
		pixels = gr.getFloatPixels();
		assertTrue(originalNDV < ndvCount(pixels));
	}

        @Test
	public void testNDVFromProcessor() throws Exception {
		GeoRaster gr = GeoRasterFactory.createGeoRaster(otherData
				+ "sample.asc");
		gr.open();
		gr.setNodataValue((float) gr.getMin());
		float[] pixels = gr.getFloatPixels();
		int nanCount = ndvCount(pixels);
		pixels = (float[]) gr.getImagePlus().getProcessor().getPixels();
		assertTrue(nanCount == ndvCount(pixels));
	}

	private int ndvCount(float[] pixels) {
		int nanCount = 0;
		for (float f : pixels) {
			if (f == GeoRaster.FLOAT_NO_DATA_VALUE) {
				nanCount++;
			}
		}
		return nanCount;
	}

        @Test
	public void testMinMaxAndNDV() throws Exception {
		GeoRaster gr = GeoRasterFactory.createGeoRaster(otherData
				+ "sample.asc");
		gr.open();
		float originalMin = (float) gr.getMin();
		assertTrue(gr.getMetadata().getNoDataValue() == -9999);
		assertTrue(gr.getMin() != gr.getNoDataValue());

		// Change no data value to min
		gr.setNodataValue(originalMin);
		assertTrue(gr.getNoDataValue() == originalMin);
		assertTrue(gr.getMin() != gr.getNoDataValue());
	}

        @Test
	public void testNDVMinMax() throws Exception {
		RasterMetadata md = new RasterMetadata(0, 0, 0, 0, 2, 2);
		testNDVMinMax(GeoRasterFactory.createGeoRaster(new byte[] { -3, -4, 5,
				6 }, md));
		testNDVMinMax(GeoRasterFactory.createGeoRaster(new short[] { -3, -4, 5,
				6 }, md));
		testNDVMinMax(GeoRasterFactory.createGeoRaster(new float[] { -3, -4, 5,
				6 }, md));
	}

	private void testNDVMinMax(final GeoRaster gr) throws IOException {
		gr.open();
		double min = gr.getMin();
		gr.setNodataValue((float) (gr.getMin() + 1));
		assertTrue(gr.getMin() == min);
	}

        @Test
	public void testMinMax() throws Exception {
		GeoRaster gr = GeoRasterFactory.createGeoRaster(internalData
				+ "/geotif/littlelehavre.tif");

		gr.open();
		assertTrue(gr.getMin() < gr.getMax());
	}

        @Test
	public void testNDVForRGB() throws Exception {
		RasterMetadata md = new RasterMetadata(0, 0, 0, 0, 2, 2);
		GeoRaster gr = GeoRasterFactory.createGeoRaster(new int[] { -3, -4, 5,
				6 }, md);
		gr.open();
		try {
			gr.setNodataValue(23);
			fail();
		} catch (UnsupportedOperationException e) {
		}
		try {
			gr.setRangeValues(23, 24);
			fail();
		} catch (UnsupportedOperationException e) {
		}
	}

        @Test
	public void testMinMaxRGB() throws Exception {
		RasterMetadata md = new RasterMetadata(0, 0, 0, 0, 2, 2);
		GeoRaster gr = GeoRasterFactory.createGeoRaster(new int[] { -3, -4, 5,
				6 }, md);
		gr.open();
		assertTrue(gr.getMin() == -4);
		assertTrue(gr.getMax() == 6);
	}

        @Test
	public void testMinMaxFloat() throws Exception {
		RasterMetadata md = new RasterMetadata(0, 0, 0, 0, 2, 2);
		GeoRaster gr = GeoRasterFactory.createGeoRaster(new float[] { -3, -4,
				-5, -6 }, md);
		gr.open();
		assertTrue(gr.getMin() == -6);
		assertTrue(gr.getMax() == -3);
	}

        @Test
	public void testInitialNDVArray() throws Exception {
		float ndv = 3;

		GeoRaster gr = GeoRasterFactory.createGeoRaster(new float[] { 1, 2, 3,
				4, 5, 6, 7, 8, 9 }, new RasterMetadata(0, 0, 1, 1, 3, 3, ndv));
		gr.open();

		boolean someNDV = false;
		for (float pv : gr.getFloatPixels()) {
			if (pv == GeoRaster.FLOAT_NO_DATA_VALUE) {
				someNDV = true;
			}
		}

		assertTrue(someNDV);
	}

        @Test
	public void testFloatRangeValue() throws Exception {
		GeoRaster gr = GeoRasterFactory.createGeoRaster(new float[] { 1, 2, 3,
				4, 5, 6, 7, 8, -9999 }, new RasterMetadata(0, 0, 1, 1, 3, 3,
				GeoRaster.FLOAT_NO_DATA_VALUE));
		gr.open();

		testRangeValues(gr, GeoRaster.FLOAT_NO_DATA_VALUE);
	}

	//
	// TODO This tests are related to a strange behaviour in
	// ShortProcessor.getPixelValue in ImageJ. When ImageJ
	// interface is removed from grap we must uncomment and fix them
	//
	// public void testShortRangeValue() throws Exception {
	// GeoRaster gr = GeoRasterFactory.createGeoRaster(new short[] { 1, 2, 3,
	// 4, 5, 6, 7, 8, GeoRaster.SHORT_NO_DATA_VALUE },
	// new RasterMetadata(0, 0, 1, 1, 3, 3,
	// GeoRaster.SHORT_NO_DATA_VALUE));
	// gr.open();
	//
	// testRangeValues(gr, GeoRaster.SHORT_NO_DATA_VALUE);
	// }
	//
	// public void testByteRangeValue() throws Exception {
	// GeoRaster gr = GeoRasterFactory.createGeoRaster(new short[] { 1, 2, 3,
	// 4, 5, 6, 7, 8, GeoRaster.BYTE_NO_DATA_VALUE },
	// new RasterMetadata(0, 0, 1, 1, 3, 3,
	// GeoRaster.BYTE_NO_DATA_VALUE));
	// gr.open();
	//
	// testRangeValues(gr, GeoRaster.BYTE_NO_DATA_VALUE);
	// }

	private void testRangeValues(GeoRaster gr, float ndv) throws IOException {
		double min = 3;
		double max = 5;
		gr.setRangeValues(min, max);

		for (int i = 0; i < gr.getWidth(); i++) {
			for (int j = 0; j < gr.getHeight(); j++) {
				float value = gr.getImagePlus().getProcessor().getPixelValue(i,
						j);
				if (value == ndv) {
					continue;
				} else if ((value >= min) && (value <= max)) {
					assertTrue(true);
				} else {
					assertTrue(false);
				}

			}
		}
	}
}
