package kuleuven.group2.policy;

import java.util.Comparator;
import java.util.List;

import kuleuven.group2.data.Test;

/**
 * An interface that has to be implemented for the different policies
 * according to the order in which tests need to be executed.
 * 
 * @author 	Group 2
 * @version	17 November 2013
 *
 */
public interface Policy extends Comparator<Test> {
	
	/**
	 * Sorts the given tests according to this policy.
	 * 
	 * @param 	tests
	 * 			The tests that needs to be sorted.
	 */
	public void getSortedTestAccordingToPolicy(Test[] tests);
		
	/**
	 * Sorts the given tests according to this policy.
	 * 
	 * @param 	tests
	 * 			The tests that needs to be sorted.
	 */
	public void getSortedTestAccordingToPolicy(List<Test> tests);
}