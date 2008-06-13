/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.grap.misc;

import java.awt.image.BufferedImage;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

public class TransparencyTest extends AbstractTransparencyTest {
	public void testTransparency() throws Exception {
		final GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
				+ "grid/sample.asc");
		gr.open();

		final int width = gr.getWidth();
		final int height = gr.getHeight();
		final BufferedImage bi = overlapRedBackGroundWithAGeoRaster(gr);

		// new ImagePlus("", bi).show();

		final float[] pixels = gr.getFloatPixels();
		for (int y = 0, i = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (Float.isNaN(pixels[i])) {
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
				noDataValue, 1, -2, 3, -4, 5, -6, 7, -8 }, new RasterMetadata(
				0.5, 0.5, 1, -1, ncols, nrows));
		gr.open();

		final int width = gr.getWidth();
		final int height = gr.getHeight();
		final BufferedImage bi = overlapRedBackGroundWithAGeoRaster(gr);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final float pixelValue = gr.getImagePlus().getProcessor()
						.getPixelValue(x, y);
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
					noDataValue, 143 }, new RasterMetadata(0.5, 0.5, 1, -1,
					ncols, nrows));
			gr.open();
			gr.setNodataValue(noDataValue);

			final BufferedImage bi = overlapRedBackGroundWithAGeoRaster(gr);
			final float pixelValue = gr.getImagePlus().getProcessor()
					.getPixelValue(0, 0);
			final int pixelColor = bi.getRGB(0, 0);
			System.out.printf("%x (%g)\n", pixelColor, pixelValue);
			assertTrue(Float.isNaN(pixelValue));
			assertTrue(pixelColor == RED);
		}
	}
}