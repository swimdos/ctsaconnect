package net.ctsaconnect.misc;

import java.io.File;
import java.net.URL;
import java.util.Set;

import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.owlapi.XMLCatalogIRIMapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import static com.essaid.owlapi.util.OWLQueryUtil.*;

public class FindOldURIs {

	OWLOntology refactoringOntology;
	OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	OWLDataFactory df = man.getOWLDataFactory();

	public static final String CATALOG_URL = "http://connect-isf.googlecode.com/svn/trunk/src/work-area/catalog-v001.xml";
	public static final String SVN_TRUNK_ROOT = System.getProperty("isf.svn.trunk.root");

	/**
	 * @param args
	 */
	public void find() throws Exception {

		XMLCatalog catalog = CatalogUtilities.parseDocument(new URL(CATALOG_URL));
		// this is the OWLAPI mapper that can be used to configure a manager to
		// resolve URLs based on the catalog entries.
		XMLCatalogIRIMapper xmlm = new XMLCatalogIRIMapper(catalog);
		man.addIRIMapper(xmlm);
		refactoringOntology = man.loadOntologyFromOntologyDocument(new File(SVN_TRUNK_ROOT
				+ "/src/work-area/arg-refactoring.owl"));

		for (OWLEntity e : refactoringOntology.getSignature(true)) {
			if (e.getIRI().toString().toLowerCase().contains("arg_")) {
				Set<String> labels = ouqGetLabels(refactoringOntology, e.getIRI(), true);
				System.out.println(e.getIRI() + "\t" + labels);

			}
		}

	}

	public static void main(String[] args) throws Exception {
		new FindOldURIs().find();

	}
}
