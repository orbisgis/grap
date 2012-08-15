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
import org.orbisgis.progress.ProgressMonitor;

public class GeoRasterResample implements Operation {

	private float size;

	public GeoRasterResample(float size) {
		this.size = size;
	}

	@Override
	public GeoRaster execute(GeoRaster geoRaster, ProgressMonitor pm)
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
