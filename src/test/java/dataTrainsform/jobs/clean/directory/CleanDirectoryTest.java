package dataTrainsform.jobs.clean.directory;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataTrainsform.DataTransformTest;
import dataTrainsform.jobs.clean.file.CleanFileTest;

public class CleanDirectoryTest extends DataTransformTest{
	
	private static Logger log = LogManager.getLogger(CleanFileTest.class);
	

	@BeforeEach
	private void setUp() throws IOException {
		File templateDir = new File(DataTransformTest.classLoader.getResource("testFiles/dataTransform/jobs/clean/directory/templateDir").getFile());
		File testDir =     new File(DataTransformTest.classLoader.getResource("testFiles/dataTransform/jobs/clean/directory/testDir").getFile());
		
		FileUtils.copyDirectory(templateDir, testDir);	
	}
	
	@AfterEach
	private void tearDown() throws IOException {
		File testDir =     new File(DataTransformTest.classLoader.getResource("testFiles/dataTransform/jobs/clean/directory/testDir").getFile());
		
		FileUtils.deleteDirectory(testDir);	
	}
	
	@Test
	public void deleteFilesTest() throws Exception {

		try {
			executeProfile("testFiles/dataTransform/jobs/clean/directory/delete-config.xml");
			
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	@Test
	public void keepFilesTest() throws Exception {

		try {
			executeProfile("testFiles/dataTransform/jobs/clean/directory/keep-config.xml");
			
		} catch (Exception e) {
			log.error(e);
		}
		
	}
}
