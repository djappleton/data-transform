package dataTransform.jobs.clean.file;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@XmlRootElement
public class Replacement {
	
	private static Logger log = LogManager.getLogger(Replacement.class);

	private String searchFor = "";
	private String replaceWith = "";
	private boolean recursive = false;
	
	@XmlAttribute(name = "searchFor")
	public String getSearchFor() {return searchFor;}
	public void setSearchFor(String $in) {
		searchFor = $in;
		log.trace("setting searchFor : " + searchFor);
	}
	
	@XmlAttribute(name = "replaceWith")
	public String getReplaceWith() {return replaceWith;}
	public void setReplaceWith(String $in) {
		replaceWith = $in;
		log.trace("setting replaceWith : " + replaceWith);
	}
	
	@XmlAttribute(name = "recursive")
	public boolean getRecursive() {return recursive;}
	public void setRecursive(boolean $in) {
		recursive = $in;
		log.trace("setting recursive : " + recursive);
	}
	
	/**
	 * 
	 * 
	 * @param $in
	 * @return
	 */
	public String replace(String $in) {
		
		String retVal = $in;
		
		if (this.getRecursive() == true) {
			while (retVal.indexOf(this.getSearchFor()) > -1) {
				retVal = StringUtils.replaceEach(retVal, new String[] {this.getSearchFor()}, new String[] {this.getReplaceWith()});
			}			
		} else {
			retVal = StringUtils.replaceEach(retVal, new String[] {this.getSearchFor()}, new String[] {this.getReplaceWith()});				
		}		
		
		return retVal;
		
	}
}
