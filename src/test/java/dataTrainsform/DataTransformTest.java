package dataTrainsform;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dataTrainsform.jobs.clean.file.CleanFileTest;
import dataTransform.xml.Profile;

public abstract class DataTransformTest {

	private static Logger log = LogManager.getLogger(DataTransformTest.class);
	
	public static ClassLoader classLoader = CleanFileTest.class.getClassLoader();
	

	public void executeProfile(String $fileLocation) throws Exception {
		
		File file = new File(classLoader.getResource($fileLocation).getFile());
		Profile profile = new Profile(file);
		profile.execute();
		
	}
	
	public void removeFile(File $file) throws Exception {
		try {
			if ($file.exists()) {
				$file.delete();
			}
		} catch (Exception e) {
			log.error(e);
		}
		
	}
	
}
