package net.ctsaconnect.misc;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.ctsaconnect.common.Const;

import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.owlapi.XMLCatalogIRIMapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import static net.ctsaconnect.common.Const.*;
import static com.essaid.owlapi.util.OWLQueryUtil.*;

public class GenerateModules {

	OWLOntology argOntology;
	OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	OWLOntologyManager localMan = OWLManager.createOWLOntologyManager();
	OWLDataFactory df = man.getOWLDataFactory();
	Map<String, OWLOntology> genOntMap = new HashMap<String, OWLOntology>();
	Map<String, OWLOntology> genOntCacheMap = new HashMap<String, OWLOntology>();
	OWLAnnotationProperty moduleProperty = df.getOWLAnnotationProperty(IRI.create(REFACT_MODULE));

	public static final String CATALOG_URL = "http://connect-isf.googlecode.com/svn/trunk/src/work-area/catalog-v001.xml";
	public static final String SVN_TRUNK_ROOT = System.getProperty("isf.svn.trunk.root");

	private void go() throws Exception {
		loadOntologies(new File(SVN_TRUNK_ROOT + "/src/ontology/module"));
		// System.out.println(SVN_TRUNK_ROOT);
		XMLCatalog catalog = CatalogUtilities.parseDocument(new URL(CATALOG_URL));
		// this is the OWLAPI mapper that can be used to configure a manager to
		// resolve URLs based on the catalog entries.
		XMLCatalogIRIMapper xmlm = new XMLCatalogIRIMapper(catalog);
		man.addIRIMapper(xmlm);

		argOntology = man.loadOntologyFromOntologyDocument(IRI
				.create("http://connect-isf.googlecode.com/svn/trunk/src/work-area/arg-refactoring.owl"));

		for (OWLEntity e : argOntology.getSignature()) {
			Set<OWLAnnotation> modAnnotations = e.getAnnotations(argOntology, moduleProperty);
			for (OWLAnnotation a : modAnnotations) {
				String modString = ((OWLLiteral) a.getValue()).getLiteral();
				writeModuleEntry(e, modString);
				for (OWLAnnotationAxiom aa : ouqGetAnnotationAxioms(e, argOntology, true)) {
					writeModuleEntry(aa, modString);
				}
			}
		}
		for (OWLAxiom axiom : argOntology.getAxioms()) {
			Set<OWLAnnotation> modAnnotations = axiom.getAnnotations(moduleProperty);
			for (OWLAnnotation a : modAnnotations) {
				String modString = ((OWLLiteral) a.getValue()).getLiteral();
				writeModuleEntry(axiom, modString);
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

	}

	private void writeModuleEntry(OWLObject object, String moduleAnnotation) throws Exception {
		String[] tokens = moduleAnnotation.split("[ ]+");
		for (String token : tokens) {
			System.out.println("Doing token: "+token);
			int index = token.lastIndexOf(':');
			if (index > -1) {
				// we have a colon
				String moduleSuffix = token.substring(index + 1);
				String moduleName = token.substring(0, index);
				System.out.println("Writing to module: " + moduleName);
				if (moduleSuffix.toLowerCase().equals("d")) {
					writeOwlObject(moduleName, moduleSuffix, object);

				} else if (moduleSuffix.toLowerCase().equals("r")) {
					writeOwlObject(moduleName, moduleSuffix, object);

				}
			}
		}

	}

	private void writeOwlObject(String moduleName, String suffix, OWLObject object) throws Exception {

		OWLOntology moduleOntology = genOntMap.get(moduleName);
		OWLOntology cacheOntology = genOntCacheMap.get(moduleName);
		if (moduleOntology == null) {
			new File(SVN_TRUNK_ROOT + "/src/ontology/module/" + moduleName).mkdir();
			moduleOntology = localMan.createOntology(IRI.create(ARG_BASE_URI + "arg/module/" + moduleName
					+ "-generated.owl"));
			localMan.setOntologyDocumentIRI(
					moduleOntology,
					IRI.create(new File(SVN_TRUNK_ROOT + "/src/ontology/module/" + moduleName + "/"
							+ moduleName + "-generated.owl").getAbsoluteFile().toURI()));
			genOntMap.put(moduleName, moduleOntology);
			System.out.println("Created ontology and added to map: " + moduleName + " -- "
					+ moduleOntology);
			cacheOntology = localMan.createOntology(IRI.create(ARG_BASE_URI + "arg/module/" + moduleName
					+ "-generated-cache.owl"));
			localMan.setOntologyDocumentIRI(
					cacheOntology,
					IRI.create(new File(SVN_TRUNK_ROOT + "/src/ontology/module/" + moduleName + "/"
							+ moduleName + "-generated-cache.owl").getAbsoluteFile().toURI()));
			genOntCacheMap.put(moduleName, cacheOntology);
			System.out.println("Created ontology and added to map: " + moduleName + " -- "
					+ cacheOntology);
		}
		OWLAxiom axiom = null;
		if (object instanceof OWLEntity) {
			axiom = df.getOWLDeclarationAxiom((OWLEntity) object);
		} else if (object instanceof OWLAxiom) {
			axiom = (OWLAxiom) object;
		}
		if (!cacheOntology.containsAxiom(axiom)) {
			if (!(axiom instanceof OWLAnnotationAxiom)) {
				man.addAxiom(moduleOntology, axiom);
				man.addAxiom(cacheOntology, axiom);
			} else {
				// only add annotation axioms (labels, definitions, etc.) to declaration
				// ontology
				if (suffix.equals("d")) {
					man.addAxiom(moduleOntology, axiom);
					man.addAxiom(cacheOntology, axiom);
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
					if (file.getName().endsWith("-generated-cache.owl")) {
						moduleName = file.getName().substring(0, file.getName().indexOf('-'));
						genOntCacheMap.put(moduleName, localMan.loadOntologyFromOntologyDocument(file));
						System.out.println("Loading ontology cache map entry: " + moduleName + " -- " + file);
					}

				} else {
					loadOntologies(file);
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new GenerateModules().go();

	}
}
