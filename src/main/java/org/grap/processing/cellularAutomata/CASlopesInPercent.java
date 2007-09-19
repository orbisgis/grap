package org.grap.processing.cellularAutomata;

import org.grap.processing.cellularAutomata.cam.IFloatCA;

public class CASlopesInPercent implements IFloatCA {
	private int nrows;

	private int ncols;

	private float[] DEM;

	private final static float SQRT2 = (float) Math.sqrt(2d);

	public CASlopesInPercent(final float[] DEM, final int nrows, final int ncols) {
		this.nrows = nrows;
		this.ncols = ncols;
		this.DEM = DEM;
	}

	public int getNCols() {
		return ncols;
	}

	public int getNRows() {
		return nrows;
	}

	public float init(int r, int c, int i) {
		final float currentElevation = DEM[i];

		if (Float.isNaN(currentElevation)) {
			// noDataValue
			return Float.NaN;
		} else {
			final float[] tmpSlopes = new float[] {
					currentElevation - getDEMValue(r, c + 1, i + 1),
					(currentElevation - getDEMValue(r + 1, c + 1, i + ncols + 1))
							/ SQRT2,
					currentElevation - getDEMValue(r + 1, c, i + ncols),
					(currentElevation - getDEMValue(r + 1, c - 1, i + ncols - 1))
							/ SQRT2,
					currentElevation - getDEMValue(r, c - 1, i - 1),
					(currentElevation - getDEMValue(r - 1, c - 1, i - ncols - 1))
							/ SQRT2,
					currentElevation - getDEMValue(r - 1, c, i - ncols),
					(currentElevation - getDEMValue(r - 1, c + 1, i - ncols + 1))
							/ SQRT2 };

			final int idx = getIdxForMaxValue(tmpSlopes);
			if (-1 == idx) {
				// possible outlet (exutoire)
				// sink (depression)
				return Float.NEGATIVE_INFINITY;
			} else {
				return getSlopeInPercent(tmpSlopes[idx]);
			}
		}
	}

	public float localTransition(float[] rac, int r, int c, int i) {
		/* remain unchanged : one step of computation (init) */
		return rac[i];
	}

	private static float getSlopeInPercent(final float ratio) {
		return (float) (400 * Math.atan(ratio) / Math.PI);
	}

	private static int getIdxForMaxValue(final float[] values) {
		float max = 0;
		int result = -1;
		for (int i = 0; i < values.length; i++) {
			if ((!Float.isNaN(values[i])) && (values[i] >= max)) {
				result = i;
				max = values[i];
			}
		}
		return result;
	}

	private float getDEMValue(final int r, final int c, final int i) {
		return ((0 > r) || (nrows <= r) || (0 > c) || (ncols <= c)) ? Float.NaN
				: DEM[i];
	}
}