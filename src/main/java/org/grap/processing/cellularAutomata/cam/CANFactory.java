package org.grap.processing.cellularAutomata.cam;

import org.grap.processing.cellularAutomata.parallelImpl.PCAN;
import org.grap.processing.cellularAutomata.seqImpl.SCAN;

public class CANFactory {
	private final static int NUMBER_OF_THREADS = Runtime.getRuntime()
			.availableProcessors();

	public static ICAN createCAN(final ICA ca) {
		if (1 == NUMBER_OF_THREADS) {
			System.out.println("CANFactory : sequential implementation");
			return new SCAN(ca);
		} else {
			System.out.println("CANFactory : parallel implementation");
			return new PCAN(ca);
		}
	}
}