package dataTrainsform.jobs.clean.file;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import dataTransform.jobs.clean.file.Replacement;

public class ReplacementTest {

	@Test
	public void replaceTest() {
		
		//Test for normal string replacement
		String ts1 = "Hello World!";
		
		Replacement r1 = new Replacement();
		r1.setSearchFor("Hello");
		r1.setReplaceWith("Goodbye");
		
		String rs1 = r1.replace(ts1);
		
		assertTrue(rs1.equals("Goodbye World!"), "Replacement should read Goodbye World!, but was : " + rs1);
		
		
		//Test for recursive string replacement
		String ts2 = "Hello      World!";
		
		Replacement r2 = new Replacement();
		r2.setSearchFor("  ");
		r2.setReplaceWith(" ");
		r2.setRecursive(true);
		
		String rs2 = r2.replace(ts2);
		
		assertTrue(rs2.equals("Hello World!"), "Replacement should read Hello World!, but was : " + rs2);
	}
	
}
