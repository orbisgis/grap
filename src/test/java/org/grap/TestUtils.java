package org.grap;

public class TestUtils {
	/**
	 * TODO this method is duplicated in gdms and should be removed when the two
	 * projects become one
	 */
	public static void printFreeMemory() {
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		// System.out.println("free memory: " + freeMemory / 1024);
		// System.out.println("allocated memory: " + allocatedMemory / 1024);
		// System.out.println("max memory: " + maxMemory / 1024);
		System.out.println("total free memory: "
				+ (freeMemory + (maxMemory - allocatedMemory)) / 1024);
	}
}
