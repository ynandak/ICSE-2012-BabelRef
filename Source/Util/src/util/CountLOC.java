package util;

import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 */
public class CountLOC {
	
	public static String projectName	= "";
	public static String projectFolder	= "C:\\Users\\HUNG\\Desktop\\Lab\\Web Projects\\workspace\\Server Code\\" + projectName;

	public static void main(String[] args) {
		ArrayList<String> allFiles = FileIO.getAllFilesInFolderByExtensions(projectFolder, new String[]{".php", ".html", ".js"});
		int count = 0;
		
		for (String file : allFiles) {
			count += getLOC(file);
		}
		
		System.out.println("Project folder:  " + projectFolder);
		System.out.println("Number of files: " + allFiles.size());
		System.out.println("Total LOC:       " + count);

	}
	
	public static int getLOC(String file) {
		String fileContent = FileIO.readStringFromFile(file);
		int count = 1;
		for (int i = 0; i < fileContent.length(); i++)
			if (fileContent.charAt(i) == '\n')
				count++;
		return count;
	}

}
