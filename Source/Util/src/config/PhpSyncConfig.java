package config;

/**
 * 
 * @author HUNG
 *
 */
public class PhpSyncConfig {

	/*
	 * Default files and folders
	 */	
	public static String CLIENT_SERVER_MAPPING			= "Mapping";
	public static String OUTPUT_FOLDER 					= DataModelConfig.WORKSPACE + "/" + CLIENT_SERVER_MAPPING + "/{PROJECT_NAME}/{RELATIVE_FILE_PATH}";	// @see config.PhpSyncConfig.getOutputFolder(String, String)
	
	public static String clientCodeHtmlFile 			= "client_code.html";
	public static String clientServerMappingXmlFile 	= "mapping.xml";
	
	/*
	 * XML identifiers
	 */
	public static String XML_MAPPING 		= "Mapping";
	public static String XML_OFFSET 		= "Offset";
	public static String XML_STRING_VALUE 	= "StringValue";
	
	/*
	 * Utility methods
	 */
	
	public static String getProjectFolder(String projectName) {
		return DataModelConfig.getProjectFolder(projectName);
	}
	
	public static String getOutputFolder(String projectName, String relativeFilePath) {
		return OUTPUT_FOLDER.replace("{PROJECT_NAME}", projectName).replace("{RELATIVE_FILE_PATH}", relativeFilePath);
	}
	
}
