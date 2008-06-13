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

		final float[] pixels = gr.getFloatPixels();
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

		final byte[] pixels = gr.getBytePixels();
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