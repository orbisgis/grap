package org.grap;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.grap.io.BasicTest;
import org.grap.io.GeoreferencingTest;
import org.grap.processing.operation.AllWatershedsTest;
import org.grap.processing.operation.CANImplementationsTest;
import org.grap.processing.operation.CropTest;
import org.grap.processing.operation.IdentityTest;
import org.grap.processing.operation.SlopesAccumulationsTest;

public class GrapTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for grap");
		// $JUnit-BEGIN$
		suite.addTestSuite(BasicTest.class);
		suite.addTestSuite(GeoreferencingTest.class);
		suite.addTestSuite(AllWatershedsTest.class);
		suite.addTestSuite(CANImplementationsTest.class);
		suite.addTestSuite(IdentityTest.class);
		suite.addTestSuite(CropTest.class);
		suite.addTestSuite(SlopesAccumulationsTest.class);
		// $JUnit-END$
		return suite;
	}
}