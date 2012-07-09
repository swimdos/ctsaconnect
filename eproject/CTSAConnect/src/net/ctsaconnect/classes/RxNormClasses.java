package net.ctsaconnect.classes;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import net.ctsaconnect.common.DBInfo;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import static net.ctsaconnect.common.Const.*;
import static net.ctsaconnect.common.OWLUtil.*;

public class RxNormClasses {
	private OWLOntologyManager man = OWLManager.createOWLOntologyManager();;
	private Connection rxnormDbConnection;
	private OWLOntology ontology;

	void generate() throws Exception {
		rxnormDbConnection = DriverManager.getConnection(DBInfo.RXNORM_DB_URL,
				DBInfo.READ_UMLS_DB_USER, DBInfo.READ_UMLS_DB_PASS);
		ontology = man.createOntology(ugetIri(RXN_ONTOLOGY_URI));
		String sql = "select rxcui, str  from rxnconso where sab='rxnorm' and tty in ('SCD', 'SBD')";
		ResultSet rs = rxnormDbConnection.createStatement().executeQuery(sql);

		while (rs.next()) {

		}

		man.saveOntology(ontology, new FileOutputStream(OWL_FILES_GENERATED_DIR_NAME + File.separator
				+ RXN_ONTOLOGY_FILE_NAME));

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new RxNormClasses().generate();

	}

}
