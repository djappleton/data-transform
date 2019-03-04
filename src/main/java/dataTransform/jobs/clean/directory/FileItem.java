package dataTransform.jobs.clean.directory;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@XmlRootElement
public class FileItem {
	
	private static Logger log = LogManager.getLogger(FileItem.class);
	
	private String name = "";
	
	@XmlAttribute
	public String getName() {return name;}
	public void setNam(String $in) {
		name = $in;
		log.trace("setting name : " + name);
	}
}
