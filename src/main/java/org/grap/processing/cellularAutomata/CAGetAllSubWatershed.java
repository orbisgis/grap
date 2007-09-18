package org.grap.processing.cellularAutomata;

public class CAGetAllSubWatershed implements IFloatCA {
	private int nrows;

	private int ncols;

	private short[] slopesDirections;

	public CAGetAllSubWatershed(final short[] slopesDirections,
			final int nrows, final int ncols) {
		this.nrows = nrows;
		this.ncols = ncols;
		this.slopesDirections = slopesDirections;
	}

	public int getNCols() {
		return ncols;
	}

	public int getNRows() {
		return nrows;
	}

	public float init(final int r, final int c, final int i) {
		return i;
	}

	public float localTransition(final float[] rac, final int r, final int c,
			final int i) {
		switch (getSlopeDirection(r, c, i)) {
		case 1:
			return getRacValue(rac[i], rac, r, c + 1, i + 1);
		case 2:
			return getRacValue(rac[i], rac, r + 1, c + 1, i + ncols + 1);
		case 4:
			return getRacValue(rac[i], rac, r + 1, c, i + ncols);
		case 8:
			return getRacValue(rac[i], rac, r + 1, c - 1, i + ncols - 1);
		case 16:
			return getRacValue(rac[i], rac, r, c - 1, i - 1);
		case 32:
			return getRacValue(rac[i], rac, r - 1, c - 1, i - ncols - 1);
		case 64:
			return getRacValue(rac[i], rac, r - 1, c, i - ncols);
		case 128:
			return getRacValue(rac[i], rac, r - 1, c + 1, i - ncols + 1);
		}
		return -1;
	}

	private int getSlopeDirection(final int r, final int c, final int i) {
		return ((0 > r) || (nrows <= r) || (0 > c) || (ncols <= c)) ? -1
				: slopesDirections[i];
	}

	private float getRacValue(final float currentValue, final float[] rac,
			final int r, final int c, final int i) {
		if ((0 > r) || (nrows <= r) || (0 > c) || (ncols <= c)) {
			return Float.NaN;
		} else if (Float.isNaN(rac[i]) || (-1 == rac[i])) {
			return currentValue;
		} else {
			return rac[i];
		}
	}
}