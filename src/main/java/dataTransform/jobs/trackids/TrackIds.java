package dataTransform.jobs.trackids;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dataTransform.jobs.Job;
import dataTransform.utils.IdTracker;
import dataTransform.xml.Profile;


/**
 * <p>Job that allows the tracking and matching of IDs</p>
 * 
 * <p>When transferring from one system to another database ID's will change.
 * These will need to be tracked and substituted in order to maintain the 
 * relationships between data</p>
 * 
 * <p>This Job can be called a number of times and will continue to track IDs 
 * beyond each call.</p>
 * 
 * 
 * @author david.appleton
 *
 */
@XmlRootElement
public class TrackIds extends Job {

	private static Logger log = LogManager.getLogger(TrackIds.class);

	String inDir = null;
	String keyColumnName = null;
	String valueColumnName = null;
	
	private static List<Item> commonItemList = new ArrayList<Item>();
	private List<Item> itemList = new ArrayList<Item>();
	
	/**
	 * <p>The directory </p>
	 * 
	 * @return
	 */
	@XmlAttribute
	public String getInDir() {return inDir;}
	public void setInDir(String $in) {
		inDir = $in;
		log.trace("setting inDir : " + inDir);
	}

	@XmlAttribute
	public String getKeyColumnName() {return keyColumnName;}
	public void setKeyColumnName(String $in) {
		keyColumnName = $in;
		log.trace("setting keyColumnName : " + keyColumnName);
	}

	@XmlAttribute
	public String getValueColumnName() {return valueColumnName;}
	public void setValueColumnName(String $in) {
		valueColumnName = $in;
		log.trace("setting valueColumnName : " + valueColumnName);
	}
	
	@XmlElement(name = "item")
	public List<Item> getItemList() {return itemList;}
	public void setItemList(List<Item> $in) {
		itemList = $in;
		log.trace("setting itemList : " + itemList);
	}
	
	@Override
	public String execute(Profile $profile) throws Exception {
		log.trace("RUNNING execute();");
		
		
		if (StringUtils.isNoneEmpty(this.getInDir())) {
			File dir = new File(this.getInDir());
			
			if (dir.isDirectory()) {
				readFromFile(dir);
			}
		}
		
		if (this.getItemList().size() > 0) {
			readFromItemList(); 
		}
		
		log.trace("COMPLETED execute();");
		
		return "OK";
	}
	
	private void readFromItemList() {
		for (Item item : this.getItemList()) {
			IdTracker.setIdPair(item.getKey(), item.getValue());
		}
	}
	
	private void readFromFile(File $dataDir) throws IOException {
		
		log.trace("RUNNING readFromFile()");
		
		if ($dataDir.isDirectory()) {

			for (File file : $dataDir.listFiles()) { 
			
				if (file.getName().indexOf("data") > -1) {
		
					Reader csvReader = new FileReader(file);
					
			        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(csvReader);
			        
			        for (CSVRecord record : records) {
			    		String key = record.get(this.getKeyColumnName());
			    		String val = record.get(this.getValueColumnName());
			    		
			    		IdTracker.setIdPair(key, val);
			        }
		
				}
			
			}
		
		}
		log.trace("COMPLETED readFromFile();");
		
        
	}
	@Override
	public String initialise() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
