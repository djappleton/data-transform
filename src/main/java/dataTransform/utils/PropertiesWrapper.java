package dataTransform.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 
 * @author david.appleton
 *
 */
public class PropertiesWrapper {
	
	private PropertiesWrapper() {}
	private static Properties instance = null;
	
	private static Logger log = LogManager.getLogger(Properties.class);
	
	public static Properties getInstance() {
		if (PropertiesWrapper.instance == null) {
			synchronized(Properties.class) {
				if (PropertiesWrapper.instance == null) {
					InputStream inputStream = PropertiesWrapper.class.getClassLoader().getResourceAsStream("config.properties");
					 
					if (inputStream != null) {
						try {
							PropertiesWrapper.instance = new Properties();
							PropertiesWrapper.instance.load(inputStream);
							log.debug("property file loaded");
							log.debug(PropertiesWrapper.instance.toString());
						} catch (IOException e) {
							log.error("property file 'config.properties' not found in the classpath");
							
						}
				
					}
			
				}
		
			}
		
		}
		
		return PropertiesWrapper.instance;
	}
}
