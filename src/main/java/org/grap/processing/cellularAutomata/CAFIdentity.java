package org.grap.processing.cellularAutomata;

import org.grap.processing.cellularAutomata.cam.ICAFloat;

public class CAFIdentity implements ICAFloat {
	private int nrows;
	private int ncols;
	private float[] pixels;

	public CAFIdentity(float[] pixels, final int nrows, final int ncols) {
		this.nrows = nrows;
		this.ncols = ncols;
		this.pixels = pixels;
	}

	public float init(int r, int c, int i) {
		return pixels[i];
	}

	public float localTransition(float[] rac, int r, int c, int i) {
		return pixels[i];
	}

	public int getNCols() {
		return ncols;
	}

	public int getNRows() {
		return nrows;
	}
}