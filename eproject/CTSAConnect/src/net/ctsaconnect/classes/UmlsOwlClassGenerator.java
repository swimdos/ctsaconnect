package net.ctsaconnect.classes;

import static net.ctsaconnect.common.Const.*;
import static net.ctsaconnect.common.OWLUtil.*;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

	private Map<String, String> icdAuiToCodeMap = new HashMap<String, String>();

	public void generateICD9CMClasses() throws Exception {
		ontology = man.createOntology(IRI.create(ICD9CM_ONTOLOGY_URI));
		umlsDbConnection = DriverManager.getConnection("jdbc:mysql://bic0193:3307/umls12aa_full",
				DBInfo.READ_UMLS_DB_USER, DBInfo.READ_UMLS_DB_PASS);
		String sql = "select cui, code, str, aui from MRCONSO where tty in ('PT', 'HT') and sab = 'ICD9CM' and lat = 'ENG' ";
		ResultSet rs = umlsDbConnection.prepareStatement(sql).executeQuery();

		// OWLClass superClass = uaddClassDeclaration(ontology,
		// ICD9_BILLING_CODE_URI);
		OWLClass subClass;
		while (rs.next()) {
			String code = rs.getString(2);
			String cleanedCode = code.replace('.', '_');
			icdAuiToCodeMap.put(rs.getString(4), cleanedCode);
			subClass = uaddClassDeclaration(ontology, BASE_ICD9CM_CLASS_URI + cleanedCode);
			uaddLabel(ontology, subClass, rs.getString(3) + " (ICD9CM " + code + ")");
			uaddStringAnnotationAssertion(ontology, subClass, code, HAS_CODE_ANNOT_PROP_URI);
			uaddStringAnnotationAssertion(ontology, subClass, rs.getString(1), HAS_CUI_ANNOT_PROP_URI);
			// don't make all the classes a subclass of ICD9_Billing_CODE_URI anymore
			// uaddSubClassAxiom(ontology, subClass, superClass);
		}
		umlsDbConnection.close();
		man.saveOntology(ontology, new FileOutputStream(OWL_FILES_GENERATED_DIR_NAME + "/"
				+ ICD9CM_ONTOLOGY_FILE_NAME));
	}

	private void generateICD9CMSubClassAxioms() throws Exception {
		ontology = man.createOntology(ugetIri(ICD9CM_SUB_AXIOMS_ONTOLOGY_URI));
		String sql = "select AUI, PAUI from mrhier where sab = 'ICD9CM'";
		umlsDbConnection = DriverManager.getConnection("jdbc:mysql://bic0193:3307/umls12aa_full",
				DBInfo.READ_UMLS_DB_USER, DBInfo.READ_UMLS_DB_PASS);
		ResultSet rs = umlsDbConnection.createStatement().executeQuery(sql);
	
		// Set<String> seenCodes = new HashSet<String>();
		while (rs.next()) {
			String childAui = rs.getString(1);
			String parentAui = rs.getString(2);
			String chiledCode = icdAuiToCodeMap.get(childAui);
			String parentCode = icdAuiToCodeMap.get(parentAui);
			if (chiledCode == null || parentCode == null) {
				System.out.println("Warning , " + childAui + "/" + chiledCode + " " + parentAui + "/"
						+ parentCode + " is null");
			}
			// if (seenCodes.contains(chiledCode)) {
			// System.out.println("Warnign, " + chiledCode +
			// " appears to have two parents");
			// }
			if (parentCode != null) {
				uaddSubClassAxiom(ontology, ugetOWLClass(BASE_ICD9CM_CLASS_URI + chiledCode),
						ugetOWLClass(BASE_ICD9CM_CLASS_URI + parentCode));
			} else if (chiledCode != null) {
				uaddSubClassAxiom(ontology, ugetOWLClass(BASE_ICD9CM_CLASS_URI + chiledCode),
						ugetOWLClass(ICD9_BILLING_CODE_URI));
			}
	
			// seenCodes.add(chiledCode);
		}
		umlsDbConnection.close();
		man.saveOntology(ontology, new FileOutputStream(OWL_FILES_GENERATED_DIR_NAME + File.separator
				+ ICD9CM_SUB_AXIOMS_ONTOLOGY_FILE_NAME));
	
	}

	public void generateCPT2012Classes() throws Exception {
		ontology = man.createOntology(IRI.create(CPT_ONTOLOGY_URI));
		umlsDbConnection = DriverManager.getConnection(DBInfo.UMLS_DB_URL, DBInfo.READ_UMLS_DB_USER,
				DBInfo.READ_UMLS_DB_PASS);
		String sql = "select cui, code, str from MRCONSO where tty = 'PT' and sab = 'CPT'";
		ResultSet rs = umlsDbConnection.prepareStatement(sql).executeQuery();

		OWLClass superClass = uaddClassDeclaration(ontology, CPT_BILLING_CODE_URI);
		OWLClass subClass;

		while (rs.next()) {
			subClass = uaddClassDeclaration(ontology, BASE_CPT_CLASS_URI + rs.getString(2));
			uaddLabel(ontology, subClass, rs.getString(3) + " (CPT code)");
			uaddStringAnnotationAssertion(ontology, subClass, rs.getString(2), HAS_CODE_ANNOT_PROP_URI);
			uaddStringAnnotationAssertion(ontology, subClass, rs.getString(1), HAS_CUI_ANNOT_PROP_URI);
			uaddSubClassAxiom(ontology, subClass, superClass);
		}
		umlsDbConnection.close();
		man.saveOntology(ontology, new FileOutputStream(OWL_FILES_GENERATED_DIR_NAME + "/"
				+ CPT_ONTOLOGY_FILE_NAME));
	}

	public static void main(String[] args) throws Exception {
		UmlsOwlClassGenerator generator = new UmlsOwlClassGenerator();
		generator.generateICD9CMClasses();
		generator.generateICD9CMSubClassAxioms();
		generator.generateCPT2012Classes();
	}
}
