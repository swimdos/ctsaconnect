/**
 * 
 */
package net.ctsaconnect.datasource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.csvreader.CsvReader;

/**
 * @author drspeedo
 *
 */
public class CSVtoDataSource extends DataSourceSimple{

	
	/**
	 * 
	 */
	private CsvReader fileToConvert;
	
	public CSVtoDataSource() {
		this.fileToConvert = null;
	}
	
	public CSVtoDataSource(String csvFileLocation) {
		try {
			this.fileToConvert = new CsvReader(csvFileLocation);
		} catch (FileNotFoundException e) {
			System.out.println("The File you have specified: " + csvFileLocation + " was not found");
			e.printStackTrace();
		}
	}
	
	public void convert(){
		convert(this.fileToConvert);
	}

	
	public void convert(CsvReader incomingFile){
  	
  	try {
  		incomingFile.readHeaders();
  		
  		//TODO: svwilliams, add a header check to check the incoming file against our known format
			
			while(fileToConvert.readRecord()){
				SimpleDataObject incomingRecord = new SimpleDataObject();
				
				incomingRecord.practitionerID = incomingFile.get("PRV_PRIMARY_PROVIDER_ID");
				incomingRecord.CPTCode = incomingFile.get("PROCEDURE_CODE");
				incomingRecord.codeOccurrences = Integer.parseInt(incomingFile.get("QUANTITY"));
				incomingRecord.uniquePatient = Integer.parseInt(incomingFile.get("UNIQUE_PATIENTS"));
				
			  this.addSimpleData(incomingRecord);
			}
			
			this.i = this.SimpleDataList.iterator();
			this.fileToConvert.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  	
  	System.out.println("Number of Records: " + this.length());
	}
	
  public void execute(){
  	this.convert(this.fileToConvert);
  	System.out.print(this.print());
  }

	/**
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		// TODO -
			new CSVtoDataSource(args[0]).execute();
	}

}
