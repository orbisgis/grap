/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.ProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;

public class GeoRasterCalculator implements Operation {
	public static final int ADD = 3, SUBSTRACT = 4, MULTIPLY = 5, DIVIDE = 6,
			AND = 9, OR = 10, XOR = 11, MIN = 12, MAX = 13, AVERAGE = 7,
			DIFFERENCE = 8, COPY = 0;

	public static Map<String, Integer> operators = new HashMap<String, Integer>();
	static {
		operators.put("add", ADD);
		operators.put("substract", SUBSTRACT);
		operators.put("multiply", MULTIPLY);
		operators.put("divide", DIVIDE);
		operators.put("avg", AVERAGE);
		operators.put("diff", DIFFERENCE);
		operators.put("and", AND);
		operators.put("or", OR);
		operators.put("xor", XOR);
		operators.put("min", MIN);
		operators.put("max", MAX);
		operators.put("copy", COPY);
	}

	private GeoRaster gr2;
	private int method;

	public GeoRasterCalculator(final GeoRaster gr2, final int method) {
		this.gr2 = gr2;
		this.method = method;
	}

	public GeoRaster execute(GeoRaster gr1, ProgressMonitor pm)
			throws OperationException {
		try {

			RasterMetadata gr1Metadata = gr1.getMetadata();
			RasterMetadata gr2Metadata = gr2.getMetadata();

			Envelope gr1Envelope = gr1Metadata.getEnvelope();
			Envelope gr2Envelope = gr2Metadata.getEnvelope();

			if (gr1Envelope.equals(gr2Envelope)) {
				return applyRasterAlgebra(gr1, gr2);

			} else if (gr1Envelope.intersects(gr2Envelope)) {

				Envelope grResultEnvelope = gr1Envelope
						.intersection(gr2Envelope);
				if (gr1Envelope.contains(gr2Envelope)) {

					gr2 = cropGeoRaster(gr2, grResultEnvelope);
					return applyRasterAlgebra(gr1, gr2);

				} else if (gr2Envelope.contains(gr1Envelope)) {
					gr1 = cropGeoRaster(gr1, grResultEnvelope);
					return applyRasterAlgebra(gr1, gr2);

				} else {
					gr1 = cropGeoRaster(gr1, grResultEnvelope);
					gr2 = cropGeoRaster(gr2, grResultEnvelope);
					return applyRasterAlgebra(gr1, gr2);
				}

			}
		} catch (IOException e) {
			throw new OperationException(e);
		}
		return GeoRasterFactory.createNullGeoRaster();
	}

	private GeoRaster applyRasterAlgebra(GeoRaster gr1, GeoRaster gr2)
			throws IOException {
		final ImagePlus img1 = gr1.getImagePlus();
		final ImagePlus img2 = gr2.getImagePlus();

		final ImageProcessor ip1 = img1.getProcessor();
		final ImageProcessor ip2 = img2.getProcessor();
		final Calibration cal1 = img1.getCalibration();
		ip1.copyBits(ip2, 0, 0, method);
		if (!(ip1 instanceof ByteProcessor)) {
			ip1.resetMinAndMax();
		}
		final ImagePlus img3 = new ImagePlus("Result of "
				+ img1.getShortTitle(), ip1);
		img3.setCalibration(cal1);

		return GeoRasterFactory.createGeoRaster(img3, gr1.getMetadata());

	}

	private GeoRaster cropGeoRaster(GeoRaster gr2, Envelope intersection)
			throws OperationException {
		Rectangle2D rect = new Rectangle2D.Double(intersection.getMinX(),
				intersection.getMinY(), intersection.getWidth(), intersection
						.getHeight());

		GeoRaster ret = gr2.doOperation(new Crop(rect));
		return ret;
	}

}
