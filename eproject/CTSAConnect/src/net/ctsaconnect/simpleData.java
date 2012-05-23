package net.ctsaconnect;

public class simpleData {
/*
 * Defines the structure of data available in the simple data base data
 */
		public String practitionerID, CPTCode, ICD9Code;
		public int codeOccurrences, uniquePatient;
		

	    public  simpleData(String practitionerID,String CPTCode, String ICD9Code, int codeOccurrences, int uniquePatient){
	        this.practitionerID=practitionerID;
	        this.CPTCode=CPTCode;
	        this.ICD9Code=ICD9Code;
	        this.codeOccurrences=codeOccurrences;
	        this.uniquePatient=uniquePatient;
	    }


		
		
}

