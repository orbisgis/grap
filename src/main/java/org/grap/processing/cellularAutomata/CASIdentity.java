package org.grap.processing.cellularAutomata;

import org.grap.processing.cellularAutomata.cam.ICAShort;

public class CASIdentity implements ICAShort {
	private int nrows;
	private int ncols;
	private short[] pixels;

	public CASIdentity(final short[] pixels, final int nrows, final int ncols) {
		this.nrows = nrows;
		this.ncols = ncols;
		this.pixels = pixels;
	}

	public short init(int r, int c, int i) {
		return pixels[i];
	}

	public short localTransition(short[] rac, int r, int c, int i) {
		return pixels[i];
	}

	public int getNCols() {
		return ncols;
	}

	public int getNRows() {
		return nrows;
	}
}