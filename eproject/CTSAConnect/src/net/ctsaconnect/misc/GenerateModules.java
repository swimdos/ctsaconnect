package net.ctsaconnect.misc;

import static com.essaid.owlapi.util.OWLQueryUtil.*;
import static net.ctsaconnect.common.Const.*;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.owlapi.XMLCatalogIRIMapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class GenerateModules {
	//@formatter:off
	// TODO
	// need to clean the cache and generated from "no" and "remove" axioms
	// avoid adding the _isf_ annotations
	// write a new script to clean the modules:
	//		if the module has an axiom, and it is in the cache
	// 		if the axiom is not in the refactoring file
	//		the axiom is old and should be removed
	// 		if the axiom is not in cache, it is a locally modified axiom and it should not be 
	//		removed.
  //@formatter:on

	OWLOntology argOntology;
	OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	OWLOntologyManager localMan = OWLManager.createOWLOntologyManager();
	OWLDataFactory df = man.getOWLDataFactory();
	Map<String, OWLOntology> genOntMap = new HashMap<String, OWLOntology>();
	Map<String, OWLOntology> genOntPendingMap = new HashMap<String, OWLOntology>();
	Map<String, OWLOntology> genOntCacheMap = new HashMap<String, OWLOntology>();
	Map<String, OWLOntology> genOntOldMap = new HashMap<String, OWLOntology>();
	OWLAnnotationProperty moduleProperty = df.getOWLAnnotationProperty(IRI.create(REFACT_MODULE));

	public static final String CATALOG_URL = "http://connect-isf.googlecode.com/svn/trunk/src/work-area/catalog-v001.xml";
	public static final String SVN_TRUNK_ROOT = System.getProperty("isf.svn.trunk.root");

	private void go() throws Exception {
		loadOntologies(new File(SVN_TRUNK_ROOT + "/src/ontology/module"));

		// recreate the -generated-pending.owl ontologies
		for (Entry<String, OWLOntology> entry : genOntMap.entrySet()) {
			String moduleName = entry.getKey();
			OWLOntology o = localMan.createOntology(IRI.create(ARG_BASE_URI + "arg/module/" + moduleName
					+ "-generated-pending.owl"));
			localMan.setOntologyDocumentIRI(
					o,
					IRI.create(new File(SVN_TRUNK_ROOT + "/src/ontology/module/" + moduleName + "/"
							+ moduleName + "-generated-pending.owl").getAbsoluteFile().toURI()));

			genOntPendingMap.put(moduleName, o);
			System.out.println("Recreated ontology pending map entry: " + moduleName + " -- "
					+ moduleName);
		}
		// System.out.println(SVN_TRUNK_ROOT);
		XMLCatalog catalog = CatalogUtilities.parseDocument(new URL(CATALOG_URL));
		// this is the OWLAPI mapper that can be used to configure a manager to
		// resolve URLs based on the catalog entries.
		XMLCatalogIRIMapper xmlm = new XMLCatalogIRIMapper(catalog);
		man.addIRIMapper(xmlm);
		argOntology = man.loadOntologyFromOntologyDocument(new File(SVN_TRUNK_ROOT
				+ "/src/work-area/arg-refactoring.owl"));

		// argOntology = man.loadOntologyFromOntologyDocument(IRI
		// .create("http://connect-isf.googlecode.com/svn/trunk/src/work-area/arg-refactoring.owl"));

		for (OWLEntity e : argOntology.getSignature()) {
			Set<OWLAnnotation> modAnnotations = e.getAnnotations(argOntology, moduleProperty);
			for (OWLAnnotation a : modAnnotations) {
				String modString = ((OWLLiteral) a.getValue()).getLiteral();
				System.out.println("Writing" + e.getIRI() + " to module " + modString);
				Set<OWLAnnotationAssertionAxiom> annotationAxioms = ouqGetAnnotationAxioms(e, argOntology,
						true);
				if (e.getIRI().toString().equals("http://xmlns.com/foaf/0.1/Agent")) {
					System.out.println("pause");
				}
				writeModuleEntry(e, modString, annotationAxioms);

			}
		}
		for (OWLAxiom axiom : argOntology.getAxioms()) {
			Set<OWLAnnotation> modAnnotations = axiom.getAnnotations(moduleProperty);
			for (OWLAnnotation a : modAnnotations) {
				String modString = ((OWLLiteral) a.getValue()).getLiteral();
				writeModuleEntry(axiom, modString, new HashSet<OWLAnnotationAssertionAxiom>());
			}

		}

		saveOntologies();
		System.out.println("Finished.");
	}

	private void saveOntologies() throws Exception {
		for (OWLOntology o : genOntMap.values()) {
			System.out.println("Saving ontology: " + o);
			localMan.saveOntology(o);
		}
		for (OWLOntology o : genOntCacheMap.values()) {
			System.out.println("Saving ontology: " + o);
			localMan.saveOntology(o);
		}
		for (OWLOntology o : genOntOldMap.values()) {
			System.out.println("Saving ontology: " + o);
			localMan.saveOntology(o);
		}
		for (OWLOntology o : genOntPendingMap.values()) {
			System.out.println("Saving ontology: " + o);
			localMan.saveOntology(o);
		}

	}

	boolean approved = false;
	boolean notApproved = false;
	boolean declared = false;

	private void writeModuleEntry(OWLObject object, String moduleAnnotation,
			Set<OWLAnnotationAssertionAxiom> annotationAxioms) throws Exception {
		String[] tokens = moduleAnnotation.split("[ \n]+");
		for (String token : tokens) {
			approved = false;
			notApproved = false;
			declared = false;
			if (token.trim().equals(""))
				continue;
			// System.out.println("Doing token: " + token);
			int index = token.indexOf(':');
			if (index > -1) {
				// we have a colon
				String moduleSuffixAll = token.substring(index + 1);
				String moduleName = token.substring(0, index);
				// check if the module assignment is approved
				if (moduleSuffixAll.toLowerCase().endsWith("y")) {
					approved = true;
				}
				if (moduleSuffixAll.toLowerCase().endsWith("n")) {
					notApproved = true;
				}
				if (moduleSuffixAll.toLowerCase().startsWith("d")) {
					declared = true;
				}

				// if (moduleSuffixAll.toLowerCase().equals("y")) {
				// // approved and should use
				// // get the letter before the "y" as the suffix
				// moduleSuffixAll = token.substring(index - 1, index);
				// moduleName = token.substring(0, index - 2);
				//
				// } else {
				// // not yet decided
				// continue;
				// }
				// System.out.println("Writing to module: " + moduleName);
				// if (moduleSuffixAll.toLowerCase().equals("d")) {
				writeOwlObject(moduleName, moduleSuffixAll, object, annotationAxioms);

				// } else if (moduleSuffixAll.toLowerCase().equals("r")) {
				// writeOwlObject(moduleName, moduleSuffixAll, object);

				// }
			}
		}

	}

	private void writeOwlObject(String moduleName, String suffix, OWLObject object,
			Set<OWLAnnotationAssertionAxiom> annotations) throws Exception {

		OWLOntology moduleOntology = genOntMap.get(moduleName);
		OWLOntology modulePendingOntology = genOntPendingMap.get(moduleName);
		OWLOntology cacheOntology = genOntCacheMap.get(moduleName);
		OWLOntology oldOntology = genOntOldMap.get(moduleName);
		if (moduleOntology == null) {
			new File(SVN_TRUNK_ROOT + "/src/ontology/module/" + moduleName).mkdir();

			// the generated approved ontology
			moduleOntology = localMan.createOntology(IRI.create(ARG_BASE_URI + "arg/module/" + moduleName
					+ "-generated.owl"));
			localMan.setOntologyDocumentIRI(
					moduleOntology,
					IRI.create(new File(SVN_TRUNK_ROOT + "/src/ontology/module/" + moduleName + "/"
							+ moduleName + "-generated.owl").getAbsoluteFile().toURI()));
			genOntMap.put(moduleName, moduleOntology);
			System.out.println("Created ontology and added to map: " + moduleName + " -- "
					+ moduleOntology);

			// the generated pending ontology
			modulePendingOntology = localMan.createOntology(IRI.create(ARG_BASE_URI + "arg/module/"
					+ moduleName + "-generated-pending.owl"));
			localMan.setOntologyDocumentIRI(
					modulePendingOntology,
					IRI.create(new File(SVN_TRUNK_ROOT + "/src/ontology/module/" + moduleName + "/"
							+ moduleName + "-generated-pending.owl").getAbsoluteFile().toURI()));
			genOntPendingMap.put(moduleName, modulePendingOntology);
			System.out.println("Created pending ontology and added to map: " + moduleName + " -- "
					+ modulePendingOntology);

			// the cach ontology
			cacheOntology = localMan.createOntology(IRI.create(ARG_BASE_URI + "arg/module/" + moduleName
					+ "-generated-cache.owl"));
			localMan.setOntologyDocumentIRI(
					cacheOntology,
					IRI.create(new File(SVN_TRUNK_ROOT + "/src/ontology/module/" + moduleName + "/"
							+ moduleName + "-generated-cache.owl").getAbsoluteFile().toURI()));
			genOntCacheMap.put(moduleName, cacheOntology);
			System.out.println("Created cach ontology and added to map: " + moduleName + " -- "
					+ cacheOntology);

			// the old ontology
			oldOntology = localMan.createOntology(IRI.create(ARG_BASE_URI + "arg/module/" + moduleName
					+ "-generated-old.owl"));
			localMan.setOntologyDocumentIRI(
					oldOntology,
					IRI.create(new File(SVN_TRUNK_ROOT + "/src/ontology/module/" + moduleName + "/"
							+ moduleName + "-generated-old.owl").getAbsoluteFile().toURI()));
			genOntOldMap.put(moduleName, oldOntology);
			System.out.println("Created old ontology and added to map: " + moduleName + " -- "
					+ oldOntology);
		}

		// add the axioms
		OWLAxiom axiom = null;
		if (object instanceof OWLEntity) {
			axiom = df.getOWLDeclarationAxiom((OWLEntity) object);
		} else if (object instanceof OWLAxiom) {
			axiom = (OWLAxiom) object;
		}

		if (notApproved) {
			// make sure it is not in generated, if it is in, move to old
			if (cacheOntology.containsAxiom(axiom)) {
				// remove it and add it to old;
				List<OWLOntologyChange> changes = man.removeAxiom(moduleOntology, axiom);
				for (OWLAxiom a : annotations) {
					changes.addAll(man.removeAxiom(moduleOntology, a));
				}
				for (OWLOntologyChange change : changes) {
					man.addAxiom(oldOntology, change.getAxiom());
				}
			}
			// remove from cache
			man.removeAxiom(cacheOntology, axiom);
			man.removeAxioms(cacheOntology, annotations);
			// remove from pending
			man.removeAxiom(modulePendingOntology, axiom);
			man.removeAxioms(modulePendingOntology, annotations);
		}
		if (approved) {
			if (!cacheOntology.containsAxiom(axiom)) {
				man.addAxiom(moduleOntology, axiom);
				man.addAxiom(cacheOntology, axiom);
			}
			if (declared) {
				man.addAxioms(moduleOntology, annotations);
				man.addAxioms(cacheOntology, annotations);
			} else {
				for (OWLAnnotationAssertionAxiom aaa : annotations) {
					if (aaa.getProperty().getIRI().equals(OWLRDFVocabulary.RDFS_LABEL.getIRI())) {
						man.addAxiom(moduleOntology, aaa);
						man.addAxiom(cacheOntology, aaa);
					}
				}
			}
		}
		if (!approved && !notApproved) {
			// clean generated and cache, if needed
			// remove it and add it to old;
			List<OWLOntologyChange> changes = man.removeAxiom(moduleOntology, axiom);
			for (OWLAxiom a : annotations) {
				changes.addAll(man.removeAxiom(moduleOntology, a));
			}
			for (OWLOntologyChange change : changes) {
				man.addAxiom(oldOntology, change.getAxiom());
			}
			// remove from cache
			man.removeAxiom(cacheOntology, axiom);
			man.removeAxioms(cacheOntology, annotations);

			man.addAxiom(modulePendingOntology, axiom);
			if (declared) {
				man.addAxioms(modulePendingOntology, annotations);
			} else {
				for (OWLAnnotationAssertionAxiom aaa : annotations) {
					if (aaa.getProperty().getIRI().equals(OWLRDFVocabulary.RDFS_LABEL.getIRI())) {
						man.addAxiom(modulePendingOntology, aaa);
					}
				}
			}
		}

	}

	private void loadOntologies(File directory) throws Exception {
		String moduleName = "";
		if (directory.isDirectory()) {
			File[] files = directory.listFiles();
			for (File file : files) {
				if (!file.isDirectory()) {
					if (file.getName().endsWith("-generated.owl")) {
						moduleName = file.getName().substring(0, file.getName().indexOf('-'));
						genOntMap.put(moduleName, localMan.loadOntologyFromOntologyDocument(file));
						System.out.println("Loading ontology map entry: " + moduleName + " -- " + file);

					}
					if (file.getName().endsWith("-generated-pending.owl")) {
						file.delete();

					}
					if (file.getName().endsWith("-generated-cache.owl")) {
						moduleName = file.getName().substring(0, file.getName().indexOf('-'));
						genOntCacheMap.put(moduleName, localMan.loadOntologyFromOntologyDocument(file));
						System.out.println("Loading ontology cache map entry: " + moduleName + " -- " + file);
					}
					if (file.getName().endsWith("-generated-old.owl")) {
						moduleName = file.getName().substring(0, file.getName().indexOf('-'));
						genOntOldMap.put(moduleName, localMan.loadOntologyFromOntologyDocument(file));
						System.out.println("Loading ontology old map entry: " + moduleName + " -- " + file);
					}

				} else {
					loadOntologies(file);
				}

			}
		}
	}

	//@formatter:off
	/* Module assignment
	 * 		Yes: 
	*/		
  //@formatter:on

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new GenerateModules().go();

	}
}
