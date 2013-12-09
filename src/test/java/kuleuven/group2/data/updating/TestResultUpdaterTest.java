package kuleuven.group2.data.updating;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import kuleuven.group2.data.TestDatabase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

public class TestResultUpdaterTest {
	
	private TestDatabase database;
	private TestResultUpdater updater;
	private static Description suiteDescription;
	private static Description method1Description;
	private static Description method2Description;
	private static Failure method2Failure;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		suiteDescription = Description.createSuiteDescription(TestClass.class);
		method1Description = Description.createTestDescription(TestClass.class, "testMethod1S");
		method2Description = Description.createTestDescription(TestClass.class, "testMethod2F");
		method2Failure = new Failure(method2Description, null);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		database = new TestDatabase();
		updater = new TestResultUpdater(database);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSucceedTests() throws Exception {
		updater.testRunStarted(suiteDescription);
		
		updater.testStarted(method1Description);
		updater.testFinished(method1Description);
		
		// one successful run was added
		assertEquals(1, database.getAllTestRuns().size());
		
		updater.testStarted(method2Description);
		updater.testAssumptionFailure(method2Failure);
		
		updater.testRunFinished(null);
		
		// every test should have one run
		for(kuleuven.group2.data.Test test : database.getAllTests()) {
			assertEquals(1, test.getTestRuns().size());
		}
	}
	
	private class TestClass {
		
		private TestedClass subject;
		
		@Before
		public void setUp() {
			subject = new TestedClass();
		}
		
		@Test
		public void testMethod1S() {
			assertTrue(subject.getTrue());
		}
		
		@Test
		public void testMethod2F() {
			assertTrue(subject.getFalse());
		}
	}
	
	private class TestedClass {
		
		public TestedClass() {
			// do nothing
		}

		public boolean getFalse() {
			return false;
		}

		public boolean getTrue() {
			return true;
		}
		
	}

}
