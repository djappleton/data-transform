package dataTransform.jobs.wait;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dataTransform.jobs.Job;
import dataTransform.xml.Profile;

/**
 * <p>A Job that will pause execution for a number of seconds.</p>
 * <p>This can be convenient in instances where some processing is 
 * required on a third party system and a basic pause will allow for
 * this to complete.</p>
 * 
 * @author david.appleton
 *
 */
@XmlRootElement
public class Wait extends Job {

	private static Logger log = LogManager.getLogger(Wait.class);

	private int numberOfSeconds = 0;

	/**
	 * <p>The number of seconds that this Wait period should last.</p>
	 * 
	 * @return
	 */
	@XmlAttribute
	public int getNumberOfSeconds() {return numberOfSeconds;}
	public void setNumberOfSeconds(int $in) {
		numberOfSeconds = $in;
		log.trace("setting numberOfSeconds : " + numberOfSeconds);
	}

	@Override
	public String execute(Profile $profile) throws Exception {

		int index = this.getNumberOfSeconds();
			
		while (index > 0) {
			log.debug("Waiting : Time Left --> " + index + " seconds. ");
			index--;
			Thread.sleep(1000);
		}
		
		return "OK";
	}

	@Override
	public String initialise() throws Exception {
		return null;
		
	}

}
