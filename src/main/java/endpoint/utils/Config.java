package endpoint.utils;

import java.util.MissingResourceException;
//import java.util.ResourceBundle;
//import java.io.File;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;


/**
 * 
 * @author danielaahumada
 * System configuration vars
 */

public final class Config {
	
	
//	private static final String BUNDLE_NAME = "endpoint.utils.config"; //$NON-NLS-1$
/*
	private static final String BUNDLE_NAME = "config";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Config() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
*/

	 private static Properties prop;
        private static String filePath;

        static public void init() throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
                prop = new Properties();
                filePath = (new File(".")).getCanonicalFile().getCanonicalFile().getCanonicalPath() + "/config.xml";
                System.out.println(filePath);
                prop.loadFromXML(new FileInputStream(filePath));
		initVars();
        }

        public static String getString(String parameter) {
                return prop.getProperty(parameter);
        }
	
	public static String fusekiServerURL;
	public static String fusekiServerDatasetName;
	public static String policiesStoragePath;
	public static String baseURI;
	public static String contextBaseURI;
	public static String s4acPrefixURI;
	public static String rdfPrefixURI;

	public static String fusekiQueryURL;
	public static String fusekiDataURL;
	public static String fusekiUploadURL;
	public static String fusekiUpdateURL;	
	
	public static void initVars(){
		fusekiServerURL = getString("FUSEKI_SERVER_URL");
		fusekiServerDatasetName = getString("FUSEKI_DATASET_NAME");
		policiesStoragePath = getString("POLICIES_STORAGE_PATH");
		baseURI = getString("BASE_URI");
		contextBaseURI = getString("CONTEXT_BASE_URI");
		s4acPrefixURI = "http://ns.inria.fr/s4ac/v2#";
		rdfPrefixURI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
		fusekiQueryURL = fusekiServerURL + "/" + fusekiServerDatasetName + "/query";
		fusekiDataURL = fusekiServerURL + "/" + fusekiServerDatasetName + "/data";
		fusekiUploadURL = fusekiServerURL + "/" + fusekiServerDatasetName + "/upload";
		fusekiUpdateURL = fusekiServerURL + "/" + fusekiServerDatasetName + "/update";	
	}
}
