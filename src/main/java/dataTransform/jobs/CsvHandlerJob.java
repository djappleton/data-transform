package dataTransform.jobs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@XmlTransient
public abstract class CsvHandlerJob extends FileHandlerJob {

	private static Logger log = LogManager.getLogger(CsvHandlerJob.class);
	
	public Iterable<CSVRecord> loadCSV() throws IOException {
		File f = new File(this.getInFile());
		Reader csvReader = new FileReader(f);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().withDelimiter(',').withQuote(null).parse(csvReader);
		
		return records;
	}
	
	
}
