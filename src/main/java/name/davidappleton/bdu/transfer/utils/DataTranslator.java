package name.davidappleton.bdu.transfer.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dataTransform.utils.IdTracker;
import dataTransform.utils.PropertiesWrapper;

public class DataTranslator {

	private static Logger log = LogManager.getLogger(DataTranslator.class);

	private String dynamicsColumnId = "";
	private String salesForceColumnId = "";
	private String defaultValue = "";
	private String[] dataProcessorArr = null;

	private final DateFormat dynamicsDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");  //2010-08-23 11:04:26.000
	private final DateFormat salesForceDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");  //YYYY-MM-DDThh:mm:ss
	
	private Properties props = PropertiesWrapper.getInstance();

	public String getDynamicsColumnId() {
		return dynamicsColumnId;
	}

	public void setDynamicsColumnId(String $in) {
		dynamicsColumnId = $in;
		log.trace("setting dynamicsColumnId : " + dynamicsColumnId);
	}

	public String getSalesForceColumnId() {
		return salesForceColumnId;
	}

	public void setSalesForceColumnId(String $in) {
		salesForceColumnId = $in;
		log.trace("setting salesForceColumnId : " + salesForceColumnId);
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String $in) {
		defaultValue = $in;
		log.trace("setting defaultValue : " + defaultValue);
	}

	public void setDataProcessorArr(String $in) {
		dataProcessorArr = StringUtils.split($in, "&");
		log.trace("setting dataProcessorArr");
	}

	public String getValue(CSVRecord $record, String $in) {

		//log.debug("RUNNING : getValue(String $in) ::: " + $in);
		
		String retVal = (StringUtils.isEmpty($in)) ? this.getDefaultValue() : $in;
		
		retVal = applyProcessors($record, retVal);
		
		retVal = (retVal != null && retVal.toLowerCase().equals("null")) ? "" : retVal;
		
		//log.debug("COMPLETED : getValue(String $in) ::: " + retVal);
		
		return retVal;

	}

	private String applyProcessors(CSVRecord $record, String $in) {
		String retVal = $in;

		
		
		if (this.dataProcessorArr != null) {
			for (String processor : this.dataProcessorArr) {
				
				//log.info("PROCESSOR --> " + processor);
				
				if (processor.indexOf("lowercase") == 0) {
					retVal = retVal.toLowerCase();
					
				} else if (processor.indexOf("uppercase") == 0) {
					retVal = retVal.toUpperCase();
					
				} else if (processor.indexOf("convertToBDUEmail") == 0) {
					retVal = StringUtils.replace(retVal, "barnardos.org.uk", "barnardos.org.bdu");
					
				} else if (processor.indexOf("systemId") == 0) {
					String prefix = StringUtils.substringBetween(processor, "(", ")");
					if (prefix != null) {
						retVal = prefix + ":" + retVal;
					}
					
					//retVal = IdTracker.getValue(retVal);
					
				} else if (processor.indexOf("concat") == 0) {

					String concatenations = StringUtils.substringBetween(processor, "(", ")");
					
					retVal = concat($record, concatenations);
					
				} else if (processor.indexOf("seperate") == 0) {

					String concatenations = StringUtils.substringBetween(processor, "(", ")");
					
					retVal = seperate($record, concatenations);
					
				} else if (processor.indexOf("date") == 0) {

					retVal = convertDate(retVal, this.dynamicsDateFormat, this.salesForceDateFormat);
					
				} else if (processor.indexOf("properties") == 0) {
					
					String propertyId = StringUtils.substringBetween(processor, "(", ")");
					
					retVal = props.getProperty(propertyId);
					
					//log.info("PROPERTY : " + propertyId + " --> " + retVal);
					
				}
	
			}
		}

		return retVal;
	}
	
	private String concat(CSVRecord $record, String concatenations) {
		
		String retVal = "";
		
		String[] concatenationsArr = StringUtils.split(concatenations, ",");
		
		for (String concat : concatenationsArr) {
			
			if (concat.trim().indexOf("\"") == 0) {
				retVal += StringUtils.substringBetween(concat, "\"");
			} else {
				retVal += ($record.get(concat.trim()));
			}
		}
		
		return retVal;
	}
	
	private String convertDate(String $date, DateFormat $sourceFormat, DateFormat $destinationFormat) {
		
		String retVal = "";
		
		try {
			retVal = $destinationFormat.format($sourceFormat.parse($date));
		} catch (ParseException e) {
			log.error("Unable to parse date : " + e);
			retVal = $date;
		}
		
		return retVal;
	}
	
	private String seperate(CSVRecord $record, String concatenations) {
		
		String retVal = "";
		
		String[] concatenationsArr = StringUtils.split(concatenations, ",");
		
		int record = 0;
		String seperator = (concatenationsArr[0].trim().indexOf("\"") == 0) ? StringUtils.substringBetween(concatenationsArr[0], "\"") : concatenationsArr[0];
		
		
		for (int i=1; i<concatenationsArr.length; i++) {
			
			String concat = concatenationsArr[i];
			
			String value = "";
			
			if (concat.trim().indexOf("\"") == 0) {
				value = StringUtils.substringBetween(concat, "\"");
			} else {
				value = $record.get(concat.trim());
			}
			
			if (value != null && !value.trim().toLowerCase().equals("") && !value.trim().toLowerCase().equals("null")) {
				if (record > 0) {
					retVal += seperator;
				}
				record++;
				
				retVal += (value);
			}
			
			//log.info("CONCAT : " + value);
			
		}
		
		return retVal;
	}
	
	
	
	public static List<String> getSalesForceColumnNames(List<DataTranslator> $dataTranslatorList) {
		List<String> nameList = new ArrayList<String>();
		
		for (DataTranslator dataTranslator : $dataTranslatorList) {
			nameList.add(dataTranslator.getSalesForceColumnId());
		}
		
		return nameList;
	}
	
	public static List<String> getDynamicsColumnNames(List<DataTranslator> $dataTranslatorList) {
		List<String> nameList = new ArrayList<String>();
		
		for (DataTranslator dataTranslator : $dataTranslatorList) {
			nameList.add(dataTranslator.getDynamicsColumnId());
		}
		
		return nameList;
	}

}
