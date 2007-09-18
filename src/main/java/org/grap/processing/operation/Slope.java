package org.grap.processing.operation;

import java.io.IOException;

import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;
import org.grap.processing.BasicOperation;
import org.grap.processing.Operation;
import org.grap.processing.cellularAutomata.ACAN;
import org.grap.processing.cellularAutomata.CAGetAllSubWatershed;
import org.grap.processing.cellularAutomata.CASlopesAccumulation;
import org.grap.processing.cellularAutomata.CASlopesDirections;
import org.grap.processing.cellularAutomata.FloatCAN;
import org.grap.processing.cellularAutomata.IFloatCA;
import org.grap.processing.cellularAutomata.IShortCA;
import org.grap.processing.cellularAutomata.ShortCAN;

public class Slope extends BasicOperation implements Operation {
	public Slope(GeoRaster geoRaster) {
		super(geoRaster);
	}

	public GeoRaster execute() {
		ImagePlus imp = geoRaster.getImagePlus();
		RasterMetadata rasterMetadata = geoRaster.getMetadata();
		final float[] pixels;
		final int nrows = rasterMetadata.getNRows();
		final int ncols = rasterMetadata.getNCols();
		ImageProcessor ipResult = null;
		int type = imp.getType();

		switch (type) {
		case ImagePlus.GRAY8:
			pixels = (float[]) imp.getProcessor().convertToFloat().getPixels();
			break;
		case ImagePlus.GRAY32:
			pixels = (float[]) imp.getProcessor().getPixels();

			final IShortCA ca1 = new CASlopesDirections(pixels, nrows, ncols);
			final ACAN can1 = new ShortCAN(ca1);
			can1.getStableState();

			final short[] slopesDirections = (short[]) can1.getValuesSnapshot();

			// final IFloatCA ca3 = new CASlopesAccumulation(slopesDirections,
			// nrows, ncols);
			// final ACAN can3 = new FloatCAN(ca3);
			// can3.getStableState();

			final IFloatCA ca4 = new CAGetAllSubWatershed(slopesDirections, nrows,
					ncols);
			final ACAN can4 = new FloatCAN(ca4);
			can4.getStableState();

			
			// SlopeFunction slope = new SlopeFunction(pixels, nrows, ncols);
			// slope.computeSlopes();
			// slopes = slope.getSlopesInPercent();
			// slopes = (int[]) can3.getValuesSnapshot();
			// ipResult = new FloatProcessor(ncols, nrows, slopes, null);
			ipResult = new FloatProcessor(ncols, nrows, (float[]) can4
					.getValuesSnapshot(), null);
			
			try {
				new GeoRaster(new ImagePlus("", ipResult), rasterMetadata)
						.save("/tmp/ws.tif");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		return new GeoRaster(new ImagePlus("", ipResult), rasterMetadata);
	}
}