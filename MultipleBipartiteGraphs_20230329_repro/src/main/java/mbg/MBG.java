package mbg;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class MBG {
	
	private static Logger logger = Logger.getLogger(MBG.class);

	public final static String CONFIGURATION_FILE = "properties/MBG.properties";
	public final static boolean VERBOSE_MODE = true;
	public final static boolean FACT_COUNTING = true; // relation otherwise
	public final static boolean EXPONENT_PARAMETRIZING = true; // linear attachment otherwise
	public final static float SAMPLE = 1f; // 0.5 -> generation of 50% of facts 
	
	public static String getProperty(String key) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(CONFIGURATION_FILE));
		} catch (Exception e) {
			logger.error(e, e);
		}
		return properties.getProperty(key);
	}
}
