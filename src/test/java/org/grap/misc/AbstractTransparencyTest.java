package org.grap.misc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.grap.io.GeoreferencingException;
import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;

abstract public class AbstractTransparencyTest extends GrapTest {
	final static int RED = Color.RED.getRGB();

	BufferedImage overlapRedBackGroundWithAGeoRaster(final GeoRaster geoRaster)
			throws IOException, GeoreferencingException {
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
}