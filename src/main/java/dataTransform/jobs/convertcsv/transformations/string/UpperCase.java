package dataTransform.jobs.convertcsv.transformations.string;

import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dataTransform.jobs.convertcsv.transformations.Transformation;

public class UpperCase extends Transformation {

	private static Logger log = LogManager.getLogger(UpperCase.class);

	@Override
	public String transform(String $in, CSVRecord $record) {
		
		log.trace("RUNNING : UpperCase.transform(String $in, CSVRecord $record) : " + $in);
		
		return $in.toUpperCase();
	}
	
}
