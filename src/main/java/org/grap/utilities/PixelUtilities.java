package org.grap.utilities;

import java.io.IOException;

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;

public class PixelUtilities {
	public final static short noDataValue = Short.MIN_VALUE;

	public final static short indecisionDirection = -1;
	public final static float indecisionAngle = 0;

	private float[] pixels;
	private int ncols;
	private int nrows;

	private float[] invD8Distances;

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

	private float[] getMaxSlopeDirectionAndAngleInRadians(final int x,
			final int y) {
		final float currentElevation = getPixelValue(x, y);

		if (Float.isNaN(currentElevation)) {
			return new float[] { Float.NaN, Float.NaN };
		} else {
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
				return new float[] { indecisionDirection, indecisionAngle };
			} else {
				final float tmpDir = 1 << tmpIdx;
				final float tmpAngle = (float) Math.atan(ratios[tmpIdx]);
				return new float[] { tmpDir, tmpAngle };
			}
		}
	}

	public short getMaxSlopeDirection(final int x, final int y) {
		float tmp = getMaxSlopeDirectionAndAngleInRadians(x, y)[0];
		return Float.isNaN(tmp) ? noDataValue : (short) tmp;
	}

	public float getMaxSlopeAngleInRadians(final int x, final int y) {
		return getMaxSlopeDirectionAndAngleInRadians(x, y)[1];
	}

	public float getMaxSlopeAngleInDegrees(final int x, final int y) {
		return (float) ((180 * getMaxSlopeAngleInRadians(x, y)) / Math.PI);
	}

	// public float getMaxSlopeAngleInPercent(final int x, final int y) {
	// return (float) ((400 * getMaxSlopeAngleInRadians(x, y)) / Math.PI);
	// }
}