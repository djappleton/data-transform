package dataTransform.jobs;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dataTransform.xml.Profile;
import dataTransform.xml.Profile.DirectoryRoot;

@XmlTransient
public abstract class FileHandlerJob extends Job {

	private static Logger log = LogManager.getLogger(FileHandlerJob.class);

	private String dirFromProfileRoot = "";
	private String inFileLocation = "";
	private String outFileLocation = "";
	private String inFileEncoding = "UTF-8";
	private String outFileEncoding = "UTF-8";

	@XmlAttribute(name = "dirFromRoot")
	public String getDirFromProfileRoot() {return dirFromProfileRoot;}
	public void setDirFromProfileRoot(String $in) {
		dirFromProfileRoot = $in;
		log.trace("setting dirFromProfileRoot : " + dirFromProfileRoot);
	}

	@XmlAttribute(name = "inFile")
	public String getInFile() {return inFileLocation;}
	public void setInFile(String $in) {
		inFileLocation = $in;
		log.trace("setting inFile : " + inFileLocation);
	}
	
	@XmlAttribute(name = "outFile")
	public String getOutFile() {return outFileLocation;}
	public void setOutFile(String $in) {
		outFileLocation = $in;
		log.trace("setting outFile : " + outFileLocation);
	}

	@XmlAttribute(name = "inFileEncoding")
	public String getInFileEncoding() {return inFileEncoding;}
	public void setInFileEncoding(String $in) {
		inFileEncoding = $in;
		log.trace("setting inFileEncoding : " + inFileEncoding);
	}

	@XmlAttribute(name = "outFileEncoding")
	public String getOutFileEncoding() {return outFileEncoding;}
	public void setOutFileEncoding(String $in) {
		outFileEncoding = $in;
		log.trace("setting outFileEncoding : " + outFileEncoding);
	}
	
	public String readFile(Profile $profile) throws Exception {
		
		File f = null;
		
		if ($profile.getRootDirectory() == DirectoryRoot.CLASSPATH) {
			
			ClassLoader classLoader = getClass().getClassLoader();
			f = new File(classLoader.getResource(this.getDirFromProfileRoot() + this.getInFile()).getFile());
			
		} else {
			f = new File(this.getDirFromProfileRoot() + this.getInFile());
			
		}
		
		log.trace("Reading File : " + f.getAbsolutePath());
		
		return FileUtils.readFileToString(f, this.getInFileEncoding());
	}
	
	public List<String> readFileLines() throws IOException {
		
		log.trace("Reading File Lines : " + this.getInFile());
		
		File f = new File(this.getInFile());
		return FileUtils.readLines(f, this.getInFileEncoding());
	}

	public void saveFile(Profile $profile, String $contents) throws Exception {
		
		
		File f = null;
		
		if ($profile.getRootDirectory() == DirectoryRoot.CLASSPATH) {
			
			ClassLoader classLoader = getClass().getClassLoader();
			f = new File(classLoader.getResource(this.getDirFromProfileRoot() + this.getOutFile()).getFile());
			
		} else {
			f = new File(this.getDirFromProfileRoot() + this.getOutFile());
			
		}
		
		log.trace("Saving File : " + f.getAbsolutePath());
		
		FileUtils.write(f, $contents, this.getOutFileEncoding());
	}
	
	public void saveFileLines(List<String> $contents) throws IOException {
		
		log.trace("Saving File Lines : " + this.getOutFile());
		
		File f = new File(this.getOutFile());
		FileUtils.writeLines(f, $contents, this.getOutFileEncoding());
	}
	
	public static void deleteFile(String $fileLocation) throws IOException {
		
		File f = new File($fileLocation);
		
		if (f.exists()) {
			f.delete();
		}
	}
}
