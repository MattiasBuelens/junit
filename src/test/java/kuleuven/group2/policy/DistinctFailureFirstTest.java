package kuleuven.group2.policy;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class DistinctFailureFirstTest extends PolicyTest{

	@Test
	public void correct_order_test() {
		Policy policy = new DistinctFailureFirst();
		kuleuven.group2.data.Test[] result = policy.getSortedTestsAccordingToPolicy(testDatabase);
		
		if (result[0] == test1) {
			if (result[1] == test2) {
				assertTrue(result[2] == test3);
				assertTrue(result[3] == test4);
			} else if (result[1] == test3) {
				assertTrue(result[2] == test2);
				assertTrue(result[3] == test4);
			} else {
				fail();
			}
		} else if (result[0] == test2) {
			assertTrue(result[1] == test1);
			assertTrue(result[2] == test3);
			assertTrue(result[3] == test4);
		} else if (result[0] == test3) {
			assertTrue(result[1] == test1);
			assertTrue(result[2] == test2);
			assertTrue(result[3] == test4);
		} else {
			fail();
		}
		
		result = policy.getSortedTestsAccordingToPolicy(testDatabase, new kuleuven.group2.data.Test[] {test2, test3, test4});
		
		if (result[0] == test2) {
			assertTrue(result[1] == test3);
			assertTrue(result[2] == test4);
		} else if (result[0] == test3) {
			assertTrue(result[1] == test2);
			assertTrue(result[2] == test4);
		} else {
			fail();
		}
		
		List<kuleuven.group2.data.Test> list = new ArrayList<kuleuven.group2.data.Test>();
		list.add(test2);
		list.add(test3);
		list.add(test4);
		result = policy.getSortedTestsAccordingToPolicy(testDatabase, list);
		
		if (result[0] == test2) {
			assertTrue(result[1] == test3);
			assertTrue(result[2] == test4);
		} else if (result[0] == test3) {
			assertTrue(result[1] == test2);
			assertTrue(result[2] == test4);
		} else {
			fail();
		}
	}
	
	@Test
	public void immutable_input_test() {
		Policy policy = new DistinctFailureFirst();
		
		kuleuven.group2.data.Test[] input = new kuleuven.group2.data.Test[] {test2, test3, test4};
		policy.getSortedTestsAccordingToPolicy(testDatabase, input);
		assertTrue(input[0] == test2);
		assertTrue(input[1] == test3);
		assertTrue(input[2] == test4);
		assertEquals(input.length, 3);
		
		List<kuleuven.group2.data.Test> list = new ArrayList<kuleuven.group2.data.Test>();
		list.add(test2);
		list.add(test3);
		list.add(test4);
		policy.getSortedTestsAccordingToPolicy(testDatabase, list);
		assertTrue(list.get(0) == test2);
		assertTrue(list.get(1) == test3);
		assertTrue(list.get(2) == test4);
		assertEquals(list.size(), 3);
	}
}
