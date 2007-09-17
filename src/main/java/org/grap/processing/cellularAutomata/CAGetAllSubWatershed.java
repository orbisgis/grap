package org.grap.processing.cellularAutomata;

public class CAGetAllSubWatershed implements IIntCA {
	private int nrows;

	private int ncols;

	private int[] slopesDirections;

	public CAGetAllSubWatershed(final int[] slopesDirections,
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

	public int init(final int r, final int c, final int i) {
		return i;
	}

	public int localTransition(final int[] rac, final int r, final int c,
			final int i) {
		switch (getSlopeDirection(r, c, i)) {
		case 1:
			getRacValue(rac, r, c + 1, i + 1);
			break;
		case 2:
			getRacValue(rac, r + 1, c + 1, i + ncols + 1);
			break;
		case 4:
			getRacValue(rac, r + 1, c, i + ncols);
			break;
		case 8:
			getRacValue(rac, r + 1, c - 1, i + ncols - 1);
			break;
		case 16:
			getRacValue(rac, r, c - 1, i - 1);
			break;
		case 32:
			getRacValue(rac, r - 1, c - 1, i - ncols - 1);
			break;
		case 64:
			getRacValue(rac, r - 1, c, i - ncols);
			break;
		case 128:
			getRacValue(rac, r - 1, c + 1, i - ncols + 1);
			break;
		}
		return -1;
	}

	private int getSlopeDirection(final int r, final int c, final int i) {
		return ((0 > r) || (nrows <= r) || (0 > c) || (ncols <= c)) ? -1
				: slopesDirections[i];
	}

	private int getRacValue(final int[] rac, final int r, final int c,
			final int i) {
		return ((0 > r) || (nrows <= r) || (0 > c) || (ncols <= c)) ? -1
				: rac[i];
	}
}