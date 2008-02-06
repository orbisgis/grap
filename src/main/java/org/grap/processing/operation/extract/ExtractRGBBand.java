package org.grap.processing.operation.extract;

import java.io.IOException;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ColorProcessor;

import org.grap.io.GeoreferencingException;
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
				final ImageStack rgb = geoRaster.getGrapImagePlus().getStack();
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
		} catch (GeoreferencingException e) {
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