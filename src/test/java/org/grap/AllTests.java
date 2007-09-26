package org.grap;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.grap.processing.operation.CropTest;

public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.grap.processing.operation");
		// $JUnit-BEGIN$
		suite.addTestSuite(CropTest.class);
		// $JUnit-END$
		return suite;
	}
}