package dataTransform;

import java.io.File;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import dataTransform.utils.IdTracker;
import dataTransform.xml.Profile;

public class DataTransform {

	public static IdTracker idTracker = new IdTracker();
	
	public static Options clOptions = new Options();
	static {
		clOptions.addOption("pdir", true, "The working directory that contains the profile XML file and will hold and derived filed.");
		clOptions.addOption("pfn", true, "filename of the profile xml file");
	}
	
	public static void main(String[] args) throws Exception {
		
		// create the parser
	    CommandLineParser parser = new DefaultParser();
        File workingDirectory = null;
        String profileFilename = "";
        File profileFile = null;
	    
	    try {
	        // parse the command line arguments
	        CommandLine line = parser.parse( clOptions, args );
	        
	        
	        if (line.hasOption("pdir")) {
	        	
	        	System.out.println("pdir : " + line.getOptionValue("pdir"));
	        	
	        	workingDirectory = new File(line.getOptionValue("pdir"));
	        	if (!workingDirectory.isDirectory()) {
	        		throw new ParseException("Invalid working directory.");
	        	}
	        }
	        
	        if (line.hasOption("pfn")) {
	        	
	        	System.out.println("pfn : " + line.getOptionValue("pfn"));
	        	
	        	profileFilename = line.getOptionValue("pfn");
	        	profileFile = new File(workingDirectory.getAbsolutePath() + File.separatorChar + profileFilename);
				
	        	if (!profileFile.isFile()) {
	        		throw new ParseException("File not found : " + profileFile.getName());
	        	}
	        }
	        
	    } catch( ParseException exp ) {
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
	    }
		
		try {
			
			SAXParserFactory spf = SAXParserFactory.newInstance();
	        spf.setXIncludeAware(true);
	        spf.setNamespaceAware(true);
	        XMLReader xr = spf.newSAXParser().getXMLReader();
	        
	        System.out.println(profileFile.getAbsolutePath());
	        
	        SAXSource src = new SAXSource(xr, new InputSource(profileFile.getAbsolutePath()));
	        
	        Profile profile = JAXB.unmarshal(src, Profile.class);

	        profile.execute();
				
		} catch(JAXBException e) {
			e.printStackTrace();
			
		}

	}

}
