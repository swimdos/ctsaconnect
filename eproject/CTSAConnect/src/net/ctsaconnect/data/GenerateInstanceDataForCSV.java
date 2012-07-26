package net.ctsaconnect.data;

import java.io.File;

import net.ctsaconnect.datasource.*;

/**
 * Extends GenerateInstanceData for DataSources from CSV
 * 
 * @author svwilliams
 *
 */
public class GenerateInstanceDataForCSV extends GenerateInstanceData {

	/**
	 * Constructor that takes in a CSV File Path
	 * 
	 * @param csvFilePath path to csv file
	 */
	public GenerateInstanceDataForCSV(String csvFilePath){
		CSVtoDataSource csvSource = new CSVtoDataSource(csvFilePath);
		csvSource.convert();
		this.ds = csvSource;
	}
	
	/**
	 * Generates An Owl file based on CSV File
	 * 
	 * @param args [0] csv file path 
	 */
	public static void main(String[] args){

		//Check for required file
		if (args[0].isEmpty()){
			System.out.println("GenerateInstanceDataForCSV requires a CSV File");
		} else if (!(new File(args[0])).isFile()){
			System.out.println("File Not Found");
		}
		
		//Create a new instance and run generate
		try {
			GenerateInstanceDataForCSV gidfor = new GenerateInstanceDataForCSV(args[0]);
			gidfor.generate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
