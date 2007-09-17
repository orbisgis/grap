package org.grap.processing.operation;

import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;
import org.grap.processing.BasicOperation;
import org.grap.processing.Operation;

public class Slope extends BasicOperation implements Operation {
	public Slope() {
	}

	public Slope(GeoRaster geoRaster) {
		super(geoRaster);
	}

	public GeoRaster execute() {
		ImagePlus imp = geoRaster.getImagePlus();
		RasterMetadata rasterMetadata = geoRaster.getMetadata();
		final float[] pixels;
		final int nrows = rasterMetadata.nrows;
		final int ncols = rasterMetadata.ncols;
		final float[] slopes;
		ImageProcessor ipResult = null;
		int type = imp.getType();

		switch (type) {
		case ImagePlus.GRAY8:
			pixels = (float[]) imp.getProcessor().convertToFloat().getPixels();
			break;
		case ImagePlus.GRAY32:
			pixels = (float[]) imp.getProcessor().getPixels();
			SlopeFunction slope = new SlopeFunction(pixels, nrows, ncols);
			slope.computeSlopes();
			slopes = slope.getSlopesInPercent();
			ipResult = new FloatProcessor(ncols, nrows, slopes, null);
			break;
		}
		return new GeoRaster(new ImagePlus("", ipResult), rasterMetadata);
	}
}