package org.grap.processing.cellularAutomata;

public class FloatCAN extends ACAN {
	private int nrows;

	private int ncols;

	private float[] rac0;

	private float[] rac1;

	private IFloatCA ca;

	public FloatCAN(final IFloatCA ca) {
		this.nrows = ca.getNRows();
		this.ncols = ca.getNCols();
		rac0 = new float[nrows * ncols];
		rac1 = new float[nrows * ncols];
		this.ca = ca;

		int i = 0;
		for (int r = 0; r < nrows; r++) {
			for (int c = 0; c < ncols; c++) {
				rac0[i] = ca.init(r, c, i);
				i++;
			}
		}
	}

	public boolean globalTransition(final int step) {
		boolean modified = false;
		int i = 0;

		if (0 == step % 2) {
			for (int r = 0; r < nrows; r++) {
				for (int c = 0; c < ncols; c++) {
					rac1[i] = ca.localTransition(rac0, r, c, i);
					if (!equal(rac0[i], rac1[i])) {
						modified = true;
					}
					i++;
				}
			}
		} else {
			for (int r = 0; r < nrows; r++) {
				for (int c = 0; c < ncols; c++) {
					rac0[i] = ca.localTransition(rac1, r, c, i);
					if (!equal(rac0[i], rac1[i])) {
						modified = true;
					}
					i++;
				}
			}
		}
		return modified;
	}

	private boolean equal(final float a, final float b) {
		return ((Float.isNaN(a) && Float.isNaN(b)) || (a == b)) ? true : false;
	}

	@Override
	public void print() {
		for (int r = 0; r < nrows; r++) {
			for (int c = 0; c < ncols; c++) {
				System.out.printf("%.1f\t", rac0[r * ncols + c]);
			}
			System.out.println();
		}
	}

	@Override
	public Object getValuesSnapshot() {
		return rac0;
	}
}