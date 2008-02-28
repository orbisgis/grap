package org.grap.misc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.grap.io.GeoreferencingException;
import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

public class TransparencyTest extends GrapTest {
	private final static int RED = Color.RED.getRGB();

	private BufferedImage overlapRedBackGroundWithAGeoRaster(
			final GeoRaster geoRaster) throws IOException,
			GeoreferencingException {
		final BufferedImage redImage = new BufferedImage(geoRaster.getWidth(),
				geoRaster.getHeight(), BufferedImage.TYPE_INT_ARGB);
		final Graphics graphics = redImage.getGraphics();
		graphics.setColor(Color.RED);
		graphics.fillRect(0, 0, geoRaster.getWidth(), geoRaster.getHeight());

		graphics.drawImage(geoRaster.getGrapImagePlus().getImage(), 0, 0, null);

		System.out.println("Color model of the buffered image : "
				+ redImage.getColorModel());

		return redImage;
	}

	public void testTransparency() throws Exception {
		final GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
				+ "grid/sample.asc");
		gr.open();

		final int width = gr.getWidth();
		final int height = gr.getHeight();
		final BufferedImage bi = overlapRedBackGroundWithAGeoRaster(gr);
		// new ImagePlus("", bi).show();
		// for (int y = 0; y < height; y++) {
		// for (int x = 0; x < width; x++) {
		// if (Float.isNaN(gr.getGrapImagePlus().getPixelValue(x, y))) {
		// assertTrue(bi.getRGB(x, y) == RED);
		// }
		// }
		// }
		assertTrue(bi.getRGB(0, 0) == RED);
		assertTrue(bi.getRGB(width - 1, 0) == RED);
		assertTrue(bi.getRGB(0, height - 1) == RED);
		assertTrue(bi.getRGB(width - 1, height - 1) == RED);
	}

	public void testTransparencyOfLowestNegativePixelValue() throws Exception {
		final int ncols = 3;
		final int nrows = 3;
		final float noDataValue = Float.NaN;
		final GeoRaster gr = GeoRasterFactory.createGeoRaster(new float[] {
				noDataValue, 1, -2, 3, -4, 5, -6, 7, -8 }, ncols, nrows,
				new RasterMetadata(0.5, 0.5, 1, -1, ncols, nrows));
		gr.open();

		final int width = gr.getWidth();
		final int height = gr.getHeight();
		final BufferedImage bi = overlapRedBackGroundWithAGeoRaster(gr);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final float pixelValue = gr.getGrapImagePlus().getPixelValue(x,
						y);
				final int pixelColor = bi.getRGB(x, y);
				System.out.printf("%x (%g) ", pixelColor, pixelValue);
				if (Float.isNaN(pixelValue)) {
					assertTrue(pixelColor == RED);
				} else {
					// This is to test the opacity of pixel that have lowest
					// negative value - See bug ticket #3736
					assertTrue(pixelColor != RED);
				}
			}
			System.out.println();
		}
	}

	public void testTransparencyAndNoDataValues() throws Exception {
		final int ncols = 2;
		final int nrows = 1;

		for (float noDataValue : new float[] { Float.NaN, -9999, 0, 9999 }) {
			final GeoRaster gr = GeoRasterFactory.createGeoRaster(new float[] {
					noDataValue, 143 }, ncols, nrows, new RasterMetadata(0.5,
					0.5, 1, -1, ncols, nrows));
			gr.open();
			gr.setNodataValue(noDataValue);

			final BufferedImage bi = overlapRedBackGroundWithAGeoRaster(gr);
			final float pixelValue = gr.getGrapImagePlus().getPixelValue(0, 0);
			final int pixelColor = bi.getRGB(0, 0);
			System.out.printf("%x (%g)\n", pixelColor, pixelValue);
			assertTrue(Float.isNaN(pixelValue));
			assertTrue(pixelColor == RED);
		}
	}
}