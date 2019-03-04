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

public class Seperate extends Transformation {

	private static Logger log = LogManager.getLogger(Seperate.class);

	String seperator = "";
	int asciiCharSeperator = -1;
	List<Value> valueList = new ArrayList<Value>();

	@XmlAttribute
	public String getSeperator() {return seperator;}
	public void setSeperator(String $in) {
		seperator = $in;
		log.trace("setting seperator : " + seperator);
	}	

	@XmlAttribute
	public int getAsciiCharSeperator() {return asciiCharSeperator;}
	public void setAsciiCharSeperator(int $in) {
		asciiCharSeperator = $in;
		log.trace("setting asciiCharSeperator : " + asciiCharSeperator);
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
		
		String sepVal = "";
		if (!this.getSeperator().equals("")) {
			sepVal = this.getSeperator();
			
		} else if (this.getAsciiCharSeperator() > -1) {
			sepVal = Character.toString((char) this.getAsciiCharSeperator());
			
		}
		
		for (Value value : this.getValueList() ) {
			
			String val = Value.enterpretValue(value, $csvRecord);
			
			if (sb.length() > 0 && !StringUtils.isEmpty(val)) {
				sb.append(sepVal).append(val);
				
			} else if (!StringUtils.isEmpty(val)) {
				sb.append(val);
				
			}
			
		}
		
		log.trace("COMPLETED : Email.transform(String $in, CSVRecord $record) : " + sb.toString());
		
		return sb.toString();
	}

}
