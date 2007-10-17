package org.grap.processing.cellularAutomata.seqImpl;

import org.grap.processing.cellularAutomata.cam.ACAN;
import org.grap.processing.cellularAutomata.cam.ICAFloat;

public class SCANFloat implements ISCAN {
	private float[] rac1;
	private float[] rac0;
	private ICAFloat ca;
	private int ncols;
	private int nrows;

	public SCANFloat(final ACAN can) {
		rac0 = (float[]) can.getRac0();
		rac1 = (float[]) can.getRac1();
		ca = (ICAFloat) can.getCa();
		ncols = ca.getNCols();
		nrows = ca.getNRows();

		int i = 0;
		for (int r = 0; r < nrows; r++) {
			for (int c = 0; c < ncols; c++) {
				rac0[i] = ca.init(r, c, i);
				i++;
			}
		}
	}

	public boolean globalTransition(final int iterationsCount) {
		boolean modified = false;
		int i = 0;

		if (0 == iterationsCount % 2) {
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
}