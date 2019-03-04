package dataTransform.jobs.convertcsv.transformations.string;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dataTransform.jobs.convertcsv.transformations.Transformation;

public class Concatenate extends Transformation {

	private static Logger log = LogManager.getLogger(Concatenate.class);

	List<Value> valueList = new ArrayList<Value>();

	@XmlElement(name = "value")
	public List<Value> getValueList() {return valueList;}
	public void setValueList(List<Value> $in) {
		valueList = $in;
		log.trace("setting valueList : " + valueList);
	}
	
	@Override
	public String transform(String $in, CSVRecord $record) {
		
		log.trace("RUNNING : Concatenate.transform(String $in, CSVRecord $record) : " + $in);
		
		StringBuffer sb = new StringBuffer();
		
		for (Value value : this.getValueList() ) {
			
			sb.append(Value.enterpretValue(value, $record));
			
		}
		
		log.trace("COMPLETED : transform(String $in, CSVRecord $record) : " + sb.toString());
		
		return sb.toString();
	}
	
}
