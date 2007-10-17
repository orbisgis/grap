package org.grap.processing.operation;

import ij.ImagePlus;

import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.cellularAutomata.CASlopesDirections;
import org.grap.processing.cellularAutomata.cam.CANFactory;
import org.grap.processing.cellularAutomata.cam.ICA;
import org.grap.processing.cellularAutomata.cam.ICAN;

public class SlopesDirections implements Operation {
	public GeoRaster execute(final GeoRaster geoRaster)
			throws OperationException {
		try {
			final RasterMetadata rasterMetadata = geoRaster.getMetadata();
			final float[] pixels;
			final int nrows = rasterMetadata.getNRows();
			final int ncols = rasterMetadata.getNCols();

			if (ImagePlus.GRAY16 == geoRaster.getType()) {
				pixels = (float[]) geoRaster.getPixelProvider()
						.getFloatPixels();
			} else if (ImagePlus.GRAY32 == geoRaster.getType()) {
				pixels = (float[]) geoRaster.getPixelProvider().getPixels();
			} else {
				throw new RuntimeException(
						"The DEM must be a GRAY16 or a GRAY32 image !");
			}

			final ICA ca = new CASlopesDirections(pixels, nrows, ncols);
			final ICAN ccan = CANFactory.createCAN(ca);
			ccan.getStableState();

			return GeoRasterFactory.createGeoRaster((short[]) ccan
					.getCANValues(), ncols, nrows, rasterMetadata);
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}
}