package org.grap.processing.cellularAutomata.parallelImpl;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.grap.processing.cellularAutomata.cam.ICA;
import org.grap.processing.cellularAutomata.cam.IFloatCA;
import org.grap.processing.cellularAutomata.cam.IShortCA;

public class CAN {
	private final static int NUMBER_OF_THREADS = Runtime.getRuntime()
			.availableProcessors();

	private CyclicBarrier barrier;

	private ICA ca;

	private Object rac0;

	private Object rac1;

	private int iterationsCount;

	private BreakCondition breakCondition;

	private int nbCells;

	/* constructor */
	public CAN(final ICA ca) {
		this.ca = ca;
		breakCondition = new BreakCondition(NUMBER_OF_THREADS);
		// This barrier action is useful for updating shared-state before
		// any of the parties continue.
		barrier = new CyclicBarrier(NUMBER_OF_THREADS + 1, breakCondition);
		nbCells = ca.getNRows() * ca.getNCols();

		if (ca instanceof IShortCA) {
			rac0 = new short[nbCells];
			rac1 = new short[nbCells];
		} else if (ca instanceof IFloatCA) {
			rac0 = new float[nbCells];
			rac1 = new float[nbCells];
		}
	}

	/* getters */
	public ICA getCa() {
		return ca;
	}

	public Object getRac0() {
		return rac0;
	}

	public Object getRac1() {
		return rac1;
	}

	public Object getCANValues() {
		return rac0;
	}

	public int getIterationsCount() {
		return iterationsCount;
	}

	public BreakCondition getBreakCondition() {
		return breakCondition;
	}

	/* public methods */
	public int getStableState() {
		final long startTime = System.currentTimeMillis();
		final int subDomainSize = nbCells / NUMBER_OF_THREADS;

		// initialize
		if (ca instanceof IShortCA) {
			for (int i = 0; i < NUMBER_OF_THREADS; i++) {
				final int startIdx = i * subDomainSize;
				final int endIdx = (NUMBER_OF_THREADS == i + 1) ? nbCells
						: startIdx + subDomainSize;
				new Thread(new CANShort(this, startIdx, endIdx, i)).start();
			}
		} else if (ca instanceof IFloatCA) {
			for (int i = 0; i < NUMBER_OF_THREADS; i++) {
				final int startIdx = i * subDomainSize;
				final int endIdx = (NUMBER_OF_THREADS == i + 1) ? nbCells
						: startIdx + subDomainSize;
				new Thread(new CANFloat(this, startIdx, endIdx, i)).start();
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
			System.err.printf("Step %d : %d ms\n", iterationsCount, System
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

	public void print(final String title) {
		System.out.println(title);
		if (ca instanceof IShortCA) {
			short[] _rac0 = (short[]) rac0;
			for (int r = 0; r < ca.getNRows(); r++) {
				for (int c = 0; c < ca.getNCols(); c++) {
					System.out.printf("%3d\t", _rac0[r * ca.getNCols() + c]);
				}
				System.out.println();
			}
		} else if (ca instanceof IFloatCA) {
			float[] _rac0 = (float[]) rac0;
			for (int r = 0; r < ca.getNRows(); r++) {
				for (int c = 0; c < ca.getNCols(); c++) {
					System.out.printf("%.1f\t", _rac0[r * ca.getNCols() + c]);
				}
				System.out.println();
			}
		}
	}
}