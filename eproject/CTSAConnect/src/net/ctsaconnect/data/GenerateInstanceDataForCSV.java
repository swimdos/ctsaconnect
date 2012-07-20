package net.ctsaconnect.data;

import net.ctsaconnect.datasource.*;

public class GenerateInstanceDataForCSV extends GenerateInstanceData {

	private CSVtoDataSource csvSource;
	
	public GenerateInstanceDataForCSV(String csvFilePath){
		csvSource = new CSVtoDataSource(csvFilePath);
	}
	
	public void execute(){
		
	}
}
