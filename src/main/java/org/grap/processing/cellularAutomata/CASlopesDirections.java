package org.grap.processing.cellularAutomata;

import org.grap.processing.cellularAutomata.cam.ICAShort;

public class CASlopesDirections implements ICAShort {
	private int nrows;

	private int ncols;

	private float[] DEM;

	private final static float SQRT2 = (float) Math.sqrt(2d);

	public CASlopesDirections(final float[] DEM, final int nrows,
			final int ncols) {
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

	public short init(int r, int c, int i) {
		final float currentElevation = DEM[i];

		if (Float.isNaN(currentElevation)) {
			// noDataValue
			return Short.MIN_VALUE;
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
				return -1;
			} else {
				return (short) (1 << idx);
			}
		}
	}

	public short localTransition(short[] rac, int r, int c, int i) {
		/* remain unchanged : one step of computation (init) */
		return rac[i];
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

	private float getDEMValue(final int r, final int c, final int i) {
		return ((0 > r) || (nrows <= r) || (0 > c) || (ncols <= c)) ? Float.NaN
				: DEM[i];
	}
}