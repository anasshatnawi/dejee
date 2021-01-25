package realeity.technique.storage.files;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Save in a file the information about a layered partition
 * @author Alvine Boaye Belle
 *
 */

public class FileProcessment {
	private static String separator = ", ";
	
	/**
	 * Constructor of the class ResultFile. 
	 * Its visibility is private in order not to allow the other classes to create many 
	 * instances of ResultFile
	 */
	private FileProcessment(){
		
	}
	
	/**
	 * unique instance of ResultFile.
	 */
	private static FileProcessment uniqueInstance;
	
	/**
	 *Method insuring that a unique instance of ResultFile is created.
	 * @return uniqueInstance
	 */
	public static FileProcessment getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new FileProcessment();
		}
		return uniqueInstance;
	}

	
	/**
	 * Allows to create a directory from a file's path.
	 * @param directoryName
	 */
	public void createDirectory(String directoryName){
		File directory = new File(directoryName);
		directory.mkdirs();
	}
	
	/**
	 * 
	 * @param fileContent
	 * @param fileName
	 * @param parentDirectory
	 */
	public void storeInFile(String fileContent, String fileName, 
			                String parentDirectory){
		//create the parent directory that will contain the file
		createDirectory(parentDirectory);
				
		//create the file
		BufferedWriter output = null; 
		try{
			File graphFile = new File(fileName);
			if (graphFile.exists())
				graphFile.delete();
			  output = new BufferedWriter(new FileWriter(graphFile));
			  output.write(fileContent);//writes in the file the dot code corresponding to the current partition 
			  output.close();
			  System.out.println("The data have been saved under the path : " 
			                     + fileName + ".");
			
		 }//end of try
		 catch( FileNotFoundException en){
			 en.printStackTrace();
			 System.out.println("The file to store the results has not been found.");
	     }
		 catch( IOException en){
			 System.out.println("an input/output error has occurred. The file can not " +
			 		            " be processed.");
	     }
	}
	
	
	
	
	/**
	 * 
	 * @param irrelevantNamesFile path to the file containing the names of the packages that should be ignored. 
	 *  It contains the whole names of the packages (with the "." and the ".*") that are irrelevant for the project.
	 * @return
	 */
	public static List<String> processIrrelevantPackagesFile(String irrelevantNamesFile){
		
		//read the content of file
		List<String>  packageNames = new  ArrayList<String>();
		try{
			FileInputStream input = new FileInputStream(irrelevantNamesFile); 
			InputStreamReader inputStreamReader = new InputStreamReader(input);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while ((line= bufferedReader.readLine())!= null){
				packageNames.add(line);
				
			}
			bufferedReader.close(); 
		}		
		catch (Exception e){
			System.out.println("The file can not be processed : an input/output error has" +
					           " occurred.");
		}

		return packageNames;
	}
	
	/**
	 * Read a file.
	 * @param fileName
	 * @return
	 */
	public static String readFile(String fileName){
		String fileContent = "";
		try{
			FileInputStream input= new FileInputStream(fileName); 
			InputStreamReader inputStreamReader = new InputStreamReader(input);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while ((line= bufferedReader.readLine())!= null){
				fileContent += line + "\r\n";
				
			}
			bufferedReader.close(); 
		}		
		catch (Exception e){
			System.out.println("The file can not be processed : an input/output error" +
					           " has occurred.");
		}
        return fileContent;
	}
}