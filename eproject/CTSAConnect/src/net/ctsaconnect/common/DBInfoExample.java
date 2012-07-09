package net.ctsaconnect.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBInfoExample {
	// public class DBInfo {

	// A JDBC URL to a UMLS database
	public static final String UMLS_DB_URL = "jdbc:mysql://host_name:port_number/database_name";
	public static final String RXNORM_DB_URL = "jdbc:mysql://host_name:port_number/database_name";
	public static final String SNOMED_DB_URL = "jdbc:mysql://host_name:port_number/database_name";
	public static final String CTSADATA_DB_URL = "jdbc:mysql://host_name:port_number/database_name";

	public static final String READ_UMLS_DB_USER = "user_name";
	public static final String READ_UMLS_DB_PASS = "password";

	public static final String WRITE_UMLS_DB_USER = "user_name";
	public static final String WRITE_UMLS_DB_PASS = "password";

	/**
	 * A static method to simplify getting a read connection to the database.
	 * 
	 * @return the read umls db connection
	 * @throws SQLException
	 *           the sQL exception
	 */
	public static Connection getReadUmlsDbConnection() throws SQLException {
		return DriverManager.getConnection(DBInfo.UMLS_DB_URL, DBInfo.READ_UMLS_DB_USER,
				DBInfo.READ_UMLS_DB_PASS);
	}

	public static Connection getReadSnomedDbConnection() throws SQLException {
		return DriverManager.getConnection(DBInfo.SNOMED_DB_URL, DBInfo.READ_UMLS_DB_USER,
				DBInfo.READ_UMLS_DB_PASS);
	}

	public static Connection getWriteCtsadataDbConnection() throws SQLException {
		return DriverManager.getConnection(DBInfo.CTSADATA_DB_URL, DBInfo.WRITE_UMLS_DB_USER,
				DBInfo.WRITE_UMLS_DB_PASS);
	}

}
