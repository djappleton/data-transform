package dataTransform.jobs.convertcsv.transformations.id;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dataTransform.jobs.convertcsv.transformations.Transformation;
import dataTransform.jobs.convertcsv.transformations.properties.Property;
import dataTransform.utils.IdTracker;

public class SystemId extends Transformation {

	private static Logger log = LogManager.getLogger(Property.class);

	String useDefaultIfNotFound = "";
	
	String prefix = "";
	String defaultValue = "";
	String defaultKey = "";
	String ignoreIfFound = "";

	@XmlAttribute
	public String getUseDefaultIfNotFound() {return useDefaultIfNotFound;}
	public void setUseDefaultIfNotFound(String $in) {
		useDefaultIfNotFound = $in;
		log.trace("setting useDefaultValue : " + useDefaultIfNotFound);
	}

	@XmlAttribute
	public String getPrefix() {return prefix;}
	public void setPrefix(String $in) {
		prefix = $in;
		log.trace("setting prefix : " + prefix);
	}

	@XmlAttribute
	public String getDefaultValue() {return defaultValue;}
	public void setDefaultValue(String $in) {
		defaultValue = $in;
		log.trace("setting defaultValue : " + defaultValue);
	}

	@XmlAttribute
	public String getDefaultKey() {return defaultKey;}
	public void setDefaultKey(String $in) {
		defaultKey = $in;
		log.trace("setting defaultKey : " + defaultKey);
	}

	@XmlAttribute
	public String getIgnoreIfFound() {return ignoreIfFound;}
	public void setIgnoreIfFound(String $in) {
		ignoreIfFound = $in.toLowerCase();
		log.trace("setting ignoreIfFound : " + ignoreIfFound);
	}


	
	@Override
	public String transform(String $in, CSVRecord $record) {
		
		log.trace("RUNNING : SystemId.transform(String $in, CSVRecord $record) : " + $in);
		
		String key = (!this.getPrefix().equals("")) ? this.getPrefix() + $in : $in;
		String value = IdTracker.getValue(key);
		
		if (value == null && this.getUseDefaultIfNotFound().equals("true")) {
			if (!this.getDefaultKey().equals("")) {
				value = IdTracker.getValue(this.getDefaultKey());
			} else {
				value = IdTracker.getValue(key, this.getDefaultValue());
			}
		}
		
		if (value != null) {
			if (this.getIgnoreIfFound().equals("true")) {
				value = "";
			} 
		} else {
			value = $in;
		}
			
		if (value.equals(this.getPrefix() + "NULL")) {
			value = "";
		}
		
//		if (this.getIgnoreIfFound().equals("true") && !value.equals("")) {
//			value = "";
//		}
		
		return value;
	}
	
	
	public String transformOld(String $in, CSVRecord $record) {
		
		log.trace("RUNNING : SystemId.transform(String $in, CSVRecord $record) : " + $in);
		
		if (this.getIgnoreIfFound().equals("true") && this.getDefaultValue().equals("") && !this.getUseDefaultIfNotFound().equals("true")) {
			this.setDefaultValue("##DEAFULT##");
			this.setUseDefaultIfNotFound("true");
		}
			
		String key = (!this.getPrefix().equals("")) ? this.getPrefix() + $in : $in;
		String value = "";
		
		if (this.getUseDefaultIfNotFound().equals("true")) {
			value = IdTracker.getValue(key, this.getDefaultValue());
		} else {
			value = IdTracker.getValue(key);
		}
		
		
		if (value.equals(this.getPrefix() + "NULL")) {
			value = "";
		}		
		
		
		
		if (this.getIgnoreIfFound().equals("true") && (value.equals(this.getDefaultValue()) || !value.equals(""))) {
			value = $in;
			
		}
		
		
		return value;
	}
	
}
