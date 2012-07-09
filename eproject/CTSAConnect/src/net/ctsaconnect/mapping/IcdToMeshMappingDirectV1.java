package net.ctsaconnect.mapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import net.ctsaconnect.common.DBInfo;

import static net.ctsaconnect.common.OWLUtil.*;
import static net.ctsaconnect.common.Const.*;

public class IcdToMeshMappingDirectV1 {

	OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	OWLDataFactory df = man.getOWLDataFactory();

	OWLOntology sameCuiOntology;
	OWLOntology similarCuiOntology;
	OWLOntology broaderCuiOntology;

	void generate() throws Exception {
		init();
		String sql = "select CUI, CODE, STR from mrconso where SAB='ICD9CM' and tty in ('PT', 'HT') ";
		ResultSet rs = con.createStatement().executeQuery(sql);
		while (rs.next()) {
			String cui = rs.getString(1);
			String icdCode = rs.getString(2);
			String label = rs.getString(3);
			System.out.println("\nFinding MeSH for: " + cui + "/" + icdCode + " " + label);
			Set<String> sameCuiMesh = getMeshFromSameCui(icdCode);
			Set<String> similarCuiMesh = getMeshFromSimilarCui(icdCode);
//			System.out.println(icdCode + "  " + sameCuiMesh.size() + "  " + similarCuiMesh.size());
//			if (sameCuiMesh.size() > 0 && similarCuiMesh.size() > 0) {
//				System.out.println("hello");
//			}
		}

	}

	void init() throws Exception {
		con = DBInfo.getReadUmlsDbConnection();
		// from same cui
		String sql = "select c2.code, c2.STR from  mrconso c1 inner join mrconso c2 on c1.cui = c2.cui where c1.code = ? and c1.sab = 'ICD9CM' and c1.tty in ('PT', 'HT') and c1.lat = 'ENG' and c2.sab = 'MSH' and c2.tty = 'MH'";
		ps1 = con.prepareStatement(sql);
		sameCuiOntology = man.createOntology(ugetIri(ICD9_MESH_SAME_CUI_URI));

		// from related cui
		sql = "select c2.code, c2.STR from mrconso c1 inner join mrrel r on c1.CUI = r.CUI1 	inner join mrconso c2 on c2.cui = r.CUI2 	where c1.code = ? and c1.sab = 'ICD9CM' and c1.tty in ('PT', 'HT') and c1.lat = 'ENG' and c2.lat = 'ENG' 	and r.REL = 'RL' and c2.sab = 'MSH' and c2.tty = 'MH' 	and r.cui1 != r.cui2 ";
		ps2 = con.prepareStatement(sql);
		similarCuiOntology = man.createOntology(ugetIri(ICD9_MESH_SIMILAR_CUI_ONTOLOGY_URI));

		// from broader cui
		sql = "";
		// ps3 = con.prepareStatement("");
	}

	Connection con;

	PreparedStatement ps1;
	PreparedStatement ps2;
	PreparedStatement ps3;

	Set<String> getMeshFromSameCui(String icdcode) throws Exception {
		Set<String> meshCode = new HashSet<String>();
		ps1.setString(1, icdcode);
		ResultSet rs = ps1.executeQuery();

		while (rs.next()) {
			String code = rs.getString(1);
			if (!meshCode.contains(code)) {
				System.out.println("\tSame MeSH:" + code + "  " + rs.getString(2));
				meshCode.add(rs.getString(1));
			}
		}
		return meshCode;
	}

	Set<String> getMeshFromSimilarCui(String icdcode) throws Exception {
		Set<String> meshCode = new HashSet<String>();
		ps2.setString(1, icdcode);
		ResultSet rs = ps2.executeQuery();

		while (rs.next()) {
			String code = rs.getString(1);
			if (!meshCode.contains(code)) {
				System.out.println("\tSimilar MeSH:" + code + "  " + rs.getString(2));
				meshCode.add(rs.getString(1));
			}
		}
		return meshCode;
	}

	List<String> getMeshFromBroaderCui(String cui, String icdcode) throws Exception {
		List<String> meshCode = null;

		return meshCode;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new IcdToMeshMappingDirectV1().generate();
	}

}
