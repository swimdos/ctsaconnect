package net.ctsaconnect.classes;

import static net.ctsaconnect.common.OWLUtil.*;
import static net.ctsaconnect.common.Const.*;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import net.ctsaconnect.common.DBInfo;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class UmlsOwlClassGenerator {

	private OWLOntologyManager man = OWLManager.createOWLOntologyManager();;
	private Connection umlsDbConnection;
	private OWLOntology ontology;

	public UmlsOwlClassGenerator() {

	}

	public void generateICD9CMSubClasses() throws Exception {
		ontology = man.createOntology(IRI.create(ICD9CM_ONTOLOGY_URI));
		umlsDbConnection = DriverManager.getConnection("jdbc:mysql://bic0193:3307/umls12aa_full?user=writeumls&password=writeumlspass");
		String sql = "select cui, code, str from MRCONSO where tty = 'PT' and sab = 'ICD9CM' and lat = 'ENG' ";
		ResultSet rs = umlsDbConnection.prepareStatement(sql).executeQuery();

		OWLClass superClass = uaddClassDeclaration(ontology, ICD9_BILLING_CODE_URI);
		OWLClass subClass;

		while (rs.next()) {
			subClass = uaddClassDeclaration(ontology, BASE_ICD9CM_CLASS_URI + rs.getString(2).replace('.', '_'));
			uaddLabel(ontology, subClass, "ICD9 code for " + rs.getString(3));
			uaddStringAnnotationAssertion(ontology, subClass, rs.getString(2), HAS_CODE_ANNOT_PROP_URI);
			uaddStringAnnotationAssertion(ontology, subClass, rs.getString(1), HAS_CUI_ANNOT_PROP_URI);
			uaddSubClassAxiom(ontology, subClass, superClass);
		}
		umlsDbConnection.close();
		man.saveOntology(ontology, new FileOutputStream(OWL_FILES_GENERATED_DIR_NAME + "/" + ICD9CM_ONTOLOGY_FILE_NAME));
	}

	public void generateCPT2012SubClasses() throws Exception {
		ontology = man.createOntology(IRI.create(CPT_ONTOLOGY_URI));
		umlsDbConnection = DriverManager.getConnection(DBInfo.UMLS_DB_URL, DBInfo.READ_UMLS_DB_USER, DBInfo.READ_UMLS_DB_PASS);
		String sql = "select cui, code, str from MRCONSO where tty = 'PT' and sab = 'CPT'";
		ResultSet rs = umlsDbConnection.prepareStatement(sql).executeQuery();

		OWLClass superClass = uaddClassDeclaration(ontology, CPT_BILLING_CODE_URI);
		OWLClass subClass;

		while (rs.next()) {
			subClass = uaddClassDeclaration(ontology, BASE_CPT_CLASS_URI + rs.getString(2));
			uaddLabel(ontology, subClass, "CPT code for " + rs.getString(3));
			uaddStringAnnotationAssertion(ontology, subClass, rs.getString(2), HAS_CODE_ANNOT_PROP_URI);
			uaddStringAnnotationAssertion(ontology, subClass, rs.getString(1), HAS_CUI_ANNOT_PROP_URI);
			uaddSubClassAxiom(ontology, subClass, superClass);
		}
		umlsDbConnection.close();
		man.saveOntology(ontology, new FileOutputStream(OWL_FILES_GENERATED_DIR_NAME + "/" + CPT_ONTOLOGY_FILE_NAME));
	}

	public static void main(String[] args) throws Exception {
		UmlsOwlClassGenerator generator = new UmlsOwlClassGenerator();
		generator.generateICD9CMSubClasses();
		generator.generateCPT2012SubClasses();
	}
}
