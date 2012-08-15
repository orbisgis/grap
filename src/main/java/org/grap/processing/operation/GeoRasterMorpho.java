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

import ij.ImagePlus;
import ij.measure.Calibration;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.ProgressMonitor;

public class GeoRasterMorpho implements Operation {
	public static final int DILATE = 1, ERODE = 2, SMOOTH = 4, EDGES = 8,
			INVERT = 16;

	public static Map<String, Integer> operators = new HashMap<String, Integer>();
	static {
		operators.put("erode", ERODE);
		operators.put("dilate", DILATE);
		operators.put("smooth", SMOOTH);
		operators.put("edges", EDGES);
		operators.put("invert", INVERT);

	}

	private int method;

	public GeoRasterMorpho(final int method) {
		this.method = method;
	}

	public GeoRaster execute(final GeoRaster gr1, ProgressMonitor pm)
			throws OperationException {
		try {
			final ImagePlus img1 = gr1.getImagePlus();

			final ImageProcessor ip1 = img1.getProcessor();
			final Calibration cal1 = img1.getCalibration();
			switch (method) {
			case ERODE:
				ip1.erode();
				break;
			case DILATE:
				ip1.dilate();
				break;
			case SMOOTH:
				ip1.smooth();
				break;
			case EDGES:
				ip1.findEdges();
				break;
			case INVERT:
				ip1.invert();
				break;
			default:
				break;
			}

			if (!(ip1 instanceof ByteProcessor)) {
				ip1.resetMinAndMax();
			}
			final ImagePlus img3 = new ImagePlus("Result of "
					+ img1.getShortTitle(), ip1);
			img3.setCalibration(cal1);

			return GeoRasterFactory.createGeoRaster(img3, gr1.getMetadata());

		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

}
