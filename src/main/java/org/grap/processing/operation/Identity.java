package org.grap.processing.operation;

import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.cellularAutomata.CAFIdentity;
import org.grap.processing.cellularAutomata.cam.CANFactory;
import org.grap.processing.cellularAutomata.cam.ICA;
import org.grap.processing.cellularAutomata.cam.ICAN;

public class Identity implements Operation {

	public GeoRaster execute(GeoRaster geoRaster) throws OperationException {
		try {
			final RasterMetadata rasterMetadata = geoRaster.getMetadata();
			final float[] pixels = geoRaster.getPixelProvider()
					.getFloatPixels();
			final int nrows = rasterMetadata.getNRows();
			final int ncols = rasterMetadata.getNCols();

			final ICA ca = new CAFIdentity(pixels, nrows, ncols);
			final ICAN ccan = CANFactory.createCAN(ca);
			ccan.getStableState();

			return GeoRasterFactory.createGeoRaster((float[]) ccan
					.getCANValues(), ncols, nrows, rasterMetadata);
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}
}