package org.grap.processing.cellularAutomata.parallelImpl;

public class BreakCondition implements Runnable {
	private boolean[] collectOfModificationsValues;

	private boolean globalStop;

	public BreakCondition(final int numberOfThreads) {
		collectOfModificationsValues = new boolean[numberOfThreads];
	}

	public void setModificationValue(final boolean modified,
			final int currentThreadIdx) {
		collectOfModificationsValues[currentThreadIdx] = modified;
	}

	public boolean doIContinue() {
		return globalStop;
	}

	public synchronized void run() {
		globalStop = false;
		for (boolean modified : collectOfModificationsValues) {
			if (modified) {
				globalStop = true;
				break;
			}
		}
	}
}