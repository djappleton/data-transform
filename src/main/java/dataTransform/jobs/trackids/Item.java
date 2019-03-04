package dataTransform.jobs.trackids;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Item {

	private static Logger log = LogManager.getLogger(Item.class);

	private String key = null;
	private String value = null;
	
	
	@XmlAttribute
	public String getKey() {return key;}
	public void setKey(String $in) {
		key = $in;
		log.trace("setting key : " + key);
	}
	
	@XmlAttribute
	public String getValue() {return value;}
	public void setValue(String $in) {
		value = $in;
		log.trace("setting value : " + value);
	}
}
