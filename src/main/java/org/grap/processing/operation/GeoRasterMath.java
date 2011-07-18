/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
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

public class GeoRasterMath implements Operation {
	public static final int ADD = 3, SUBSTRACT = 4, MULTIPLY = 5, DIVIDE = 6,
			AND = 9, MIN = 12, MAX = 13, AVERAGE = 7, DIFFERENCE = 8, OR = 16,
			XOR = 32, ABS = 128, SQR = 256, SQRT = 512, EXP = 1024;

	public static Map<String, Integer> operators = new HashMap<String, Integer>();
	static {
		operators.put("abs", ABS);
		operators.put("add", ADD);
		operators.put("multiply", MULTIPLY);
		operators.put("divide", DIVIDE);
		operators.put("min", MIN);
		operators.put("max", MAX);
		operators.put("substract", SUBSTRACT);
		operators.put("or", OR);
		operators.put("xor", XOR);
		operators.put("sqr", SQR);
		operators.put("sqrt", SQRT);
		operators.put("exp", EXP);
	}

	private int method;

	private double value;

	public GeoRasterMath(final double value, final int method) {
		this.method = method;
		this.value = value;
	}

	public GeoRasterMath(final int method) {
		new GeoRasterMath(0, method);
	}

	public GeoRaster execute(final GeoRaster gr1, ProgressMonitor pm)
			throws OperationException {
		try {
			final ImagePlus img1 = gr1.getImagePlus();

			final ImageProcessor ip1 = img1.getProcessor();
			final Calibration cal1 = img1.getCalibration();

			switch (method) {
			case ADD:
				ip1.add(value);
				break;
			case DIVIDE:
				ip1.multiply(1.0 / value);
				break;
			case MIN:
				ip1.min(value);
				break;
			case MAX:
				ip1.max(value);
				break;
			case MULTIPLY:
				ip1.multiply(value);
				break;
			case SUBSTRACT:
				ip1.add(-value);
				break;
			case OR:
				ip1.or((int) value);
				break;
			case XOR:
				ip1.xor((int) value);
				break;
			case ABS:
				ip1.abs();
				break;
			case EXP:
				ip1.exp();
				break;
			case SQR:
				ip1.sqr();
				break;
			case SQRT:
				ip1.sqrt();
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
