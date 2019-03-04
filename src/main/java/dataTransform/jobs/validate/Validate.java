package dataTransform.jobs.validate;

import java.io.File;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dataTransform.jobs.CsvHandlerJob;
import dataTransform.xml.Profile;

@XmlRootElement
public class Validate extends CsvHandlerJob {

	private static Logger log = LogManager.getLogger(Validate.class);
	

	
//	@XmlTransient
//	private static List<Validate> commonDataValidationList = null;
	
//	@XmlAttribute(name = "validation")
//	private List<Validate> dataValidationList = null;


	/*	
	public static List<Validate> getCommonDataValidationList() {return commonDataValidationList;}
	public static void setCommonDataValidationList(List<Validate> $in) {
		commonDataValidationList = $in;
		log.trace("setting commonDataValidationList : " + commonDataValidationList);
	}
	

	public List<Validate> getDataValidationList() {return dataValidationList;}
	public void setDataValidationList(List<Validate> $in) {
		dataValidationList = $in;
		log.trace("setting dataValidationList : " + dataValidationList);
	}
*/	
	
	
	@Override
	public String execute(Profile $profile) throws Exception {

		String result = "";
		
		File inFile = new File(this.getInFile());
		File outFile = new File(this.getOutFile());
		
		String fileContents = FileUtils.readFileToString(inFile, "UTF-8");
		

//		validateData(fileContents, Validate.getCommonDataValidationList());
//		validateData(fileContents, this.getDataValidationList());
		
		FileUtils.writeStringToFile(outFile, fileContents, this.getOutFileEncoding());
			
		return result;
		
	}

	
	/**
	 * 
	 * @param $fileContents
	 * @param $validationList
	 */
//	private void validateData(String $fileContents, List<Validate> $validationList) {

		
		
//	}
	
	

	@Override
	public String initialise() throws Exception {
		
//		Validate.getCommonDataValidationList().addAll(this.getDataValidationList());
//		this.getDataValidationList().clear();
		
		return null;
	}
}
