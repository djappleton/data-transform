package dataTransform.jobs.convertcsv;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dataTransform.jobs.Job;
import dataTransform.jobs.convertcsv.transformations.UnrecognisedValueException;
import dataTransform.xml.Profile;

@XmlRootElement
public class ConvertCSV extends Job {
	
	private static Logger log = LogManager.getLogger(ConvertCSV.class);
	
	
	private String inFileLocation = "";
	private String outDirLocation = "";
	private boolean excludeRowsIfEmpty = false;
	private String excludeRowIfEmptyColumns = null;
	private int batchSize = 200;
	private List<Column> columnList = new ArrayList<Column>();

	public String outputFilename = "data";
	private File inFile = null;
	private File workingFile = null;
	private File outDir = null;
	
	
	@XmlAttribute(name = "inFileLocation")
	public String getInFile() {return inFileLocation;}
	public void setInFile(String $in) {
		inFileLocation = $in;
		log.trace("setting inFile : " + inFileLocation);
	}
	
	@XmlAttribute
	public String getFilename() {return outputFilename;}
	public void setFilename(String $in) {
		outputFilename = $in;
		log.trace("setting outputFilename : " + outputFilename);
	}
	
	@XmlAttribute(name = "outDirLocation")
	public String getOutDir() {return outDirLocation;}
	public void setOutDir(String $in) {
		outDirLocation = $in;
		log.trace("setting outDirLocation : " + outDirLocation);
	}
	
	@XmlAttribute
	public boolean getExcludeRowsIfEmpty() {return excludeRowsIfEmpty;}
	public void setExcludeRowsIfEmpty(boolean $in) {
		excludeRowsIfEmpty = $in;
		log.trace("setting excludeRowsIfEmpty : " + excludeRowsIfEmpty);
	}
	
	@XmlAttribute
	public String getExcludeRowIfEmptyColumns() {return excludeRowIfEmptyColumns;}
	public void setExcludeRowIfEmptyColumns(String $in) {
		excludeRowIfEmptyColumns = $in;
		log.trace("setting excludeRowIfEmptyColumns : " + excludeRowIfEmptyColumns);
	}
	
	@XmlAttribute
	public int getBatchSize() {return batchSize;}
	public void setBatchSize(int $in) {
		batchSize = $in;
		log.trace("setting batchSize : " + batchSize);
	}
	
	@XmlElement(name = "column")
	public List<Column> getColumns() {return columnList;}
	public void setColumns(List<Column> $in) {
		columnList = $in;
		log.trace("setting columnList : " + columnList);
	}
	
	@Override
	public String execute(Profile $profile) throws Exception {

		initialiseFiles();
		
		String result = "";
	
		File rawData = removeUnwantedRows();
		try {
			result = removeUnwantedColumns(rawData);
		} catch (IOException ioe) {
			log.error("ERROR : " + ioe);
		}

		return result;
		
	}
	
	private void initialiseFiles() throws Exception {
		inFile = new File(this.getInFile());
		outDir = new File(this.getOutDir());
		workingFile = new File(outDir.getPath() + File.separator + inFile.getName());

		
		log.info(workingFile.getAbsolutePath());
		
		if (!inFile.exists()) {
			log.error("inFile does not exist : " + inFile.getName());
			throw new Exception("inFile does not exist");
		} else {
			String inFileString = FileUtils.readFileToString(inFile, "UTF-8");

			
			do {
				inFileString = StringUtils.replace(inFileString, "  ", " ");
			} while (inFileString.indexOf("  ") > -1);

			inFileString = StringUtils.replace(inFileString, " ^", "^");
			inFileString = StringUtils.replace(inFileString, "^ ", "^");

						
			inFileString = inFileString.replace(" \r\n", "\r\n");
			inFileString = inFileString.replace(" \n", "\n");
			
			inFileString = inFileString.replace("\r\n", "[NEWLINE]");
			inFileString = inFileString.replace("\n", "[TEXTBREAK]");
			inFileString = inFileString.replace(Character.toString((char)10), "[TEXTBREAK]");
			inFileString = inFileString.replace(System.getProperty("line.separator"), "[TEXTBREAK]");
			
			inFileString = StringUtils.replace(inFileString, "[NEWLINE]", "\r\n");
			
			FileUtils.writeStringToFile(workingFile, inFileString, "UTF-8");
		}
		
		FileUtils.forceMkdir(outDir);
		log.info("outFile created.");
	}
	
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private File removeUnwantedRows() throws IOException {

		log.trace("RUNNING : removeUnwantedRows() ");

		File outFile = new File(this.getOutDir() + "rawData.csv");

		FileUtils.touch(outFile);

		List<String> fileLines = FileUtils.readLines(workingFile, "UTF-8");
		List<String> outFileLines = new ArrayList<String>();
		for (String line : fileLines) {
			if (line.trim().equals("") || line.indexOf("-") == 0 || line.indexOf("rows affected)") > 0) {
				// do nothing
			} else {
				outFileLines.add(line);
			}

		}

		FileUtils.writeLines(outFile, outFileLines, "\n");

		log.trace("COMPLETED : removeUnwantedRows() ");

		return outFile;
	}
	
	private String removeUnwantedColumns(File $rawDataFile) throws IOException {
		
		log.trace("RUNNING : removeUnwantedColumns()");
		
		String result = "OK";
		
		int fileIndex = 1;
		int recordCount = 0;
		
		File outFile = null;
		CSVPrinter csvPrinter = null;
		
		Reader csvReader = new FileReader($rawDataFile);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().withDelimiter(',').withQuote(null).parse(csvReader);
        
        Iterator<CSVRecord> it = records.iterator();
        while (it.hasNext()) {
        	
        	if (outFile == null || recordCount >= this.getBatchSize()) {
        		recordCount = 0;
        		
        		if (csvPrinter != null) {
        			csvPrinter.flush(); 
        	        csvPrinter.close();
        		}
        		
        		String fileLocation = this.getOutDir() + this.getFilename() + String.format("%09d", fileIndex++) + ".csv";
        		outFile = new File(fileLocation);
        		FileUtils.touch(outFile);
        		
        		//CSVPrinter tmpCsv = new CSVPrinter(tmpFile);
                csvPrinter = new CSVPrinter(new FileWriter(outFile), CSVFormat.EXCEL.withDelimiter(',').withQuote('"').withQuoteMode(QuoteMode.ALL));

                //Add header row.
                csvPrinter.printRecord(this.getDestinationColumnNames());
        	}
        	
        	
        	CSVRecord record;
        	
        	try {
        		
        		
	        	record = it.next();
	        			        	
	        	boolean includeRecord = true;
	        
	        	StringBuffer unrecognisedValuesSb = new StringBuffer();
	        	
	        	List<String> values = new ArrayList<String>();
	        	
				for (Column column : this.getColumns()) {
					
					String value = "";
					
					try {
						value = column.getValue(record);
						
		        		log.info("Value = " + value);
						
					} catch (UnrecognisedValueException urve) {
						
						String rawValue = column.getRawValue(record);
						if (!rawValue.equals("NULL")) {
							String errorStr = "<li>The value used in field '" + column.getDestinationName() + "' did not pass the validation criteria.  The value was: '" + column.getRawValue(record) + "'</li>";
						
							unrecognisedValuesSb.append(errorStr);
							log.error(errorStr);
						}
						value = "";
						
					}
					
					value = sanitise(value, column.getDestinationDataType().equals("html"));
					
					values.add(value);
					
					log.debug("SOURCE COLUMN : " + column.getSourceName() + " --> DESTINATION COLUMN : " + column.getDestinationName() + " ::: VALUE USED : " + value);
					
					if (column.getExcludeRowIfEmpty() == true && StringUtils.isEmpty(value)) {
						includeRecord = false;
					}				
					
					if (this.getExcludeRowsIfEmpty() && isEmptyRecord(values)) {
						includeRecord = false;
					}	
					
				}
				
				if (!StringUtils.isEmpty(this.getExcludeRowIfEmptyColumns()) && areSpecificColumnsEmpty(values)) {
					includeRecord = false;
				}
				
				if (includeRecord) {
					
					if (unrecognisedValuesSb.length() > 0) {
						unrecognisedValuesSb.insert(0, "Migration errors:<ul>");
						unrecognisedValuesSb.append("</ul>");
						
						int recordIndex = 0;
						for (Column column : this.getColumns()) {
							if (column.getAppendUnrecognisedValues() == true) {
								String update = values.get(recordIndex);
								update += unrecognisedValuesSb.toString();
								
								update= sanitise(update, column.getDestinationDataType().equals("html"));
								
								values.set(recordIndex, update);
							}
							
							recordIndex++;
						}
					}
					
		        	recordCount++;
					csvPrinter.printRecord(values);
				}
			
        	} catch (IOException ioe) {
        		log.error("ERROR : " + ioe);
        	}
        
        }
		
		if (csvPrinter != null) {
			csvPrinter.flush(); 
	        csvPrinter.close();
		}
		
		log.trace("COMPLETED : removeUnwanterColumns()");
        
		return result;
	}	
	

	
	public List<String> getDestinationColumnNames() {
		
		log.trace("RUNNING : getDestinationColumnNames()");
		
		List<String> nameList = new ArrayList<String>();
		
		for (Column column : this.getColumns()) {
			nameList.add(column.getDestinationName());
		}
		
		log.trace("COMPLETED : getDestinationColumnNames()");
		
		return nameList;
	}
	
	private boolean isEmptyRecord(List<String> $values) {
		
		log.trace("RUNNING : isEmptyRecord(List<String> $values)");
		
		boolean retVal = true;
		
		for (String value : $values) {
			if (StringUtils.isEmpty(value)) {
				retVal = false;
				break;
			}
		}
		
		log.trace("COMPLETED : isEmptyRecord(List<String> $values)");
		
		return retVal;
		
	}
	
	private boolean areSpecificColumnsEmpty(List<String> $values) {
		
		log.trace("RUNNING : areSpecificColumnsEmpty(List<String> $values)");
		
		boolean retVal = true;
		
		List<String> specifiedColumns = Arrays.asList(StringUtils.split(this.getExcludeRowIfEmptyColumns(), ","));
		
		int columnIndex = 0;
				
		for (Column column : this.getColumns()) {
			
			if (specifiedColumns.contains(column.getDestinationName())) {
				
				if (!StringUtils.isEmpty($values.get(columnIndex))) {
					retVal = false;
					break;
				}
			}
			
			columnIndex++;
		}
		
		log.trace("COMPLETED : areSpecificColumnsEmpty(List<String> $values)");
		
		return retVal;
		
	}
	
	
	private String sanitise(String $value, boolean $isHtml) {

		String retVal = $value.trim();
		
		if ($isHtml) {
			retVal = StringUtils.replace(retVal, "[TEXTBREAK]", "<br />");
			retVal = StringUtils.replace(retVal, "<br>", "<br />");
			retVal = StringUtils.replace(retVal, "<br/>", "<br />");
		} else {
			retVal = StringUtils.replace(retVal, "[TEXTBREAK]", "\n");
			retVal = StringUtils.replace(retVal, "<ul>", "\n");
			retVal = StringUtils.replace(retVal, "</li>", "\n");
			retVal = StringUtils.replace(retVal, "<br>", "\n");
			retVal = StringUtils.replace(retVal, "<br/>", "\n");
			retVal = StringUtils.replace(retVal, "<br />", "\n");

			retVal = StringUtils.replace(retVal, "<li>", "");
			retVal = StringUtils.replace(retVal, "</ul>", "");
		}
		
		return retVal;
	}
	@Override
	public String initialise() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
