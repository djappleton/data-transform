package dataTransform.jobs.salesforce.dataloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dataTransform.jobs.Job;
import dataTransform.utils.PropertiesWrapper;
import dataTransform.xml.Profile;

@XmlRootElement
public class DataLoader extends Job {

	private static Logger log = LogManager.getLogger(DataLoader.class);
	private static Properties props = PropertiesWrapper.getInstance();

	private String sfdcEndpoint = null;
	private String sfdcUsername = null;
	private String sfdcPassword = null;
	private String processEncryptionKeyFile = null;
	private int sfdcTimeoutSecs = 600;
	private int sfdcLoadBatchSize = 200;
	private int sfdcExtractionRequestSize = 500; 
	
	
	
	private String configFileDir = null;
	private String beanName = null;
	private String dataDirectory = null;
	private String extractFileName = null;
	


	@XmlAttribute
	public String getSfdcEndpoint() {return sfdcEndpoint;}
	public void setSfdcEndpoint(String $in) {
		sfdcEndpoint = $in;
		log.trace("setting sfdcEndpoint : " + sfdcEndpoint);
	}

	@XmlAttribute
	public String getSfdcUsername() {return sfdcUsername;}
	public void setSfdcUsername(String $in) {
		sfdcUsername = $in;
		log.trace("setting sfdcUsername : " + sfdcUsername);
	}

	@XmlAttribute
	public String getSfdcPassword() {return sfdcPassword;}
	public void setSfdcPassword(String $in) {
		sfdcPassword = $in;
		log.trace("setting sfdcPassword : " + sfdcPassword);
	}

	@XmlAttribute
	public String getProcessEncryptionKeyFile() {return processEncryptionKeyFile;}
	public void setProcessEncryptionKeyFile(String $in) {
		processEncryptionKeyFile = $in;
		log.trace("setting confprocessEncryptionKeyFileigFileDir : " + processEncryptionKeyFile);
	}
	
	@XmlAttribute
	public String getConfigFileDir() {return configFileDir;}
	public void setConfigFileDir(String $in) {
		configFileDir = $in;
		log.trace("setting configFileDir : " + configFileDir);
	}

	@XmlAttribute
	public String getDataDir() {return dataDirectory;}
	public void setDataDir(String $in) {
		dataDirectory = $in;
		log.trace("setting dataDirectory : " + dataDirectory);
	}

	@XmlAttribute
	public String getExtractFileName() {
		String retVal = "data.csv";
		if (StringUtils.isNoneEmpty(extractFileName)) {
			retVal = extractFileName;
		}
		return retVal;
	}
	public void setExtractFileName(String $in) {
		extractFileName = $in;
		log.trace("setting extractFileName : " + extractFileName);
	}

	@XmlAttribute
	public String getBeanName() {return beanName;}
	public void setBeanName(String $in) {
		beanName = $in;
		log.trace("setting beanName : " + beanName);
	}

	@Override
	public String execute(Profile $profile) throws Exception {

		log.debug("RUNNING : execute()");

		String result = "";
		
		File dataDir = new File(this.getDataDir());
		FileUtils.forceMkdir(dataDir);
		
		if (dataDir.isDirectory()) {
			File[] files = dataDir.listFiles();
			
			if (files.length > 0) {
				result = processFiles(files);
				
			} else {
				File file = new File(this.getDataDir() + this.getExtractFileName());
				if (file.getName().indexOf("data") > -1) {
					prepareConfigFile(file);
					result = executeCommand();
				}
			}
			
		} else {
			result = "Directory not found (" + dataDir.getPath() + ")";
			
		}
			
		log.debug("COMPLETED : execute()");

		return result;
	}
	
	private String processFiles(File[] $files) throws IOException {
		
		log.debug("RUNNING : processFiles(File[] $files)");
		
		String result = "";
		
		for (File file : $files) {
			
			if (!file.isDirectory() && file.getName().indexOf("data") == 0) {
				prepareConfigFile(file);
	
				result = executeCommand();
				if (!result.equals("OK")) {
					break;
				}
			}
			
		}
		
		log.debug("COMPLETED : processFiles(File[] $files)");
		
		return result;
	}
	
	private String executeCommand() {
	
		log.debug("RUNNING : executeCommand() : " + this.getConfigFileDir() + " --> " + this.getBeanName());
		
		String retVal = "OK";
		
		try {
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PumpStreamHandler ps = new PumpStreamHandler(os);
			DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
	
			Properties propertiesWrapper = PropertiesWrapper.getInstance();
			String pathToBin = propertiesWrapper.getProperty("salesforce.dataloader.pathToBin");
			
			CommandLine cmdLine = new CommandLine("cmd.exe");
			cmdLine.addArgument("/C");
			cmdLine.addArgument("CD");
			cmdLine.addArgument(pathToBin);
			cmdLine.addArgument("&&");
			cmdLine.addArgument("process.bat");
			cmdLine.addArgument(this.getConfigFileDir());
			cmdLine.addArgument(this.getBeanName());
	
			ExecuteWatchdog watchdog = new ExecuteWatchdog(1000000);
			Executor executor = new DefaultExecutor();
			executor.setWatchdog(watchdog);
			executor.setStreamHandler(ps);
			executor.execute(cmdLine, resultHandler);
	
			while (!resultHandler.hasResult()) {
				try {
	
					resultHandler.waitFor();
				} catch (InterruptedException e) {
	
				}
			}
	
		} catch (IOException ioe) {
			log.error("IO EXCEPTION : " + ioe);
			retVal = "ERROR : " + ioe;
		}
		
		log.debug("COMPLETED : executeCommand(");
		
		return retVal;
	
	}
	
	private void prepareConfigFile(File $datafile) throws IOException {
		
		log.debug("Preparing File : " + $datafile.getAbsolutePath());
		
		File templateConfigFile = new File(this.getConfigFileDir() + "conf/process-conf-template.xml");
				
		String contents = FileUtils.readFileToString(templateConfigFile, "UTF-8");

		contents = StringUtils.replace(contents, "[proxyHost]", (String)props.get("sfdc.proxyHost"));
		contents = StringUtils.replace(contents, "[proxyPassword]", (String)props.get("sfdc.proxyPassword"));
		contents = StringUtils.replace(contents, "[proxyPort]", (String)props.get("sfdc.proxyPort"));
		contents = StringUtils.replace(contents, "[proxyUsername]", (String)props.get("sfdc.proxyUsername"));

		contents = StringUtils.replace(contents, "[sfdl.endpoint]", (String)props.get("salesforce.dataloader.endpoint"));
		contents = StringUtils.replace(contents, "[sfdl.username]", (String)props.get("salesforce.dataloader.username"));
		contents = StringUtils.replace(contents, "[sfdl.password]", (String)props.get("salesforce.dataloader.password"));
		contents = StringUtils.replace(contents, "[sfdl.encryptionKeyFile]", (String)props.get("salesforce.dataloader.encryptionKeyFile"));

		contents = StringUtils.replace(contents, "[data.filename]", $datafile.getAbsolutePath());
		contents = StringUtils.replace(contents, "[data.configDir]", this.getConfigFileDir());
		contents = StringUtils.replace(contents, "[log.success.filename]", $datafile.getParent() + "\\result\\success\\" + $datafile.getName());
		contents = StringUtils.replace(contents, "[log.error.filename]", $datafile.getParent() + "\\result\\error\\" + $datafile.getName());
		contents = StringUtils.replace(contents, "[log.debug.filename]", $datafile.getParent() + "\\result\\debug\\" + $datafile.getName());

		FileUtils.forceMkdir(new File($datafile.getParent() + "\\result\\success\\"));
		FileUtils.forceMkdir(new File($datafile.getParent() + "\\result\\error\\"));
		FileUtils.forceMkdir(new File($datafile.getParent() + "\\result\\debug\\"));
		
		File outFile = new File(this.getConfigFileDir() + "process-conf.xml");
		
		FileUtils.writeStringToFile(outFile, contents, "UTF-8");
		
	}
	@Override
	public String initialise() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
