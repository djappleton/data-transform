package dataTransform.jobs;

import dataTransform.xml.Profile;

public abstract class Job {
	
	/**
	 * <p>Take any steps required to set up the Job.</p>
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract String initialise() throws Exception;
	
	/**
	 * <p>Execute the Job.</p>
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract String execute(Profile $profile) throws Exception;

	
}
