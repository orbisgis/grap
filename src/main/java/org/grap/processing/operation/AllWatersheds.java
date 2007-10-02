package org.grap.processing.operation;

import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.cellularAutomata.CAGetAllSubWatershed;
import org.grap.processing.cellularAutomata.cam.IFloatCA;
import org.grap.processing.cellularAutomata.parallelImpl.CAN;

public class AllWatersheds implements Operation {
	public GeoRaster execute(final GeoRaster geoRaster) {
		final ImagePlus imp = geoRaster.getImagePlus();
		final RasterMetadata rasterMetadata = geoRaster.getRasterMetadata();
		final short[] pixels;
		final int nrows = rasterMetadata.getNRows();
		final int ncols = rasterMetadata.getNCols();

		if (ImagePlus.GRAY8 == geoRaster.getType()) {
			// TODO : verify doScaling
			pixels = (short[]) imp.getProcessor().convertToShort(false).getPixels();
		} else if (ImagePlus.GRAY16 == geoRaster.getType()) {
			pixels = (short[]) imp.getProcessor().getPixels();
		} else {
			throw new RuntimeException(
					"The DEM directions must be a GRAY16 or a GRAY32 image !");
		}

		final IFloatCA ca = new CAGetAllSubWatershed(pixels, nrows, ncols);
		CAN ccan = new CAN(ca);
		ccan.getStableState();
		
		final ImageProcessor ipResult = new FloatProcessor(ncols, nrows,
				(float[]) ccan.getCANValues(), null);
		return new GeoRaster(new ImagePlus("", ipResult), rasterMetadata);
	}
}