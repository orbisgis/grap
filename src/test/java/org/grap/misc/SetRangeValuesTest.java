package org.grap.misc;

import ij.ImagePlus;

import java.awt.image.BufferedImage;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

public class SetRangeValuesTest extends AbstractTransparencyTest {
	public void testSetRangeValues() throws Exception {
		final double min = 75;
		final double max = 100;
		final GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
				+ "grid/sample.asc");
		gr.open();
		gr.setRangeValues(min, max);
		assertTrue(ImagePlus.GRAY32 == gr.getType());

		final int width = gr.getWidth();
		final int height = gr.getHeight();
		final BufferedImage bi = overlapRedBackGroundWithAGeoRaster(gr);

		new ImagePlus("", bi).show();

		final float[] pixels = (float[]) gr.getGrapImagePlus().getPixels();
		for (int y = 0, i = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (Float.isNaN(pixels[i]) || (pixels[i] < min)
						|| (pixels[i] > max)) {
					assertTrue(bi.getRGB(x, y) == RED);
				} else {
					if (bi.getRGB(x, y) == RED) {
						System.out.printf("[%d, %d] %x (%g)\n", x, y, bi
								.getRGB(x, y), pixels[i]);
					}
					// assertFalse(bi.getRGB(x, y) == RED);
				}
				i++;
			}
		}
	}

	public void testSetRangeValues2() throws Exception {
		final double min = 99;
		final double max = 100;
		final GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
				+ "geotif/440606.tif");
		gr.open();
		gr.setRangeValues(min, max);
		assertTrue(ImagePlus.GRAY8 == gr.getType());

		final int width = gr.getWidth();
		final int height = gr.getHeight();
		final BufferedImage bi = overlapRedBackGroundWithAGeoRaster(gr);

		new ImagePlus("", bi).show();

		final byte[] pixels = (byte[]) gr.getGrapImagePlus().getPixels();
		for (int y = 0, i = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (Float.isNaN(pixels[i]) || (pixels[i] < min)
						|| (pixels[i] > max)) {
					if (bi.getRGB(x, y) != 0xffffffff /* RED */) {
						System.out.printf("** [%d, %d] %x (%d)\n", x, y, bi
								.getRGB(x, y), pixels[i]);
					}
					// assertTrue(bi.getRGB(x, y) == RED);
				} else {
					if (bi.getRGB(x, y) == RED) {
						System.out.printf("[%d, %d] %x (%d)\n", x, y, bi
								.getRGB(x, y), pixels[i]);
					}
					// assertFalse(bi.getRGB(x, y) == RED);
				}
				i++;
			}
		}
		// final GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
		// + "/geotif/440606.tif");
		// gr.open();
		//
		// gr.setRangeValues(75, 100);
		//
		// gr.show();
	}
}