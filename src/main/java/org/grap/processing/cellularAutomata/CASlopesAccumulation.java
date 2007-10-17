package org.grap.processing.cellularAutomata;

import org.grap.processing.cellularAutomata.cam.ICAFloat;

public class CASlopesAccumulation implements ICAFloat {
	private int nrows;

	private int ncols;

	private short[] slopesDirections;

	private int[] neighboursIndices;

	private short[] neighboursDirection;

	public CASlopesAccumulation(final short[] slopesDirections,
			final int nrows, final int ncols) {
		this.nrows = nrows;
		this.ncols = ncols;
		this.slopesDirections = slopesDirections;

		neighboursIndices = new int[] { 1, ncols + 1, ncols, ncols - 1, -1,
				-ncols - 1, -ncols, -ncols + 1 };
		neighboursDirection = new short[] { 16, 32, 64, 128, 1, 2, 4, 8 };
	}

	public int getNCols() {
		return ncols;
	}

	public int getNRows() {
		return nrows;
	}

	public float init(int r, int c, int i) {
		return 1;
	}

	public float localTransition(float[] rac, int r, int c, int i) {
		int result = 1;
		for (int k = 0; k < neighboursIndices.length; k++) {
			final int ii = i + neighboursIndices[k];
			final int rr = ii / ncols;
			final int cc = ii % ncols;

			result += (getSlopesDirectionsValue(slopesDirections, rr, cc, ii) == neighboursDirection[k]) ? getRacValue(
					rac, rr, cc, ii)
					: 0;
		}
		return result;
	}

	private short getSlopesDirectionsValue(short[] v, final int r, final int c,
			final int i) {
		return ((0 > r) || (nrows <= r) || (0 > c) || (ncols <= c)) ? -1 : v[i];
	}

	private float getRacValue(final float[] rac, final int r, final int c,
			final int i) {
		return ((0 > r) || (nrows <= r) || (0 > c) || (ncols <= c)) ? Float.NaN
				: rac[i];
	}
}