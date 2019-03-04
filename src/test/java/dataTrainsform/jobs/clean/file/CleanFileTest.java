package dataTrainsform.jobs.clean.file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import dataTrainsform.DataTransformTest;

public class CleanFileTest extends DataTransformTest{
	
	private static Logger log = LogManager.getLogger(CleanFileTest.class);
	
	@Test
	public void configText() throws Exception {

		try {
			executeProfile("testFiles/dataTransform/jobs/clean/file/config.xml");
			
			File expectedFile = new File(DataTransformTest.classLoader.getResource("testFiles/dataTransform/jobs/clean/file/Expected.csv").getFile());
			File derivedFile = new File(DataTransformTest.classLoader.getResource("testFiles/dataTransform/jobs/clean/file/Derived.csv").getFile());
				
			assertEquals(FileUtils.readFileToString(derivedFile, "UTF-8"), 
					     FileUtils.readFileToString(expectedFile, "UTF-8"), 
					     "The contents of the files do not match.");
			
			removeFile(derivedFile);
			
		} catch (Exception e) {
			log.error(e);
		}
	}

}
