package kuleuven.group2.runner;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

public class TestRunnerTest {
	
	protected TestRunner testRunner;
	protected kuleuven.group2.data.Test testMethod2Arg;
	protected kuleuven.group2.data.Test testMethodFail;
	protected JUnitCore junitCore;
	
	protected boolean listenerVisited;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		junitCore = new JUnitCore();
		
		testRunner = new TestRunner();
		testRunner.setClassLoader(getClass().getClassLoader());
		
		testMethod2Arg = new kuleuven.group2.data.Test(
				kuleuven.group2.data.signature.JavaSignatureParserTest.class.getName(),
				"testMethod2Arg"
				);
		
		testMethodFail = new kuleuven.group2.data.Test(
				kuleuven.group2.runner.TestRunnerTest.class.getName(),
				"fail"
				);
		
		listenerVisited = false;
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testSucceededSimple() {
		Result[] result = testRunner.runTestMethods(testMethod2Arg);
		Result testMethod2ArgResult = result[0];
		
		assertTrue(testMethod2ArgResult.wasSuccessful());
		assertEquals(1, testMethod2ArgResult.getRunCount());
	}
	
	/*@Test
	public void testFailedSimple() {
		Result[] result = testRunner.runTestMethods(testMethodFail);
		Result testMethodFailResult = result[0];
		
		assertFalse(testMethodFailResult.wasSuccessful());
		assertEquals(1, testMethodFailResult.getRunCount());
		assertEquals(1, testMethodFailResult.getFailureCount());
	}

	// used as failing test
	@Test
	public void fail() {
		assertTrue(false);
	}*/
	
	@Test
	public void testRunListener() {
		junitCore.addListener(new RunListener() {
			@Override
			public void testStarted(Description description) throws Exception {
				TestRunnerTest.this.listenerVisited();
				System.out.println("teststarted");
		    }
			@Override
		    public void testRunFinished(Result result) throws Exception {
				System.out.println("testrunfinished");
		    }
		});

		Result[] result = testRunner.runTestMethods(testMethod2Arg);
		
		assertTrue(listenerVisited);
	}
	
	public void listenerVisited() {
		listenerVisited = true;
	}

}
