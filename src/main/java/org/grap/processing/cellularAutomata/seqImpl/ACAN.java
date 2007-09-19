package org.grap.processing.cellularAutomata.seq;

abstract public class ACAN {
	public abstract boolean globalTransition(final int step);

	abstract public Object getCANValues();

	public int getStableState() {
		final long globalStart = System.currentTimeMillis();
		long localStart = globalStart;
		int step = 0;

		while (globalTransition(step)) {
			print(step, localStart);
			step++;
			localStart = System.currentTimeMillis();
		}
		System.out.printf("Total in %d ms\n", System.currentTimeMillis()
				- globalStart);
		return step;
	}

	public abstract void print();

	public void print(final int step, final long localStart) {
		System.out.printf("Step %d in %d ms\n", step, System
				.currentTimeMillis()
				- localStart);
	}
}