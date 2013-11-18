package kuleuven.group2.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kuleuven.group2.data.signature.JavaSignature;
import kuleuven.group2.data.testrun.TestRun;

/**
 * A class that has keepstrack of all tests (Test), tested methods
 * (TestedMethod) and links between them (TestMethodLink) for access by updaters
 * and users.
 * 
 * @author Vital D'haveloose, Ruben Pieters
 */
public class TestDatabase {

	// TODO: FASTER Use a Map with the signature as key?
	protected Collection<TestMethodLink> testMethodLinks = Collections
			.synchronizedCollection(new HashSet<TestMethodLink>());
	protected Set<TestedMethod> methods = Collections.synchronizedSet(new HashSet<TestedMethod>());
	protected Set<Test> tests = Collections.synchronizedSet(new HashSet<Test>());

	// METHODS

	protected boolean containsMethod(JavaSignature signature) {
		for (TestedMethod testedMethod : methods) {
			if (testedMethod.getSignature().equals(signature)) {
				return true;
			}
		}
		return false;
		//TODO: FASTER: cache result
	}
	
	protected TestedMethod getMethod(JavaSignature signature) {
		for (TestedMethod testedMethod : methods) {
			if (testedMethod.getSignature().equals(signature)) {
				return testedMethod;
			}
		}
		throw new IllegalArgumentException("Method with signature " + signature + " not in database.");
	}

	public void addMethod(TestedMethod testedMethod) {
		methods.add(testedMethod);
	}

	protected void removeMethod(TestedMethod testedMethod) {
		methods.remove(testedMethod);
	}

	// TESTS

	protected Test getTest(String testClassName, String testMethodName) {
		for (Test test : tests) {
			if (test.equalName(testClassName, testMethodName)) {
				return test;
			}
		}
		return null;
	}

	public Collection<Test> getAllTests() {
		return tests;
	}

	protected void addTest(Test test) {
		tests.add(test);
	}

	protected void removeTest(Test test) {
		tests.remove(test);
	}

	// TESTRUNS
	protected void addTestRun(TestRun testRun, Test test) {
		addTestRun(testRun, test.getTestClassName(), test.getTestMethodName());
	}

	protected void addTestRun(TestRun testRun, String testClassName, String testMethodName) {
		Test test = getTest(testClassName, testMethodName);
		if (test == null) {
			test = new Test(testClassName, testMethodName);
			addTest(test);
		}
		test.addTestRun(testRun);
	}

	public List<TestRun> getAllTestRuns() {
		List<TestRun> testRuns = new ArrayList<TestRun>();
		for (Test test : tests) {
			testRuns.addAll(test.getTestRuns());
		}
		return testRuns;
	}

	// METHOD-TEST LINKS

	public boolean containsMethodTestLink(TestedMethod testedMethod, Test test) {
		return testMethodLinks.contains(new TestMethodLink(testedMethod, test));
	}

	protected void addMethodTestLink(TestedMethod testedMethod, Test test) {
		testMethodLinks.add(new TestMethodLink(testedMethod, test));
	}

	protected void clearMethodLinks() {
		testMethodLinks.clear();
	}

	public Collection<TestedMethod> getLinkedMethods(Test test) {
		Collection<TestedMethod> linkedMethods = new HashSet<TestedMethod>();
		for (TestMethodLink testMethodLink : testMethodLinks) {
			if (testMethodLink.getTest().equals(test)) {
				linkedMethods.add(testMethodLink.getTestedMethod());
			}
		}
		return linkedMethods;
	}

	public Collection<Test> getLinkedTests(TestedMethod testedMethod) {
		Collection<Test> linkedTests = new HashSet<Test>();
		for (TestMethodLink testMethodLink : testMethodLinks) {
			if (testMethodLink.getTestedMethod().equals(testedMethod)) {
				linkedTests.add(testMethodLink.getTest());
			}
		}
		return linkedTests;
	}

	public void printMethodLinks() {
		for (TestMethodLink testMethodLink : testMethodLinks) {
			System.out.println(testMethodLink);
		}
	}

	public int getNbLinks() {
		return testMethodLinks.size();
	}

}
