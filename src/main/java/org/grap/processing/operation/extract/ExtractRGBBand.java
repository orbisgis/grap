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
package org.grap.processing.operation.extract;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ColorProcessor;

import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.OperationException;

public class ExtractRGBBand {
	private GeoRaster geoRaster;
	private ImageStack red;
	private ImageStack green;
	private ImageStack blue;

	public ExtractRGBBand(GeoRaster geoRaster) {
		this.geoRaster = geoRaster;

	}

	public void extractBands() throws OperationException {
		try {
			if (geoRaster.getType() == ImagePlus.COLOR_RGB) {
				final ImageStack rgb = geoRaster.getImagePlus().getStack();
				final int w = rgb.getWidth();
				final int h = rgb.getHeight();
				red = new ImageStack(w, h);
				green = new ImageStack(w, h);
				blue = new ImageStack(w, h);

				final int slice = 1;
				final int n = rgb.getSize();

				for (int i = 1; i <= n; i++) {
					final byte[] r = new byte[w * h];
					final byte[] g = new byte[w * h];
					final byte[] b = new byte[w * h];
					final ColorProcessor cp = (ColorProcessor) rgb
							.getProcessor(slice);
					cp.getRGB(r, g, b);

					red.addSlice(null, r);
					green.addSlice(null, g);
					blue.addSlice(null, b);
				}
			}
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	public GeoRaster getRedBand() {
		return GeoRasterFactory.createGeoRaster(new ImagePlus("red", red),
				geoRaster.getMetadata());
	}

	public GeoRaster getBlueBand() {
		return GeoRasterFactory.createGeoRaster(new ImagePlus("blue", blue),
				geoRaster.getMetadata());
	}

	public GeoRaster getGreenBand() {
		return GeoRasterFactory.createGeoRaster(new ImagePlus("green", green),
				geoRaster.getMetadata());
	}
}