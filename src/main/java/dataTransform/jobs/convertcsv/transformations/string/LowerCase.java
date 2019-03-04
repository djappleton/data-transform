package dataTransform.jobs.convertcsv.transformations.string;

import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dataTransform.jobs.convertcsv.transformations.Transformation;

public class LowerCase extends Transformation {

	private static Logger log = LogManager.getLogger(LowerCase.class);

	@Override
	public String transform(String $in, CSVRecord $record) {
		
		log.trace("RUNNING : LowerCase.transform(String $in, CSVRecord $record) : " + $in);
		
		return $in.toLowerCase();
	}


	
}
