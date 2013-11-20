package kuleuven.group2.policy;

import java.util.List;

import kuleuven.group2.data.Test;
import kuleuven.group2.data.TestDatabase;

/**
 * An interface that has to be implemented for the different policies
 * according to the order in which tests need to be executed.
 * 
 * @author 	Group 2
 * @version	17 November 2013
 *
 */
public interface Policy {
	
	/*
	 * Policy is not an abstract class for the same reason Comparator
	 * is not an abstract class.
	 * If significant similarities between policies appear in the future,
	 * the Policy interface can change to an abstract class.
	 */
	
	/**
	 * Sorts the tests of the given test database according to this policy.
	 * 
	 * @param	testDatabase
	 * 			The test database which contains the given tests.
	 * @return	The tests of the given test database according to this policy.
	 */
	public Test[] getSortedTestsAccordingToPolicy(TestDatabase testDatabase);
	
	/**
	 * Sorts the given tests according to this policy.
	 * 
	 * @param	testDatabase
	 * 			The test database which contains the given tests.
	 * @param 	tests
	 * 			The tests that needs to be sorted.
	 * @post	The given array may be modified.
	 * @return	The tests of the given test database according to this policy.
	 */
	public Test[] getSortedTestsAccordingToPolicy(TestDatabase testDatabase, Test[] tests);
		
	/**
	 * Sorts the given tests according to this policy.
	 * 
	 * @param	testDatabase
	 * 			The test database which contains the given tests.
	 * @param 	tests
	 * 			The tests that needs to be sorted.
	 * @post	The given collection may be modified.
	 * @return	The tests of the given test database according to this policy.
	 */
	public Test[] getSortedTestsAccordingToPolicy(TestDatabase testDatabase, List<Test> tests);
}