package net.ctsaconnect.misc;

public class GenId {

	/**
	 * A simple script to create new IRIs for the google spreadsheet/registry of
	 * ARG IRIs. Protege doesn't do a good job tracking used IRIs so we are using
	 * a spreadsheet instead.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String uri = "http://purl.obolibrary.org/obo/ARG_";
		int id = 2000;
		for (int i = 0; i < 1000; ++i) {
			String idString = "0000" + ++id;
			idString = idString.substring(idString.length() - 7, idString.length());
			System.out.println(uri + idString);
		}
	}

}
