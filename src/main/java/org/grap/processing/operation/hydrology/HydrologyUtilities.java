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
package org.grap.processing.operation.hydrology;

import java.io.IOException;

import org.grap.model.GeoRaster;

public class HydrologyUtilities {
	public final static float noDataValueForDirection = Byte.MIN_VALUE;
	public final static float noDataValueForAngle = Float.NaN;

	public final static float indecisionDirection = -1;
	public final static float indecisionAngle = 0;

	private final static double FACTOR = 180 / Math.PI;

	private float[] pixels;
	private int ncols;
	private int nrows;

	private float[] invD8Distances;

	/**
	 * Implementation of some classical D8 analysis algorithms. D8 stands for
	 * "Deterministic eight neighbour" method by O’Callaghan & Mark (1984)
	 *
	 * The standard we have decided to implement is the one explained by David
	 * G. Tarboton (Utah State University, May, 2005) in the "Terrain Analysis
	 * Using Digital Elevation Models" (TauDEM) method.
	 *
	 * 4 | 3 | 2
	 *
	 * 5 | X | 1
	 *
	 * 6 | 7 | 8
	 *
	 * sink and flat areas pixels are equal to -1
	 *
	 * nodataValue pixels are equal to Short.MIN_VALUE
	 */

	public HydrologyUtilities(final GeoRaster dem)
			throws IOException {
		ncols = dem.getMetadata().getNCols();
		nrows = dem.getMetadata().getNRows();
		pixels = dem.getFloatPixels();

		float invPixelSize_X = 1 / dem.getMetadata().getPixelSize_X();
		float invPixelSize_Y = Math.abs(1 / dem.getMetadata().getPixelSize_Y());
		float invHypotenuse = (float) (1 / Math.sqrt(dem.getMetadata()
				.getPixelSize_X()
				* dem.getMetadata().getPixelSize_X()
				+ dem.getMetadata().getPixelSize_Y()
				* dem.getMetadata().getPixelSize_Y()));

		invD8Distances = new float[] { invPixelSize_X, invHypotenuse,
				invPixelSize_Y, invHypotenuse, invPixelSize_X, invHypotenuse,
				invPixelSize_Y, invHypotenuse };
	}

	private float getPixelValue(final int x, final int y) {
		return ((0 > y) || (nrows <= y) || (0 > x) || (ncols <= x)) ? Float.NaN
				: pixels[y * ncols + x];
	}

	private static int getIdxForMaxValue(final float[] values) {
		float max = 0;
		int result = -1;
		for (int i = 0; i < values.length; i++) {
			if ((!Float.isNaN(values[i])) && (values[i] > max)) {
				result = i;
				max = values[i];
			}
		}
		return result;
	}

	private float[] getD8DirectionAndD8Slope(final int x, final int y) {
		final float currentElevation = getPixelValue(x, y);

		if (Float.isNaN(currentElevation)
				|| ((0 == y) || (nrows == y + 1) || (0 == x) || (ncols == x + 1))) {
			return new float[] { noDataValueForDirection, noDataValueForAngle };
		} else {
			final float[] ratios = new float[] {
					(currentElevation - getPixelValue(x + 1, y))
							* invD8Distances[0],
					(currentElevation - getPixelValue(x + 1, y - 1))
							* invD8Distances[1],
					(currentElevation - getPixelValue(x, y - 1))
							* invD8Distances[2],
					(currentElevation - getPixelValue(x - 1, y - 1))
							* invD8Distances[3],
					(currentElevation - getPixelValue(x - 1, y))
							* invD8Distances[4],
					(currentElevation - getPixelValue(x - 1, y + 1))
							* invD8Distances[5],
					(currentElevation - getPixelValue(x, y + 1))
							* invD8Distances[6],
					(currentElevation - getPixelValue(x + 1, y + 1))
							* invD8Distances[7] };
			final int tmpIdx = getIdxForMaxValue(ratios);
			if (-1 == tmpIdx) {
				// maybe an outlet or a sink
				return new float[] { indecisionDirection, indecisionAngle };
			} else {
				return new float[] { 1 + tmpIdx, ratios[tmpIdx] };
				// return new float[] { 1 << tmpIdx, ratios[tmpIdx] };
			}
		}
	}

	public float getD8Direction(final int x, final int y) {
		return getD8DirectionAndD8Slope(x, y)[0];
	}

	public float getSlope(final int x, final int y) {
		return getD8DirectionAndD8Slope(x, y)[1];
	}

	public float getSlopeInRadians(final int x, final int y) {
		return (float) Math.atan(getSlope(x, y));
	}

	public float getSlopeInDegrees(final int x, final int y) {
		return (float) (FACTOR * getSlopeInRadians(x, y));
	}
}