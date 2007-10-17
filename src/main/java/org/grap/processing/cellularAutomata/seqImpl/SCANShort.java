package org.grap.processing.cellularAutomata.seqImpl;

import org.grap.processing.cellularAutomata.cam.ACAN;
import org.grap.processing.cellularAutomata.cam.ICAShort;

public class SCANShort implements ISCAN {
	private short[] rac1;
	private short[] rac0;
	private ICAShort ca;
	private int ncols;
	private int nrows;

	public SCANShort(final ACAN can) {
		rac0 = (short[]) can.getRac0();
		rac1 = (short[]) can.getRac1();
		ca = (ICAShort) can.getCa();
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
}