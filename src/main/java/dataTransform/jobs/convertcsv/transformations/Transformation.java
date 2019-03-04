package dataTransform.jobs.convertcsv.transformations;

import org.apache.commons.csv.CSVRecord;

public abstract class Transformation {

	public abstract String transform(String $in, CSVRecord $csvRecord) throws UnrecognisedValueException;
	
}
