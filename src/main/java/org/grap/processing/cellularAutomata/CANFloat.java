package org.grap.processing.cellularAutomata;

import java.util.concurrent.CyclicBarrier;

class CANFloat implements Runnable {
	private int ncols;

	private CAN can;

	private float[] rac0;

	private float[] rac1;

	private IFloatCA ca;

	private int startIdx;

	private int endIdx;

	private int currentThreadIdx;

	public CANFloat(final CAN can, final int startIdx, final int endIdx,
			final int currentThreadIdx) {
		this.can = can;
		rac0 = (float[]) can.getRac0();
		rac1 = (float[]) can.getRac1();
		ca = (IFloatCA) can.getCa();
		ncols = ca.getNCols();
		this.startIdx = startIdx;
		this.endIdx = endIdx;
		this.currentThreadIdx = currentThreadIdx;
	}

	public void run() {
		// initialize
		for (int i = startIdx; i < endIdx; i++) {
			final int r = i / ncols;
			final int c = i % ncols;
			rac0[i] = ca.init(r, c, i);
		}
		can.synchronization();

		// get stable state
		do {
			boolean modified = false;
			if (0 == (can.getIterationsCount() % 2)) {
				for (int i = startIdx; i < endIdx; i++) {
					final int r = i / ncols;
					final int c = i % ncols;
					rac1[i] = ca.localTransition(rac0, r, c, i);
					if (!equal(rac0[i], rac1[i])) {
						modified = true;
					}
				}
			} else {
				for (int i = startIdx; i < endIdx; i++) {
					final int r = i / ncols;
					final int c = i % ncols;
					rac0[i] = ca.localTransition(rac1, r, c, i);
					if (!equal(rac0[i], rac1[i])) {
						modified = true;
					}
				}
			}
			can.getBreakCondition().setModificationValue(modified,
					currentThreadIdx);
			can.synchronization();
		} while (can.getBreakCondition().doIContinue());
	}

	private boolean equal(final float a, final float b) {
		return ((Float.isNaN(a) && Float.isNaN(b)) || (a == b)) ? true : false;
	}
}