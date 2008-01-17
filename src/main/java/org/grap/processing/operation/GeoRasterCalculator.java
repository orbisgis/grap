/*
 * The GRAP library (GeoRAster Processing) is a middleware dedicated
 * to the processing of various kinds of geographic raster data. It
 * provides a complete and robust API to manipulate ASCII Grid or
 * tiff, png, bmp, jpg (with the corresponding world file) geographic
 * images. GRAP is produced  by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GRAP.
 *
 * GRAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAP. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.grap.processing.operation;

import ij.ImagePlus;
import ij.measure.Calibration;
import ij.process.Blitter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.GrapImagePlus;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;

public class GeoRasterCalculator implements Operation {
	public static final int ADD = 3, SUBSTRACT = 4, MULTIPLY = 5, DIVIDE = 6,
			AND = 9, OR = 10, XOR = 11, MIN = 12, MAX = 13, AVERAGE = 7,
			DIFFERENCE = 8, COPY = 0;

	public static Map<String, Integer> operators = new HashMap<String, Integer>();
	static {
		operators.put("Add", ADD);
		operators.put("Substract", SUBSTRACT);
		operators.put("Multiply", MULTIPLY);
		operators.put("Divide", DIVIDE);
		operators.put("Average", AVERAGE);
		operators.put("Difference", DIFFERENCE);
		operators.put("And", AND);
		operators.put("Or", OR);
		operators.put("XOr", XOR);
		operators.put("Min", MIN);
		operators.put("Max", MAX);
		operators.put("Copy", COPY);
	}

	private GeoRaster gr2;

	private int method;

	public GeoRasterCalculator(final GeoRaster gr2, int method) {
		this.gr2 = gr2;
		this.method = method;

	}

	public GeoRaster execute(final GeoRaster gr1) throws OperationException,
			GeoreferencingException {

		try {
			GrapImagePlus img1 = gr1.getGrapImagePlus();

			GrapImagePlus img2 = gr2.getGrapImagePlus();

			if (gr1.getMetadata().getEnvelope().equals(
					gr2.getMetadata().getEnvelope())) {

				ImageProcessor ip1 = img1.getProcessor();
				ImageProcessor ip2 = img2.getProcessor();
				Calibration cal1 = img1.getCalibration();

				ip1.copyBits(ip2, 0, 0, method);

				if (!(ip1 instanceof ByteProcessor))
					ip1.resetMinAndMax();

				ImagePlus img3 = new ImagePlus("Result of "
						+ img1.getShortTitle(), ip1);
				img3.setCalibration(cal1);

				return GeoRasterFactory
						.createGeoRaster(img3, gr1.getMetadata());

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return GeoRasterFactory.createNullGeoRaster();
	}

	ImageProcessor createNewImage(ImageProcessor ip1, ImageProcessor ip2,
			Calibration cal) {
		int width = Math.min(ip1.getWidth(), ip2.getWidth());
		int height = Math.min(ip1.getHeight(), ip2.getHeight());
		ImageProcessor ip3 = ip1.createProcessor(width, height);

		ip3.insert(ip1, 0, 0);
		return ip3;
	}

}