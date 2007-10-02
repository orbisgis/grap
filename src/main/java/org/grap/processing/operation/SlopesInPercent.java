package org.grap.processing.operation;

import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.cellularAutomata.CASlopesInPercent;
import org.grap.processing.cellularAutomata.cam.IFloatCA;
import org.grap.processing.cellularAutomata.parallelImpl.CAN;

public class SlopesInPercent implements Operation {
	public GeoRaster execute(final GeoRaster geoRaster) {
		final ImagePlus imp = geoRaster.getImagePlus();
		final RasterMetadata rasterMetadata = geoRaster.getRasterMetadata();
		final float[] pixels;
		final int nrows = rasterMetadata.getNRows();
		final int ncols = rasterMetadata.getNCols();

		if (ImagePlus.GRAY16 == geoRaster.getType()) {
			pixels = (float[]) imp.getProcessor().convertToFloat().getPixels();
		} else if (ImagePlus.GRAY32 == geoRaster.getType()) {
			pixels = (float[]) imp.getProcessor().getPixels();
		} else {
			throw new RuntimeException(
					"The DEM must be a GRAY16 or a GRAY32 image !");
		}

		final IFloatCA ca = new CASlopesInPercent(pixels, nrows, ncols);
		CAN ccan = new CAN(ca);
		ccan.getStableState();
		
		final ImageProcessor ipResult = new FloatProcessor(ncols, nrows,
				(float[]) ccan.getCANValues(), null);
		return new GeoRaster(new ImagePlus("", ipResult), rasterMetadata);
	}
}