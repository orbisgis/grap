package org.grap.processing.operation;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.cellularAutomata.CASlopesDirections;
import org.grap.processing.cellularAutomata.cam.IShortCA;
import org.grap.processing.cellularAutomata.parallelImpl.CAN;

public class SlopesDirections implements Operation {
	public GeoRaster execute() {
		throw new Error();
	}

	public GeoRaster execute(GeoRaster geoRaster) {
		final ImagePlus imp = geoRaster.getImagePlus();
		final RasterMetadata rasterMetadata = geoRaster.getMetadata();
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

		final IShortCA ca = new CASlopesDirections(pixels, nrows, ncols);
		CAN ccan = new CAN(ca);
		ccan.getStableState();
		
		final ImageProcessor ipResult = new ShortProcessor(ncols, nrows,
				(short[]) ccan.getCANValues(), null);
		// TODO : what about adding a ".convertToByte(false)" ?
		return new GeoRaster(new ImagePlus("", ipResult), rasterMetadata);
	}
}