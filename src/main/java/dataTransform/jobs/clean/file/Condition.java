package dataTransform.jobs.clean.file;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@XmlRootElement
public class Condition {

	private static Logger log = LogManager.getLogger(Condition.class);
	
	private String contains = "";
	private boolean isEmpty = false;
	
	@XmlAttribute
	public String getContains() {return contains;}
	public void setContains(String $in) {
		contains = $in;
		log.trace("setting contains : " + contains);
	}
	
	@XmlAttribute
	public boolean getIsEmpty() {return isEmpty;}
	public void setIsEmpty(boolean $in) {
		isEmpty = $in;
		log.trace("setting isEmpty : " + isEmpty);
	}
	
	/**
	 * 
	 * 
	 * @param $line
	 * @return
	 */
	public boolean isConditionMet(String $line) {
		
		boolean retVal = false;
		
		if (this.getIsEmpty() == true) {
			String trimmedLine = StringUtils.trimToEmpty($line);
			if (StringUtils.isEmpty(trimmedLine)) {
				retVal = true;
			} 
		}
		
		if (this.getContains() != null && !this.getContains().equals("") && $line.indexOf(this.getContains()) > -1) {
			retVal = true;
		}
				
		return retVal;
	}
	
}
