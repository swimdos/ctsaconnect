package net.ctsaconnect.datasource;

/**
 * Defines the structure of data available in the simple database data. This
 * class will represent the common interface for data elements. Original data
 * from various institutions will be used to populate instances of this class.
 * These instances will form the input to any code or algorithms that are not
 * institution specific.
 * 
 * @see DataSource
 */
public class SimpleDataObject {

	public String practitionerID, CPTCode, ICD9Code;
	public int codeOccurrences, uniquePatient;

	public SimpleDataObject(String practitionerID, String CPTCode, String ICD9Code,
			int codeOccurrences, int uniquePatient) {
		this.practitionerID = practitionerID;
		this.CPTCode = CPTCode;
		this.ICD9Code = ICD9Code;
		this.codeOccurrences = codeOccurrences;
		this.uniquePatient = uniquePatient;
	}

	public SimpleDataObject() {
		// TODO Auto-generated constructor stub
	}

}
