package com.ee.tayra.exploratory

import org.junit.runner.JUnitCore

class ExploratoryTestRunner {

	public static void main(String[] args) throws Exception {
		JUnitCore.main("com.ee.tayra.exploratory.BackupExploratoryTest",
"com.ee.tayra.exploratory.RestoreExploratoryTest",
"com.ee.tayra.exploratory.RestoreExploratoryTest");
 }
}
