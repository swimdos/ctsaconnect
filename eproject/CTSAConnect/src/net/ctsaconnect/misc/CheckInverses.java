package net.ctsaconnect.misc;

import static com.essaid.owlapi.util.OWLCreateUtil.*;
import static net.ctsaconnect.common.Const.*;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.owlapi.XMLCatalogIRIMapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class CheckInverses {

	OWLOntology refactoringOntology;
	OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	OWLDataFactory df = man.getOWLDataFactory();

	public static final String CATALOG_URL = "http://connect-isf.googlecode.com/svn/trunk/src/work-area/catalog-v001.xml";
	public static final String SVN_TRUNK_ROOT = System.getProperty("isf.svn.trunk.root");

	private void check() throws Exception {
		XMLCatalog catalog = CatalogUtilities.parseDocument(new URL(CATALOG_URL));
		// this is the OWLAPI mapper that can be used to configure a manager to
		// resolve URLs based on the catalog entries.
		XMLCatalogIRIMapper xmlm = new XMLCatalogIRIMapper(catalog);
		man.addIRIMapper(xmlm);
		refactoringOntology = man.loadOntologyFromOntologyDocument(new File(SVN_TRUNK_ROOT
				+ "/src/work-area/arg-refactoring.owl"));

		Set<String> properties = new HashSet<String>();
		properties.add(REFACT_REPLACES_IRI);
		properties.add(REFACT_REPLACED_BY_IRI);
		properties.add(REFACT_POSSIBLE_REPLACES_IRI);
		properties.add(REFACT_POSSIBLE_REPLACED_BY_IRI);
		for (OWLAnnotationAssertionAxiom aaa : refactoringOntology
				.getAxioms(AxiomType.ANNOTATION_ASSERTION)) {
			OWLAnnotationProperty ap = aaa.getProperty();
			if (properties.contains(ap.getIRI().toString())) {
				IRI subject = (IRI) aaa.getSubject();
				IRI object = (IRI) aaa.getValue();

				OWLAnnotationAssertionAxiom aaaAdded = null;
				if (ap.getIRI().toString().equals(REFACT_REPLACED_BY_IRI)) {
					aaaAdded = oucGetAnnotationAssertionAxiom(object,
							oucGetAnnotationProperty(REFACT_REPLACES_IRI).getIRI(), subject);
					if (!refactoringOntology.containsAxiomIgnoreAnnotations(aaaAdded)) {
						System.out.println(aaaAdded);
						man.addAxiom(refactoringOntology, aaaAdded);
					}
				}
				if (ap.getIRI().toString().equals(REFACT_REPLACES_IRI)) {
					aaaAdded = oucGetAnnotationAssertionAxiom(object,
							oucGetAnnotationProperty(REFACT_REPLACED_BY_IRI).getIRI(), subject);
					if (!refactoringOntology.containsAxiomIgnoreAnnotations(aaaAdded)) {
						System.out.println(aaaAdded);
						man.addAxiom(refactoringOntology, aaaAdded);
					}
				}
				if (ap.getIRI().toString().equals(REFACT_POSSIBLE_REPLACES_IRI)) {
					aaaAdded = oucGetAnnotationAssertionAxiom(object,
							oucGetAnnotationProperty(REFACT_POSSIBLE_REPLACED_BY_IRI).getIRI(), subject);
					if (!refactoringOntology.containsAxiomIgnoreAnnotations(aaaAdded)) {
						System.out.println(aaaAdded);
						man.addAxiom(refactoringOntology, aaaAdded);
					}
				}
				if (ap.getIRI().toString().equals(REFACT_POSSIBLE_REPLACED_BY_IRI)) {
					aaaAdded = oucGetAnnotationAssertionAxiom(object,
							oucGetAnnotationProperty(REFACT_POSSIBLE_REPLACES_IRI).getIRI(), subject);
					if (!refactoringOntology.containsAxiomIgnoreAnnotations(aaaAdded)) {
						System.out.println(aaaAdded);
						man.addAxiom(refactoringOntology, aaaAdded);
					}
				}

			}
		}
		man.saveOntology(refactoringOntology);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new CheckInverses().check();

	}
}
