package org.grap.processing.cellularAutomata.parallelImpl;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.grap.processing.cellularAutomata.cam.ACAN;
import org.grap.processing.cellularAutomata.cam.ICA;
import org.grap.processing.cellularAutomata.cam.ICAFloat;
import org.grap.processing.cellularAutomata.cam.ICAShort;

public class PCAN extends ACAN {
	private final static int NUMBER_OF_THREADS = Runtime.getRuntime()
			.availableProcessors();

	private CyclicBarrier barrier;

	private int iterationsCount;

	private BreakCondition breakCondition;

	/* constructor */
	public PCAN(final ICA ca) {
		super(ca);

		breakCondition = new BreakCondition(NUMBER_OF_THREADS);
		// This barrier action is useful for updating shared-state before
		// any of the parties continue.
		barrier = new CyclicBarrier(NUMBER_OF_THREADS + 1, breakCondition);
	}

	/* getters */
	public int getIterationsCount() {
		return iterationsCount;
	}

	public BreakCondition getBreakCondition() {
		return breakCondition;
	}

	/* public methods */
	public int getStableState() {
		final long startTime = System.currentTimeMillis();
		final int subDomainSize = getNbCells() / NUMBER_OF_THREADS;

		// initialize
		if (getCa() instanceof ICAShort) {
			for (int i = 0; i < NUMBER_OF_THREADS; i++) {
				final int startIdx = i * subDomainSize;
				final int endIdx = (NUMBER_OF_THREADS == i + 1) ? getNbCells()
						: startIdx + subDomainSize;
				new Thread(new PCANShort(this, startIdx, endIdx, i)).start();
			}
		} else if (getCa() instanceof ICAFloat) {
			for (int i = 0; i < NUMBER_OF_THREADS; i++) {
				final int startIdx = i * subDomainSize;
				final int endIdx = (NUMBER_OF_THREADS == i + 1) ? getNbCells()
						: startIdx + subDomainSize;
				new Thread(new PCANFloat(this, startIdx, endIdx, i)).start();
			}
		}
		// initialize
		synchronization();
		System.err.printf("end of initialization\n");

		// get stable state
		iterationsCount = 0;
		do {
			final long startT = System.currentTimeMillis();
			synchronization();
			System.err.printf("Par. Step %d : %d ms\n", iterationsCount, System
					.currentTimeMillis()
					- startT);
			iterationsCount++;
		} while (breakCondition.doIContinue());

		System.err.printf("Total duration : %d ms\n", System
				.currentTimeMillis()
				- startTime);
		return iterationsCount;
	}

	public void synchronization() {
		try {
			barrier.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
	}
}