package org.grap.processing.cellularAutomata;

public class CASlopesAccumulation implements IIntCA {
	private int nrows;

	private int ncols;

	private int[] slopesDirections;

	private int[] neighboursIndices;

	private int[] neighboursDirection;

	public CASlopesAccumulation(final int[] slopesDirections, final int nrows,
			final int ncols) {
		this.nrows = nrows;
		this.ncols = ncols;
		this.slopesDirections = slopesDirections;

		neighboursIndices = new int[] { 1, ncols + 1, ncols, ncols - 1, -1,
				-ncols - 1, -ncols, -ncols + 1 };
		neighboursDirection = new int[] { 16, 32, 64, 128, 1, 2, 4, 8 };
	}

	public int getNCols() {
		return ncols;
	}

	public int getNRows() {
		return nrows;
	}

	public int init(int r, int c, int i) {
		return 1;
	}

	public int localTransition(int[] rac, int r, int c, int i) {
		int result = 1;
		for (int k = 0; k < neighboursIndices.length; k++) {
			final int ii = i + neighboursIndices[k];
			final int rr = ii / ncols;
			final int cc = ii % ncols;

			result += (getValue(slopesDirections, rr, cc, ii) == neighboursDirection[k]) ? getValue(
					rac, rr, cc, ii)
					: 0;
		}
		return result;
	}

	private float getValue(final int[] v, final int r, final int c, final int i) {
		return ((0 > r) || (nrows <= r) || (0 > c) || (ncols <= c)) ? Float.NaN
				: v[i];
	}
}