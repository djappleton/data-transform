package dataTransform.jobs.convertcsv.transformations.string;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dataTransform.jobs.convertcsv.transformations.Transformation;
import dataTransform.jobs.convertcsv.transformations.UnrecognisedValueException;

public class Validate extends Transformation {

	private static Logger log = LogManager.getLogger(Validate.class);
	private static UrlValidator urlValidator = new UrlValidator();
	private static EmailValidator emailValidator = EmailValidator.getInstance();
	
	public static List<String> urlExclusions = new ArrayList<String>();
	
	public static String errorEmail = "email.not.valid.on.transfer@barnardos.org.uk";

	static {
		urlExclusions.add("http://n/a");
		urlExclusions.add("http://No file open");
		urlExclusions.add("http://N/A");
		urlExclusions.add("http://N?A");
		urlExclusions.add("http://No file");
		urlExclusions.add("http://x");
	}
	
	String type = "";

	@XmlAttribute
	public String getType() {
		return type;
	}

	public void setType(String $in) {
		type = $in;
		log.trace("setting type : " + type);
	}

	@Override
	public String transform(String $in, CSVRecord $record) throws UnrecognisedValueException {

		log.trace("RUNNING : Validate.transform(String $in, CSVRecord $record) : " + $in);

		String retVal = ($in != null && !$in.toLowerCase().equals("null")) ? "" : $in;

		if (this.getType().equals("email")) {
			retVal = validateEmail($in);

		} else if (this.getType().equals("phone")) {
			retVal = validatePhone($in);

		} else if (this.getType().equals("url")) {
			retVal = validateURL($in);

		}

		return retVal;
	}

	private String validateEmail(String $in) throws UnrecognisedValueException {

		String retVal = $in.trim();

		if (!emailValidator.isValid(retVal)) {
			throw new UnrecognisedValueException();
		}

		return retVal;
	}

	private String validateURL(String $in) throws UnrecognisedValueException {
		
		if (StringUtils.isNoneEmpty($in)) {
			if (!urlValidator.isValid($in) || urlExclusions.contains($in)) {
				throw new UnrecognisedValueException();
			}
		}

		return $in;
	}

	private String validatePhone(String $in) throws UnrecognisedValueException {

		String retVal = $in.trim();

		retVal = StringUtils.remove($in, '(');
		retVal = StringUtils.remove(retVal, ')');
		retVal = StringUtils.replace(retVal, ".", " ");
		retVal = StringUtils.replace(retVal, "-", " ");
		retVal = StringUtils.replace(retVal, "+44", "0");
		retVal = StringUtils.remove(retVal, ' ');

		if (retVal.indexOf("44") == 0) {
			retVal = StringUtils.replaceFirst(retVal, "44", "0");
		}

		if (retVal.matches("^[\\d ]*$") && retVal.length() > 7) {

		} else {
			throw new UnrecognisedValueException();
		}

		if (retVal.charAt(0) != '0') {
			throw new UnrecognisedValueException();
		}
		
		if (allSameLetter(retVal)) {
			throw new UnrecognisedValueException();			
		}
		
		if (retVal.length() < 10) {
			throw new UnrecognisedValueException();			
		}

		return retVal.trim();
	}

	public boolean allSameLetter(String str) {
		char c1 = str.charAt(0);
		for (int i = 1; i < str.length(); i++) {
			char temp = str.charAt(i);
			if (c1 != temp) {
				// if chars does NOT match,
				// just return false from here itself,
				// there is no need to verify other chars
				return false;
			}
		}
		// As it did NOT return from above if (inside for)
		// it means, all chars matched, so return true
		return true;
	}

}
