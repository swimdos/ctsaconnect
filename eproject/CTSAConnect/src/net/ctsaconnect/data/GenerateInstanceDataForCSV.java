package net.ctsaconnect.data;

import net.ctsaconnect.datasource.*;

public class GenerateInstanceDataForCSV extends GenerateInstanceData {

	
	public GenerateInstanceDataForCSV(String csvFilePath){
		CSVtoDataSource csvSource = new CSVtoDataSource(csvFilePath);
		csvSource.convert();
		this.ds = csvSource;
	}
	
	public void execute(){
		
	}
	
	public static void main(String[] args){
		try {
			GenerateInstanceDataForCSV gidfor = new GenerateInstanceDataForCSV(args[0]);
			gidfor.generate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
