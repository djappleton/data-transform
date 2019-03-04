package dataTransform.jobs.convertcsv.transformations.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dataTransform.jobs.convertcsv.transformations.Transformation;

public class DateConverter extends Transformation {
	
	private static Logger log = LogManager.getLogger(DateConverter.class);

	private final DateFormat dynamicsDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); // 2010-08-23
	private final DateFormat salesForceDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.UK); // YYYY-MM-DDThh:mm:ss
	private final DateFormat salesForceDateFormatNoTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // YYYY-MM-DDThh:mm:ss

	String mustBeOnOrAfter = "";

	@XmlAttribute
	public String getMustBeOnOrAfter() {return mustBeOnOrAfter;}
	public void setMustBeOnOrAfter(String $in) {
		mustBeOnOrAfter = $in;
		log.trace("setting mustBeOnOrAfter : " + mustBeOnOrAfter);
	}
	
	@Override
	public String transform(String $in, CSVRecord $csvRecord) {
		String retVal = "";

		try {
			
			Date thisDate = dynamicsDateFormat.parse($in);
			Date compareDate = null;

			if (StringUtils.isNotEmpty(this.getMustBeOnOrAfter())) {
				compareDate = dynamicsDateFormat.parse($csvRecord.get(this.getMustBeOnOrAfter()));
				
				if (compareDate != null && compareDate.before(thisDate)) {
										
					thisDate = compareDate;
				}
			}
			
			retVal = salesForceDateFormat.format(thisDate);
			
			Calendar c = Calendar.getInstance();
			c.setTime(thisDate);
			
			if (retVal.indexOf("+0100") > -1) {
				c.add(Calendar.HOUR, 2);
				retVal = salesForceDateFormatNoTime.format(c.getTime());
			}
			
			
		} catch (ParseException e) {
			log.error("Unable to parse date : " + e);
			retVal = $in;
		}

		log.trace("FINAL DATE : " + retVal);
		
		return retVal;
	}
}
