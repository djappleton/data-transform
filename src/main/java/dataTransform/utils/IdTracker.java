package dataTransform.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class IdTracker {
	
	private static Logger log = LogManager.getLogger(IdTracker.class);

	private static Map<String, String> idPairMap = new HashMap<String, String>();
	
	public static String getValue(String $key) {
			
		return getValue($key, null);
	}
	
	public static String getValue(String $key, String $default) {
		
		log.trace("LOOKING UP " + $key + " --> MAP SIZE : " + idPairMap.size());
		
		String newId = idPairMap.get($key);
		
		log.trace("RETURNED " + newId);
		
		if ($default != null && StringUtils.isEmpty(newId)) {
			newId = $default;
		}
		
//		if (StringUtils.isEmpty(newId)) {
//			newId = ($default != null) ? $default : $key;
//		}
		
		return newId;
	}
	
	public static void setIdPair(String $key, String $value) {
		
		log.debug("SETTING KEY:" + $key + " --> VALUE:" + $value);
		
		idPairMap.put($key, $value);
	}
}
