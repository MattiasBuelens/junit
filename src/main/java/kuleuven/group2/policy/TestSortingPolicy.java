package kuleuven.group2.policy;

import java.util.Collection;
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
public interface TestSortingPolicy {
	
	/*
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
	public List<Test> getSortedTests(TestDatabase testDatabase);
		
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
	public List<Test> getSortedTests(TestDatabase testDatabase, Collection<Test> tests);
	
	/**
	 * Checks if this test sorting policy contains the given
	 * test sorting policy.
	 * 
	 * @param	policy
	 * 			The test sorting policy that has to be checked.
	 * @return	True if and only if this test sorting policy
	 * 			contains the given test sorting policy.
	 */
	public boolean contains(TestSortingPolicy policy);
}