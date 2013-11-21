package kuleuven.group2.data.updating;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import kuleuven.group2.data.TestDatabase;
import kuleuven.group2.data.TestedMethod;
import kuleuven.group2.data.updating.ICurrentRunningTestHolder;
import kuleuven.group2.data.updating.MethodTestLinkUpdater;
import kuleuven.group2.data.updating.OssRewriterLoader;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MethodTestLinkUpdaterTest {
	
	private TestDatabase database;
	private MethodTestLinkUpdater updater;
	CurrentTestHolder currentTestHolder;
	
	private static String getFalsePath = "kuleuven/group2/data/methodlink/MethodLinkerTest$TestedClass.getFalse()Z";
	@SuppressWarnings("unused")
	private static TestedMethod getFalse = new TestedMethod(getFalsePath);
	private static String getTruePath = "kuleuven/group2/data/methodlink/MethodLinkerTest$TestedClass.getTrue()Z";
	private static TestedMethod getTrue = new TestedMethod(getTruePath);
	
	private static kuleuven.group2.data.Test testMethod1S = new kuleuven.group2.data.Test("TestClass", "testMethod1S");
	private static kuleuven.group2.data.Test testMethod2F = new kuleuven.group2.data.Test("TestClass", "testMethod2F");
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		database = new TestDatabase();
		database.addMethod(getTrue);
		OssRewriterLoader loader = new OssRewriterLoader();
		currentTestHolder = new CurrentTestHolder();
		updater = new MethodTestLinkUpdater(database, loader);
		updater.registerTestHolder(currentTestHolder);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLinks() throws Exception {

		assertEquals(0, database.getNbLinks());
		
		currentTestHolder.setCurrentTest(testMethod2F);
		updater.enterMethod(getFalsePath);
		
		// getFalse was not added to the database, and will not be linked
		assertEquals(0, database.getNbLinks());
		
		currentTestHolder.setCurrentTest(testMethod1S);
		updater.enterMethod(getTruePath);
		
		// getTrue was added to the database, and now has a link with testMethod1S
		assertEquals(1, database.getNbLinks());

		assertTrue(database.containsMethodTestLink(getTrue, testMethod1S));
	}
	
	@Test
	public void testWithOssRewriter() {
		//TODO: make test with ossRewriter
	}
	
	@SuppressWarnings("unused")
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
	
	private class CurrentTestHolder implements ICurrentRunningTestHolder {
		
		private kuleuven.group2.data.Test currentTest;
		
		public CurrentTestHolder() {
			// do nothing
		}

		@Override
		public kuleuven.group2.data.Test getCurrentRunningTest() {
			return currentTest;
		}
		
		public void setCurrentTest(kuleuven.group2.data.Test test) {
			currentTest = test;
		}
		
	}

}