package name.davidappleton.bdu.transfer.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;


public class BDUFlowUpdate {

	File processFile = new File("C:\\Users\\david.appleton\\git\\data-transform\\src\\test\\resources\\test-1\\config\\Contract\\process-conf.xml");
	String pathToBin = "C:\\Program Files (x86)\\salesforce.com\\Data Loader\\bin\\";
	
	public static void main(String... args) throws IOException, InterruptedException {
		
		File origDir = new File("C:\\Users\\david.appleton\\git\\data-transform\\src\\test\\resources\\test-1\\output\\contractOriginal");
		File tempDir = new File("C:\\Users\\david.appleton\\git\\data-transform\\src\\test\\resources\\test-1\\output\\contractTemp");
		File origProcessDir = new File("C:\\Users\\david.appleton\\git\\data-transform\\src\\test\\resources\\test-1\\output\\contractOriginal\\process");
		File tempProcessDir = new File("C:\\Users\\david.appleton\\git\\data-transform\\src\\test\\resources\\test-1\\output\\contractTemp\\process");
		
		BDUFlowUpdate fu = new BDUFlowUpdate();
		File[] tempDirs = fu.generateDirs(tempDir, tempProcessDir).toArray(new File[] {});
		File[] originalDirs = fu.generateDirs(origDir, origProcessDir).toArray(new File[] {});
		
		for (int i=0; i<tempDirs.length; i++) {
			fu.executeCommand(tempDirs[i]);
			fu.executeCommand(originalDirs[i]);
			
			int index = 120;
			while (index > 0) {
				System.out.println("Waiting : Time Left --> " + index + " seconds. ");
				index--;
				Thread.sleep(1000);
			}
			
		}
		
		
	}
	
	public List<File> generateDirs(File sourceDir, File outDir) throws IOException {
		
		List<File> directoryList = new ArrayList<File>();
		
		FileUtils.deleteDirectory(outDir);
		FileUtils.forceMkdir(outDir);
		
		for (File f : sourceDir.listFiles()) {
			if (f.getName().indexOf(".csv") > -1) {
			
				String dirName = outDir.getAbsolutePath() + File.separator + f.getName().substring(0, f.getName().indexOf("."));
				File dir = new File(dirName);
				
				FileUtils.forceMkdir(dir);
				directoryList.add(dir);
				
				
				File dataFile = new File(dirName + File.separator + "data.csv");
				File processNewFile = new File(dirName + File.separator + "process-conf.xml");
	
				FileUtils.copyFile(f, dataFile);
				
				processFile.createNewFile();
				
				String processText = FileUtils.readFileToString(processFile, "UTF-8");
				processText = StringUtils.replace(processText, "[DATA-FILE]", dataFile.getAbsolutePath());
				processText = StringUtils.replace(processText, "[WORKING-DIRECTORY]", dir.getAbsolutePath());
				FileUtils.writeStringToFile(processNewFile, processText, "UTF-8");
				
				File successDir = new File(dirName + File.separator + "result" + File.separator + "success");
				File errorDir = new File(dirName + File.separator + "result" + File.separator + "error");
				File debugDir = new File(dirName + File.separator + "result" + File.separator + "debug");
				
				FileUtils.forceMkdir(successDir);
				FileUtils.forceMkdir(errorDir);
				FileUtils.forceMkdir(debugDir);
				
			}
		}
		
		return directoryList;
		
	}
	
	
	private String executeCommand(File runningDir) throws InterruptedException {
	
		String retVal = "OK";
		
		
		
		try {
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PumpStreamHandler ps = new PumpStreamHandler(os);
			DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
			
			CommandLine cmdLine = new CommandLine("cmd.exe");
			cmdLine.addArgument("/C");
			cmdLine.addArgument("CD");
			cmdLine.addArgument(pathToBin);
			cmdLine.addArgument("&&");
			cmdLine.addArgument("process.bat");
			cmdLine.addArgument(runningDir.getAbsolutePath());
			cmdLine.addArgument("update");
	
			for (String s : cmdLine.getArguments()) {
				System.out.println(s);
			}

			System.out.println("-----------------------");
			
			
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
			retVal = "ERROR : " + ioe;
		}
		
		return retVal;
	
	}
	
	
}
