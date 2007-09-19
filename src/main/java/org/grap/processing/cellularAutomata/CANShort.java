package org.grap.processing.cellularAutomata;

class CANShort implements Runnable {
	private int ncols;

	private CAN can;

	private short[] rac0;

	private short[] rac1;

	private IShortCA ca;

	private int startIdx;

	private int endIdx;

	private int currentThreadIdx;

	public CANShort(final CAN can, final int startIdx, final int endIdx,
			final int currentThreadIdx) {
		this.can = can;
		rac0 = (short[]) can.getRac0();
		rac1 = (short[]) can.getRac1();
		ca = (IShortCA) can.getCa();
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
		System.err.printf("[%d] end of initialization\n", currentThreadIdx);

		// get stable state
		do {
			boolean modified = false;
			if (0 == (can.getIterationsCount() % 2)) {
				for (int i = startIdx; i < endIdx; i++) {
					final int r = i / ncols;
					final int c = i % ncols;
					rac1[i] = ca.localTransition(rac0, r, c, i);
					if (rac0[i] != rac1[i]) {
						modified = true;
					}
				}
			} else {
				for (int i = startIdx; i < endIdx; i++) {
					final int r = i / ncols;
					final int c = i % ncols;
					rac0[i] = ca.localTransition(rac1, r, c, i);
					if (rac0[i] != rac1[i]) {
						modified = true;
					}
				}
			}
			can.getBreakCondition().setModificationValue(modified,
					currentThreadIdx);
			can.synchronization();
		} while (can.getBreakCondition().doIContinue());
		
		// the end...
		System.err.printf("[%d] end\n", currentThreadIdx);
	}
}