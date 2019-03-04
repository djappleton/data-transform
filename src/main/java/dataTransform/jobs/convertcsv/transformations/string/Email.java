package dataTransform.jobs.convertcsv.transformations.string;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dataTransform.jobs.convertcsv.transformations.Transformation;

public class Email extends Transformation {

	private static Logger log = LogManager.getLogger(Seperate.class);

	String domain = "";
	String seperator = "";
	List<Value> valueList = new ArrayList<Value>();

	@XmlAttribute
	public String getDomain() {return domain;}
	public void setDomain(String $in) {
		domain = $in;
		log.trace("setting domain : " + domain);
	}

	@XmlAttribute
	public String getSeperator() {return seperator;}
	public void setSeperator(String $in) {
		seperator = $in;
		log.trace("setting seperator : " + seperator);
	}	
	
	@XmlElement(name = "value")
	public List<Value> getValueList() {return valueList;}
	public void setValueList(List<Value> $in) {
		valueList = $in;
		log.trace("setting valueList : " + valueList);
	}
	
	@Override
	public String transform(String $in, CSVRecord $csvRecord) {
		log.trace("RUNNING : Email.transform(String $in, CSVRecord $record) : " + $in);
		
		StringBuffer sb = new StringBuffer();
		
		for (Value value : this.getValueList() ) {
			
			String val = Value.enterpretValue(value, $csvRecord);
			val = StringUtils.replace(val.trim(), " ", ".");
			
			if (sb.length() > 0 && !StringUtils.isEmpty(val)) {
				sb.append(this.getSeperator()).append(val);
				
			} else if (!StringUtils.isEmpty(val)) {
				sb.append(val);
			}
			
		}
		
		sb.append(this.getDomain());
		
		log.trace("COMPLETED : Email.transform(String $in, CSVRecord $record) : " + sb.toString());
		
		return sb.toString();
	}

}
