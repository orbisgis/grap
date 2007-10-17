package org.grap.processing.cellularAutomata.seqImpl;

import org.grap.processing.cellularAutomata.cam.ACAN;
import org.grap.processing.cellularAutomata.cam.ICA;
import org.grap.processing.cellularAutomata.cam.ICAFloat;
import org.grap.processing.cellularAutomata.cam.ICAShort;

public class SCAN extends ACAN {
	private int iterationsCount;

	/* constructor */
	public SCAN(final ICA ca) {
		super(ca);
	}

	/* getters */
	public int getIterationsCount() {
		return iterationsCount;
	}

	/* public methods */
	public int getStableState() {
		final long startTime = System.currentTimeMillis();
		long startT = System.currentTimeMillis();
		ISCAN scan = null;

		// initialize
		if (getCa() instanceof ICAShort) {
			scan = new SCANShort(this);
		} else if (getCa() instanceof ICAFloat) {
			scan = new SCANFloat(this);
		}
		System.err.printf("end of initialization\n");

		// get stable state
		iterationsCount = 0;
		boolean goOn;

		do {
			goOn = scan.globalTransition(iterationsCount);
			System.err.printf("Seq. Step %d : %d ms\n", iterationsCount, System
					.currentTimeMillis()
					- startT);
			iterationsCount++;
			startT = System.currentTimeMillis();
		} while (goOn);

		System.err.printf("Total duration : %d ms\n", System
				.currentTimeMillis()
				- startTime);
		return iterationsCount;
	}
}