package dataTransform.jobs.convertcsv.transformations.properties;

import java.util.Properties;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dataTransform.jobs.convertcsv.transformations.Transformation;
import dataTransform.utils.PropertiesWrapper;

public class Property extends Transformation {

	private static Logger log = LogManager.getLogger(Property.class);
	
	private static Properties props = PropertiesWrapper.getInstance();

	private String key = "";
	
	@XmlAttribute
	public String getKey() {return key;}
	public void setKey(String $in) {
		key = $in;
		log.trace("setting key : " + key);
	}
	
	@Override
	public String transform(String $in, CSVRecord $record) {
		
		log.trace("RUNNING : Property.transform(String $in, CSVRecord $record) : " + $in);
		
		return props.getProperty(this.getKey());
	}
	
}
