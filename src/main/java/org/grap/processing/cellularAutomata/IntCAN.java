package org.grap.processing.cellularAutomata;

public class IntCAN extends ACAN {
	private int nrows;

	private int ncols;

	private int[] rac0;

	private int[] rac1;

	private IIntCA ca;

	public IntCAN(final IIntCA ca) {
		this.nrows = ca.getNRows();
		this.ncols = ca.getNCols();
		rac0 = new int[nrows * ncols];
		rac1 = new int[nrows * ncols];
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
					if (rac0[i] != rac1[i]) {
						modified = true;
					}
					i++;
				}
			}
		} else {
			for (int r = 0; r < nrows; r++) {
				for (int c = 0; c < ncols; c++) {
					rac0[i] = ca.localTransition(rac1, r, c, i);
					if (rac0[i] != rac1[i]) {
						modified = true;
					}
					i++;
				}
			}
		}
		return modified;
	}

	@Override
	public void print() {
		for (int r = 0; r < nrows; r++) {
			for (int c = 0; c < ncols; c++) {
				System.out.printf("%3d\t", rac0[r * ncols + c]);
			}
			System.out.println();
		}
	}

	@Override
	public Object getValuesSnapshot() {
		return rac0;
	}
}