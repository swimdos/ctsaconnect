package net.ctsaconnect.misc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.owlapi.XMLCatalogIRIMapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class ISFOntologies {

	public static final String REMOTE_COPY_WORKAREA_CATALOG = "http://connect-isf.googlecode.com/svn/trunk/src/work-area/catalog-v001.xml";
	public static final String LOCAL_COPY_WORKAREA_CATALOG = "src/work-area/catalog-v001.xml";
	public static final String LOCAL_RESOLVE_WORKAREA_CATALOG = "src/work-area/catalog-local.xml";
	public static final String SVN_TRUNK_ROOT = System.getProperty("isf.svn.trunk.root");

	OWLOntologyManager man = OWLManager.createOWLOntologyManager();

	public Map<String, String> loadLocalResolveCatalog() throws MalformedURLException, IOException,
			OWLOntologyCreationException {
		XMLCatalog catalog = CatalogUtilities.parseDocument(new File(SVN_TRUNK_ROOT + "/"
				+ LOCAL_RESOLVE_WORKAREA_CATALOG).toURL());
		// this is the OWLAPI mapper that can be used to configure a manager to
		// resolve URLs based on the catalog entries.
		XMLCatalogIRIMapper xmlm = new XMLCatalogIRIMapper(catalog);

		man.addIRIMapper(xmlm);
		// Open the ontology from a local file
		// The SVN_TRUNK_ROOT needs to point to the root of trunk checkout.
		man.loadOntologyFromOntologyDocument(IRI.create(new File(SVN_TRUNK_ROOT
				+ "/src/work-area/arg-refactoring.owl").toURI()));

		Map<String, String> map = new HashMap<String, String>();
		for (OWLOntology o : man.getOntologies()) {
			map.put(o.getOntologyID().getOntologyIRI().toString(), man.getOntologyDocumentIRI(o)
					.toString());
		}

		return map;
	}

	public Map<String, String> loadRemoteResolveCatalog() throws MalformedURLException, IOException,
			OWLOntologyCreationException {
		XMLCatalog catalog = CatalogUtilities.parseDocument(new URL(REMOTE_COPY_WORKAREA_CATALOG));
		// this is the OWLAPI mapper that can be used to configure a manager to
		// resolve URLs based on the catalog entries.
		XMLCatalogIRIMapper xmlm = new XMLCatalogIRIMapper(catalog);

		man.addIRIMapper(xmlm);
		// Open the ontology from a local file
		// The SVN_TRUNK_ROOT needs to point to the root of trunk checkout.
		man.loadOntologyFromOntologyDocument(IRI.create(new File(SVN_TRUNK_ROOT
				+ "/src/work-area/arg-refactoring.owl").toURI()));

		Map<String, String> map = new HashMap<String, String>();
		for (OWLOntology o : man.getOntologies()) {
			map.put(o.getOntologyID().getOntologyIRI().toString(), man.getOntologyDocumentIRI(o)
					.toString());
		}

		return map;
	}

	public OWLOntologyManager getManager(){
		return man;
	}
	
	public static void main(String[] args) throws MalformedURLException,
			OWLOntologyCreationException, IOException {
		System.out.println(SVN_TRUNK_ROOT);
		ISFOntologies isf = new ISFOntologies();
		Map<String, String> map = isf.loadRemoteResolveCatalog();
		for (Entry<String, String> e : map.entrySet()) {
			System.out.println(e.getKey() + "  " + e.getValue());
		}

	}
}
