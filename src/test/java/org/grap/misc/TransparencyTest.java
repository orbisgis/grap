package org.grap.misc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

public class TransparencyTest extends GrapTest {
	private final static int RED = Color.RED.getRGB();

	public void testTransparency() throws Exception {
		final GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
				+ "grid/sample.asc");
		gr.open();

		final int width = gr.getWidth();
		final int height = gr.getHeight();
		final Image sampleImage = gr.getGrapImagePlus().getImage();

		final BufferedImage redImage = new BufferedImage(gr.getWidth(), gr
				.getHeight(), BufferedImage.TYPE_INT_ARGB);
		final Graphics graphics = redImage.getGraphics();
		graphics.setColor(Color.RED);
		graphics.fillRect(0, 0, width, height);

		graphics.drawImage(sampleImage, 0, 0, null);

		// new ImagePlus("", redImage).show();
		// for (int x = 0; x < width; x++) {
		// for (int y = 0; y < height; y++) {
		// if (Float.isNaN(gr.getGrapImagePlus().getPixelValue(x, y))) {
		// assertTrue(redImage.getRGB(x, y) == RED);
		// }
		// }
		// }
		assertTrue(redImage.getRGB(0, 0) == RED);
		assertTrue(redImage.getRGB(width - 1, 0) == RED);
		assertTrue(redImage.getRGB(0, height - 1) == RED);
		assertTrue(redImage.getRGB(width - 1, height - 1) == RED);
	}
}