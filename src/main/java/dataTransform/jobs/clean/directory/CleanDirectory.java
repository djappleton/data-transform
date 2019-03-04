package dataTransform.jobs.clean.directory;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dataTransform.jobs.FileHandlerJob;
import dataTransform.xml.Profile;

/**
 * <p>Job used to clean a directory of files and child directories</p>
 * 
 * @author david.appleton
 *
 */
@XmlRootElement
public class CleanDirectory extends FileHandlerJob {

	private static Logger log = LogManager.getLogger(CleanDirectory.class);

	public enum FileAction {DELETE, KEEP};
	
	private FileAction identifiedFileAction = FileAction.DELETE;
	
	private static List<FileItem> commonFileList = new ArrayList<FileItem>();
	private List<FileItem> fileList = new ArrayList<FileItem>();
	
	
	@XmlAttribute(name = "identifiedFileAction")
	public FileAction getIdentifiedFileAction() {return identifiedFileAction;}
	public void setIdentifiedFileAction(FileAction $in) {
		identifiedFileAction = $in;
		log.trace("setting identifiedFileAction : " + identifiedFileAction);
	}
	
	public static List<FileItem> getCommonFileList() {return commonFileList;}
	public static void setCommonFileListList(List<FileItem> $in) {
		commonFileList.clear();
		commonFileList.addAll($in);
		log.trace("setting commonFileList : " + commonFileList);
	}
	
    @XmlElementWrapper(name="files")
    @XmlElement(name="item")
	public List<FileItem> getFileList() {return fileList;}
	public void setFileList(List<FileItem> $in) {
		fileList.clear();
		fileList.addAll($in);
		log.trace("setting fileList : " + fileList);
	}

	@Override
	public String execute(Profile $profile) throws Exception {

		log.debug("Cleaning : " + this.getDirFromProfileRoot());
		
		try {
			
			File root = new File(this.getDirFromProfileRoot());
				
			if (root.isDirectory()) {
				
				MyFileFilter ff = new MyFileFilter();
				List<String> fileNameList = new ArrayList<String>(); 
				for (FileItem fi : this.getFileList()) {
					fileNameList.add(fi.getName());
				}
				ff.setFilenamesList(fileNameList);
				
				for (File file : root.listFiles(ff)) {
				
					deleteFile(file.getAbsolutePath());
		
				}
			
				
				
			}
		} catch (Exception e) {
			log.debug("COULD NOT FIND : " + this.getDirFromProfileRoot());
			
		}
			
		return "OK";
	}
//
//	void delete(File f, List<String> $keepList) throws IOException {
//		if (f.isDirectory()) {
//			for (File c : f.listFiles()) {
//				delete(c, $keepList);
//			}
//		} 
//		
//		if (f.isDirectory() && f.listFiles().length == 0) {
//			log.debug("DELETING : " + f.getAbsolutePath());
//			f.delete();
//		} else {
//			if (!$keepList.contains(f.getName())) {
//				log.debug("DELETING : " + f.getAbsolutePath());
//				f.delete();
//			}
//		}
//		
//	}

	@Override
	public String initialise() throws Exception {

		CleanDirectory.getCommonFileList().addAll(this.getFileList());
		this.getFileList().clear();
		
		return "OK";
		
	}

}

class MyFileFilter implements FileFilter {
	
	private final List<String> filenames = new ArrayList<String>();
	private boolean inverseFilter = true;
	
	public boolean getInverseFilter() {return inverseFilter;}
	public void setInverseFilter(boolean $in) {
		inverseFilter = $in;
	}
	
	public void setFilenamesList(List<String> $in) {
		filenames.clear();
		filenames.addAll($in);
	}
	
	public boolean accept(File file) {
		
		for (String fName : filenames) {
			if (file.getName().equals(fName)) {
				return (getInverseFilter()) ? false : true;
			}
		}
		return (getInverseFilter()) ? true : false;
	}
}