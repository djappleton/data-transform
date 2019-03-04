package dataTransform.jobs.clean.file;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dataTransform.jobs.CsvHandlerJob;
import dataTransform.xml.Profile;

@XmlRootElement
public class CleanFile extends CsvHandlerJob {

	private static Logger log = LogManager.getLogger(CleanFile.class);
	

	private static final List<Replacement> commonReplacementList = new ArrayList<Replacement>();
	private final List<Replacement> replacementList = new ArrayList<Replacement>();
	
	private static final List<Condition> commonRowRemovalList = new ArrayList<Condition>();
	private final List<Condition> rowRemovalList = new ArrayList<Condition>();;
	
	
	public static List<Replacement> getCommonReplacementList() {return commonReplacementList;}
	public static void setCommonReplacementList(List<Replacement> $in) {
		commonReplacementList.clear();
		commonReplacementList.addAll($in);
		log.trace("setting commonReplacementList : " + commonReplacementList);
	}
	
    @XmlElementWrapper(name="replace")
    @XmlElement(name="replacement")
	public List<Replacement> getReplacementList() {return replacementList;}
	public void setReplacementList(List<Replacement> $in) {
		replacementList.clear();
		replacementList.addAll($in);
		log.trace("setting replacementList : " + replacementList);
	}
	
	public static List<Condition> getCommonRowRemovalList() {return commonRowRemovalList;}
	public static void setCommonRowRemovalList(List<Condition> $in) {
		commonRowRemovalList.clear();
		commonRowRemovalList.addAll($in);
		log.trace("setting rowRemovalList : " + commonRowRemovalList);
	}
	
    @XmlElementWrapper(name="removeRow")
    @XmlElement(name="condition")
	public List<Condition> getRowRemovalList() {return rowRemovalList;}
	public void setRowRemovalList(List<Condition> $in) {
		rowRemovalList.clear();
		rowRemovalList.addAll($in);
		log.trace("setting rowRemovalList : " + rowRemovalList);
	}
	
	@Override
	public String initialise() throws Exception {
		
		CleanFile.getCommonReplacementList().addAll(this.getReplacementList());
		this.getReplacementList().clear();
		
		CleanFile.getCommonRowRemovalList().addAll(this.getRowRemovalList());
		this.getRowRemovalList().clear();
		
		return "OK";
	}
	
	@Override
	public String execute(Profile $profile) throws Exception {

		String result = "OK";
		
		String fileContents = this.readFile($profile);
		
		fileContents = executeReplacements(fileContents, CleanFile.getCommonReplacementList());
		fileContents = executeReplacements(fileContents, this.getReplacementList());

		fileContents = removeRows(fileContents, CleanFile.getCommonRowRemovalList());
		fileContents = removeRows(fileContents, this.getRowRemovalList());
		
		this.saveFile($profile, fileContents);
			
		return result;
		
	}
	
	
	/**
	 * 
	 * @param $fileContents
	 * @param $replacementList
	 */
	private String executeReplacements(String $fileContents, List<Replacement> $replacementList) {
		
		String result = $fileContents;
		
		for (Replacement replacement : $replacementList) {
			result = replacement.replace(result);
		}
		
		return result;
		
	}
	
	
	/**
	 * 
	 * @param $fileContents
	 * @param $conditionList
	 */
	private String removeRows(String $fileContents, List<Condition> $conditionList) {

		String[] lines = $fileContents.split("\\r?\\n");

		StringBuffer sb = new StringBuffer();
		
		for (String line : lines) {
			
		
			boolean removeLine = false;
			
        	for (Condition condition : $conditionList) {
        		
        		removeLine = condition.isConditionMet(line);
        		
        		if (condition.isConditionMet(line)) {
        			removeLine = true;
        			break;
        		}
        		
        	}
        	
        	if (!removeLine) {
    			if (sb.length() > 0) {
    				sb.append("\r\n");
    			}
    			sb.append(line);
    			
    		}
		
		}
		
		return sb.toString();
		
	}
}
