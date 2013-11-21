package kuleuven.group2.data.updating;

import kuleuven.group2.data.Test;
import kuleuven.group2.data.TestDatabase;
import kuleuven.group2.data.TestedMethod;
import kuleuven.group2.data.signature.JavaSignature;
import kuleuven.group2.data.signature.JavaSignatureParser;
import kuleuven.group2.testrunner.TestRunner;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import be.kuleuven.cs.ossrewriter.Monitor;

/**
 * This class updates the link between methods and tests when tests are run.
 * 
 * @author Vital D'haveloose, Ruben Pieters
 */
public class MethodTestLinkUpdater extends Monitor {

	protected final TestDatabase testDatabase;
	protected CurrentRunningTestHolder currentTestHolder;
	protected final OssRewriterLoader ossRewriterLoader;

	public MethodTestLinkUpdater(TestDatabase testDatabase, OssRewriterLoader ossRewriterLoader) {
		this.testDatabase = testDatabase;
		this.ossRewriterLoader = ossRewriterLoader;
	}

	public void registerTestHolder(CurrentRunningTestHolder currentTestHolder) {
		this.currentTestHolder = currentTestHolder;
	}

	public void registerTestHolder(TestRunner testRunner) {
		this.currentTestHolder = new MethodTestLinkRunListener(testRunner);
	}

	@Override
	public void enterMethod(String methodName) {
		JavaSignature signature = new JavaSignatureParser(methodName).parseSignature();
		TestedMethod enteredMethod = testDatabase.getMethod(signature);
		if (enteredMethod != null) {
			Test currentTest = currentTestHolder.getCurrentRunningTest();
			testDatabase.addMethodTestLink(enteredMethod, currentTest);
		}
	}

	private class MethodTestLinkRunListener extends RunListener implements CurrentRunningTestHolder {

		private Test currentTest = null;

		protected MethodTestLinkRunListener(TestRunner testRunner) {
			testRunner.addRunListener(this);
		}

		/**
		 * Called before any tests have been run.
		 * 
		 * @param description
		 *            describes the tests to be run
		 */
		@Override
		public void testRunStarted(Description description) throws Exception {
			testDatabase.clearMethodTestLinks();

			ossRewriterLoader.registerMonitor(MethodTestLinkUpdater.this);
		}

		/**
		 * Called when an atomic test is about to be started.
		 * 
		 * @param description
		 *            the description of the test that is about to be run
		 *            (generally a class and method name)
		 */
		@Override
		public void testStarted(Description description) throws Exception {
			String testClassName = description.getClassName();
			String testMethodName = description.getMethodName();
			currentTest = new Test(testClassName, testMethodName);
		}

		@Override
		public void testRunFinished(Result result) throws Exception {
			ossRewriterLoader.unregisterMonitor(MethodTestLinkUpdater.this);
		}

		/**
		 * Called when an atomic test has finished, whether the test succeeds or
		 * fails.
		 * 
		 * @param description
		 *            the description of the test that just ran
		 */
		@Override
		public void testFinished(Description description) throws Exception {
			currentTest = null;
		}

		@Override
		public Test getCurrentRunningTest() {
			return currentTest;
		}

	}

}
