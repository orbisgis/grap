package org.grap.processing.operation;

import java.io.IOException;

import ij.ImagePlus;
import ij.measure.Calibration;
import ij.process.ImageProcessor;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.IProgressMonitor;

public class GeoRasterResample implements Operation {

	private float size;

	public GeoRasterResample(float size) {
		this.size = size;
	}

	@Override
	public GeoRaster execute(GeoRaster geoRaster, IProgressMonitor pm)
			throws OperationException {
		try {
			RasterMetadata metadata = geoRaster.getMetadata();
			float size_origX = metadata.getPixelSize_X();
			float size_origY = metadata.getPixelSize_Y();

			float pixel_x = (size/size_origX );
			float pixel_y = (size/size_origY );

			ImagePlus img = geoRaster.getImagePlus();
			final ImageProcessor ip1 = img.getProcessor();
			ip1.scale(pixel_x, Math.abs(pixel_y));

			RasterMetadata newMetadata = new RasterMetadata(metadata
					.getXulcorner(), metadata.getYulcorner(), size, -size, ip1
					.getWidth(), ip1.getHeight(), metadata.getRotation_X(),
					metadata.getRotation_Y());
			return GeoRasterFactory.createGeoRaster(ip1, newMetadata);
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

}
