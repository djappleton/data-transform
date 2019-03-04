package dataTransform.jobs.convertcsv.transformations.string;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Value {
	
	private static Logger log = LogManager.getLogger(Value.class);

	private String type = "interpreted";
	private String value = "";
	
	@XmlAttribute
	public String getType() {return type;}
	public void setType(String $in) {
		type = $in;
		log.trace("setting type : " + type);
	}	
	
	@XmlValue
	public String getValue() {return value;}
	public void setValue(String $in) {
		value = $in;
		log.trace("setting value : " + value);
	}
	
	public static String enterpretValue(Value $value, CSVRecord $record) {
		
		log.trace("RUNNING : enterpretValue(Value $value, CSVRecord $record)");
		
		String retVal = "";
		
		if (!StringUtils.isEmpty($value.getValue())) {
			
			if ($value.getType().equals("evaluated") && !$value.getValue().toLowerCase().equals("null")) {
				retVal = $record.get($value.getValue());
				
			} else {
				retVal = $value.getValue();
					
			}
		
		}
		
		if (retVal.toLowerCase().equals("null")) {
			retVal = "";
		}
		
		log.trace("COMPLETED : enterpretValue(Value $value, CSVRecord $record) : " + retVal);
		
		return retVal;
	}
	
}
