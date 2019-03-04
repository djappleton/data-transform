package dataTransform.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import dataTransform.jobs.Job;
import dataTransform.jobs.clean.directory.CleanDirectory;
import dataTransform.jobs.clean.file.CleanFile;
import dataTransform.jobs.convertcsv.ConvertCSV;
import dataTransform.jobs.salesforce.dataloader.DataLoader;
import dataTransform.jobs.trackids.TrackIds;
import dataTransform.jobs.wait.Wait;

/**
 * 
 * @author david.appleton
 *
 */
@XmlRootElement
public class Profile {
	
	private static Logger log = LogManager.getLogger(Profile.class);

	public enum DirectoryRoot {CLASSPATH, ROOT}
	
	private DirectoryRoot rootDirectory = DirectoryRoot.ROOT;
	private List<Job> jobList = new ArrayList<Job>();
	private List<Job> initList = new ArrayList<Job>();
	
	@XmlAttribute(name = "dirRootType")
	public DirectoryRoot getRootDirectory() {return rootDirectory;}
	public void setRootDirectory(DirectoryRoot $in) {
		rootDirectory = $in;
		log.trace("setting rootDirectory : " + rootDirectory);
	}	
	
	@XmlElements({ 
	    @XmlElement(name="cleanDirectory", type=CleanDirectory.class),
	    @XmlElement(name="cleanFile", type=CleanFile.class),
	    @XmlElement(name="convertCSV", type=ConvertCSV.class),
	    @XmlElement(name="dataloader", type=DataLoader.class),
	    @XmlElement(name="trackIds", type=TrackIds.class),
	    @XmlElement(name="wait", type=Wait.class)
	})
//	@XmlAnyElement
	@XmlElementWrapper
	public List<Job> getJobs() {
		return jobList;
	}

	public void setJobs(List<Job> $in) {
		jobList = $in;
		log.trace("setting jobs : " + jobList);
	}
	
	@XmlElements({ 
	    @XmlElement(name="cleanDirectory", type=CleanDirectory.class),
	    @XmlElement(name="cleanFile", type=CleanFile.class),
	    @XmlElement(name="convertCSV", type=ConvertCSV.class),
	    @XmlElement(name="dataloader", type=DataLoader.class),
	    @XmlElement(name="trackIds", type=TrackIds.class),
	    @XmlElement(name="wait", type=Wait.class)
	})
//	@XmlAnyElement
	@XmlElementWrapper
	public List<Job> getInit() {
		return initList;
	}

	public void setInit(List<Job> $in) {
		initList = $in;
		log.trace("setting jobs : " + initList);
	}
	
	public Profile() {}
	
	public Profile(String $configFileLocation) throws JAXBException, SAXException, ParserConfigurationException {
		new Profile(new File($configFileLocation));
	}
	
	public Profile(File $configFile) throws JAXBException, SAXException, ParserConfigurationException{
			
			SAXParserFactory spf = SAXParserFactory.newInstance();
	        spf.setXIncludeAware(true);
	        spf.setNamespaceAware(true);
	        XMLReader xr = spf.newSAXParser().getXMLReader();
	        
	        log.trace("Profile being read : " + $configFile.getAbsolutePath());
	        
	        SAXSource src = new SAXSource(xr, new InputSource($configFile.getAbsolutePath()));
	        
	        Profile profile = JAXB.unmarshal(src, Profile.class);

	        this.setInit(profile.getInit());
	        this.setJobs(profile.getJobs());
	}
	
	public void execute() throws Exception {
		
        log.info("RUNNING execute() : " + this.getInit().size() + " : " + this.getJobs().size());
        
		for (Job job : this.getInit()) {

			log.debug("INITIALISING JOB : " + job.getClass().getCanonicalName());
			
			String result = job.initialise();
			
			if (!result.equals("OK")) {
					log.error("ERROR : Returned result : " + result );
				break;
			}
			
			log.debug("INITIALISED");
		}
		
		for (Job job : this.getJobs()) {

			log.debug("RUNNING JOB : " + job.getClass().getCanonicalName());
			
			String result = job.execute(this);
			
			if (!result.equals("OK")) {
				log.error("ERROR : Returned result : " + result );
				break;
			}

			log.debug("COMPLETED JOB");
		}

        log.info("COMPLETED execute().");
		
	}
	
}
