package dataTrainsform.jobs.clean.file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import dataTransform.jobs.clean.file.Condition;

public class ConditionTest {

	@Test
	public void emptyConditionTest() {
		Condition c = new Condition();
		c.setIsEmpty(true);

		assertEquals(c.isConditionMet(""), true, "Empty String should meet the condition.");
		assertEquals(c.isConditionMet("   "), true, "Blank Spaces should meet the condition.");
	}

	@Test
	public void containsConditionTest() {
		Condition c = new Condition();
		c.setContains("-----");

		assertEquals(c.isConditionMet("-----"), true, "----- String should meet the condition.");
		assertEquals(c.isConditionMet("  -----  "), true, "----- String should meet the condition.");
		assertEquals(c.isConditionMet("  -- ---  "), false, "-- --- String should NOT meet the condition.");
	}
	
}
