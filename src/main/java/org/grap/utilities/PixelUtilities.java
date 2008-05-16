package org.grap.utilities;

import java.io.IOException;

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;

public class PixelUtilities {
	public final static short noDataValueForDirection = Short.MIN_VALUE;
	public final static float noDataValueForAngle = Float.NaN;

	public final static short indecisionDirection = -1;
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
	 * SAGA Manual : http://www.saga-gis.uni-goettingen.de/
	 * 
	 * 7 | 0 | 1 6 | x | 2 5 | 4 | 3
	 * 
	 * sink and flat areas pixels are equal to -1 nodataValue pixels are equal
	 * to Short.MIN_VALUE
	 */

	public PixelUtilities(final GeoRaster dem) throws GeoreferencingException,
			IOException {
		dem.open();
		ncols = dem.getMetadata().getNCols();
		nrows = dem.getMetadata().getNRows();
		pixels = dem.getGrapImagePlus().getFloatPixels();

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

	// private int[] neighbouringCellsIndexes(final int index) {
	// return new int[] { index + 1, index + ncols + 1, index + ncols,
	// index + ncols - 1, index - 1, index - ncols - 1, index - ncols,
	// index - ncols + 1 };
	// }

	private float[] getD8DirectionAndD8Slope(final int x, final int y) {
		final float currentElevation = getPixelValue(x, y);

		if (Float.isNaN(currentElevation)) {
			return new float[] { noDataValueForDirection, noDataValueForAngle };
		} else {
			if ((1 == x) && (1 == y)) {
				int zz =1;
			}
			final float[] ratios = new float[] {
					(currentElevation - getPixelValue(x + 1, y))
							* invD8Distances[0],
					(currentElevation - getPixelValue(x + 1, y + 1))
							* invD8Distances[1],
					(currentElevation - getPixelValue(x, y + 1))
							* invD8Distances[2],
					(currentElevation - getPixelValue(x - 1, y + 1))
							* invD8Distances[3],
					(currentElevation - getPixelValue(x - 1, y))
							* invD8Distances[4],
					(currentElevation - getPixelValue(x - 1, y - 1))
							* invD8Distances[5],
					(currentElevation - getPixelValue(x, y - 1))
							* invD8Distances[6],
					(currentElevation - getPixelValue(x + 1, y - 1))
							* invD8Distances[7] };
			final int tmpIdx = getIdxForMaxValue(ratios);
			if (-1 == tmpIdx) {
				// maybe an outlet or a sink
				return new float[] { indecisionDirection, indecisionAngle };
			} else {
				return new float[] { 1 << tmpIdx, ratios[tmpIdx] };
			}
		}
	}

	public short getD8Direction(final int x, final int y) {
		return (short) getD8DirectionAndD8Slope(x, y)[0];
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