package net.ctsaconnect.classes;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import net.ctsaconnect.common.DBInfo;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import static net.ctsaconnect.common.OWLUtil.*;
import static net.ctsaconnect.common.Const.*;

public class MeSHDescriptorsHierarchy {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new MeSHDescriptorsHierarchy().generate();

	}

	OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	OWLOntology descriptorOntology;
	OWLOntology descriptorDefinitionOntology;
	OWLOntology descriptorSubClassAxiomOntology;
	Connection con;

	private void generate() throws Exception {
		init();

		String sql = "select c.CUI, c.AUI,c.SCUI, c.CODE, c.STR, d.DEF from mrconso c left join mrdef d on c.AUI = d.AUI where c.SAB='MSH' and c.TTY in ('MH', 'HT')  and c.LAT='ENG'";
		ResultSet rs = con.createStatement().executeQuery(sql);
		int counter = 0;
		while (rs.next()) {
			System.out.println(++counter);
			OWLClass descriptor = uaddClassDeclaration(descriptorOntology,
					MESH_BASE_CLASS_URI + rs.getString(4));
			uaddLabel(descriptorOntology, descriptor, rs.getString(5) + " (MeSH " + rs.getString(4) + ")");
			uaddStringAnnotationAssertion(descriptorOntology, descriptor, rs.getString(1),
					HAS_CUI_ANNOT_PROP_URI);
			uaddStringAnnotationAssertion(descriptorOntology, descriptor, rs.getString(4),
					HAS_CODE_ANNOT_PROP_URI);
			String definition = rs.getString(6);
			if (definition != null) {
				uaddStringAnnotationAssertion(descriptorDefinitionOntology, descriptor, rs.getString(6),
						HAS_DEF_ANNOT_PROP_URI);
			}

			Set<String> parentCodes = getParentDescriptors(rs.getString(2));
			for (String parentCode : parentCodes) {
				uaddSubClassAxiom(descriptorSubClassAxiomOntology, descriptor,
						ugetOWLClass(MESH_BASE_CLASS_URI + parentCode));
			}
		}

		man.saveOntology(descriptorOntology, new FileOutputStream(OWL_FILES_GENERATED_DIR_NAME
				+ File.separator + MESH_DESCRIPTOR_FILE_NAME));
		man.saveOntology(descriptorDefinitionOntology, new FileOutputStream(
				OWL_FILES_GENERATED_DIR_NAME + File.separator + MESH_DESCRIPTOR_DEFINITION_FILE_NAME));
		man.saveOntology(descriptorSubClassAxiomOntology, new FileOutputStream(
				OWL_FILES_GENERATED_DIR_NAME + File.separator + MESH_DESCRIPTOR_SUBCLASS_AXIOM_FILE_NAME));
	}

	PreparedStatement parentDescPs;

	Set<String> getParentDescriptors(String descriptorCode) throws SQLException {
		Set<String> descriptors = new HashSet<String>();
		parentDescPs.setString(1, descriptorCode);
		ResultSet rs = parentDescPs.executeQuery();
		while (rs.next()) {
			descriptors.add(rs.getString(1));
		}
		rs.close();
		return descriptors;
	}

	private void init() throws OWLOntologyCreationException, SQLException {
		con = DBInfo.getReadUmlsDbConnection();
		descriptorOntology = man.createOntology(ugetIri(MESH_DESCRIPTOR_ONTOLOGY_URI));

		uaddAnnotationProperty(descriptorOntology, HAS_CUI_ANNOT_PROP_URI);
		uaddAnnotationProperty(descriptorOntology, HAS_CODE_ANNOT_PROP_URI);

		descriptorDefinitionOntology = man.createOntology(ugetIri(MESH_DESCRIPTOR_DEFINITION_ONTOLOGY_URI));
		uaddAnnotationProperty(descriptorDefinitionOntology, HAS_DEF_ANNOT_PROP_URI);

		descriptorSubClassAxiomOntology = man
				.createOntology(ugetIri(MESH_DESCRIPTOR_SUBCLASS_AXIOM_ONTOLOGY_URI));

		String sql = "select c.code from mrhier h inner join mrconso c on h.PAUI = c.AUI where h.AUI = ?";
		parentDescPs = con.prepareStatement(sql);

	}
}
