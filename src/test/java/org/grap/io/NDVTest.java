package org.grap.io;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

public class NDVTest extends GrapTest {

	public void testSourceWithNDV() throws Exception {
		testNDV(externalData + "grid/sample.asc", -9999.0f);
	}

	public void testSourceWithoutNDV() throws Exception {
		testNDV(externalData + "geotif/440606.tif", Float.NaN);
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

	public void testSetNDVToSourceWithout() throws Exception {
		GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
				+ "geotif/440606.tif");
		gr.open();
		gr.setNodataValue(4.3f);
		assertTrue(gr.getNoDataValue() == 4.3f);
	}

	public void testNDVWithEsriGRIDReader() throws Exception {
		final GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
				+ "grid/sample.asc");
		gr.open();
		float[] pixels = gr.getFloatPixels();
		assertTrue(0 < nanCount(pixels));

		gr.setNodataValue(Float.MAX_VALUE);
		pixels = gr.getFloatPixels();
		assertEquals(0, nanCount(pixels));
	}

	public void testNaN() throws Exception {
		GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
				+ "grid/sample.asc");
		gr.open();
		gr.setNodataValue((float) gr.getMin());
		float[] pixels = gr.getFloatPixels();
		int nanCount = nanCount(pixels);
		pixels = (float[]) gr.getImagePlus().getProcessor().getPixels();
		assertTrue(nanCount == nanCount(pixels));

	}

	private int nanCount(float[] pixels) {
		int nanCount = 0;
		for (float f : pixels) {
			if (Float.isNaN(f)) {
				nanCount++;
			}
		}
		return nanCount;
	}

	public void testMinMaxAndNDV() throws Exception {
		GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
				+ "grid/sample.asc");
		gr.open();
		float originalMin = (float) gr.getMin();
		assertTrue(gr.getMetadata().getNoDataValue() == -9999);
		assertTrue(gr.getMin() != gr.getNoDataValue());

		// Change no data value to min
		gr.setNodataValue(originalMin);
		assertTrue(gr.getNoDataValue() == originalMin);
		assertTrue(gr.getMin() != gr.getNoDataValue());
	}

	public void testNDVMinMax() throws Exception {
		RasterMetadata md = new RasterMetadata(0, 0, 0, 0, 2, 2);
		testNDVMinMax(GeoRasterFactory.createGeoRaster(new byte[] { -3, -4, 5,
				6 }, md));
		testNDVMinMax(GeoRasterFactory.createGeoRaster(new short[] { -3, -4, 5,
				6 }, md));
		testNDVMinMax(GeoRasterFactory.createGeoRaster(
				new int[] { -3, -4, 5, 6 }, md));
		testNDVMinMax(GeoRasterFactory.createGeoRaster(new float[] { -3, -4, 5,
				6 }, md));
	}

	private void testNDVMinMax(final GeoRaster gr) throws IOException {
		gr.open();
		double min = gr.getMin();
		gr.setNodataValue((float) (gr.getMin() + 1));
		assertTrue(gr.getMin() == min);
	}

}
