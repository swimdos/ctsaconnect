package net.ctsaconnect.misc;

import java.io.File;
import java.net.URL;

import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.owlapi.XMLCatalogIRIMapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class GenerateRefactoringReport {

	public static final String CATALOG_URL = "http://connect-isf.googlecode.com/svn/trunk/src/work-area/catalog-v001.xml";
	public static final String SVN_TRUNK_ROOT = System.getProperty("isf.svn.trunk.root");

	public static void main(String[] args) throws Exception {
		new GenerateRefactoringReport().generate();

	}

	public void generate() throws Exception {
		System.out.println(SVN_TRUNK_ROOT);
		XMLCatalog c = CatalogUtilities.parseDocument(new URL(CATALOG_URL));
		// this is the OWLAPI mapper that can be used to configure a manager to
		// resolve URLs based on the catalog entries.
		XMLCatalogIRIMapper xmlm = new XMLCatalogIRIMapper(c);
		System.out.println("http://vivoweb.org/ontology/core is mapped to: "
				+ xmlm.getDocumentIRI(IRI.create("http://vivoweb.org/ontology/core")));

		// this loads a local catalog and resolves an ontology URI. The output
		// should look something like:
		// http://vivoweb.org/ontology/core/vivo-skos-public-1.5.owl is mapped to:
		// file:/C:/shahim/svns/connect-isf-root/trunk/src/ontology/clinical_module/vivo/vivo-skos-public-1.5.owl
		xmlm = new XMLCatalogIRIMapper(new File(SVN_TRUNK_ROOT
				+ "/src/ontology/clinical_module/catalog-v001.xml"));
		System.out.println("http://vivoweb.org/ontology/core/vivo-skos-public-1.5.owl is mapped to: "
				+ xmlm.getDocumentIRI(IRI
						.create("http://vivoweb.org/ontology/core/vivo-skos-public-1.5.owl")));

		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		man.addIRIMapper(xmlm);

	}

}
