package sourcefiles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class GenerateSources {

	OWLOntologyManager topManager = OWLManager.createOWLOntologyManager();
	OWLOntologyManager sourceManager = OWLManager.createOWLOntologyManager();
	OWLDataFactory df = sourceManager.getOWLDataFactory();
	OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();

	OWLOntology topOntology;
	String topFile = "C:/s/svns/isf-new-layout/trunk/src/ontology/module/ero/ero.owl";
	String topDirectory = "C:/s/svns/isf-new-layout/trunk/src/ontology";

	OWLOntology isfOntology;
	String isfFile = "C:/s/svns/isf-new-layout/trunk/src/ontology/arg.owl";
	String isfDirectory = "C:/s/svns/isf-new-layout/trunk/src/ontology";

	OWLOntology disjointOntology;
	String disjIri = "http://purl.obolibrary.org/obo/arg/src-disjoint.owl";
	String disjUrl = "C:/s/svns/isf-new-layout/trunk/src/ontology/module/ero/src-tmp/src-disjoint.owl";

	OWLOntology equivalentOntology;
	String equivIri = "http://purl.obolibrary.org/obo/arg/src-equivalence.owl";
	String equivUrl = "C:/s/svns/isf-new-layout/trunk/src/ontology/module/ero/src-tmp/src-equivalence.owl";

	OWLOntology bfoBridgeOntology;
	String bfoIri = "http://purl.obolibrary.org/obo/arg/src-bfo-bridge.owl";
	String bfoUrl = "C:/s/svns/isf-new-layout/trunk/src/ontology/module/ero/src-tmp/src-bfo-bridge.owl";

	OWLOntology importerOntology;
	String importerIri = "http://purl.obolibrary.org/obo/arg/src-importer.owl";
	String importerUrl = "C:/s/svns/isf-new-layout/trunk/src/ontology/module/ero/src-tmp/src-importer.owl";

	PrintWriter pr;

	//
	// OWLOntology sourceOntology;
	// String sourceIri = "http://purl.obolibrary.org/obo/arg/src-equipment.owl";
	// String sourceUrl =
	// "C:/s/svns/isf-new-layout/trunk/src/ontology/module/ero/src-equipment.owl";

	void run() throws Exception {
		pr = new PrintWriter("C:/s/svns/isf-new-layout/trunk/src/ontology/module/ero/src-tmp/log_"
				+ System.currentTimeMillis() + ".txt");

		AutoIRIMapper mapper = new AutoIRIMapper(new File(topDirectory), true);
		topManager.addIRIMapper(mapper);
		mapper = new AutoIRIMapper(new File(topDirectory), true);
		sourceManager.addIRIMapper(mapper);

		topOntology = topManager.loadOntologyFromOntologyDocument(new File(topFile));
		isfOntology = topManager.loadOntologyFromOntologyDocument(new File(isfFile));

		File owlFile = new File(disjUrl);
		if (owlFile.exists()) {
			disjointOntology = sourceManager.loadOntologyFromOntologyDocument(owlFile);
		} else {
			disjointOntology = sourceManager.createOntology(IRI.create(disjIri));
		}

		owlFile = new File(equivUrl);
		if (owlFile.exists()) {
			equivalentOntology = sourceManager.loadOntologyFromOntologyDocument(owlFile);
		} else {
			equivalentOntology = sourceManager.createOntology(IRI.create(equivIri));
		}

		owlFile = new File(bfoUrl);
		if (owlFile.exists()) {
			bfoBridgeOntology = sourceManager.loadOntologyFromOntologyDocument(owlFile);
		} else {
			bfoBridgeOntology = sourceManager.createOntology(IRI.create(bfoIri));
		}

		owlFile = new File(importerUrl);
		if (owlFile.exists()) {
			importerOntology = sourceManager.loadOntologyFromOntologyDocument(owlFile);
		} else {
			importerOntology = sourceManager.createOntology(IRI.create(importerIri));
		}

		//
		// owlFile = new File(sourceUrl);
		// if (owlFile.exists()) {
		// sourceOntology = sourceManager.loadOntologyFromOntologyDocument(owlFile);
		// } else {
		// sourceOntology = sourceManager.createOntology(IRI.create(sourceIri));
		// }

		Set<OWLClass> tops = Util.getTopClasses(Const.TOPS_IRI_LIST_FILE_NAME);
		Set<OWLClass> subs = new HashSet<OWLClass>();

		for (OWLClass c : tops) {
			subs.add(c);
			subs.addAll(Util.getSubs(topOntology, c));
		}

		for (OWLClass c : subs) {
			for (OWLOntology o : topOntology.getImportsClosure()) {
				Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
				axioms.addAll(o.getAxioms(c));
				axioms.addAll(c.getAnnotationAssertionAxioms(o));
				axiom: for (OWLAxiom a : axioms) {
					topManager.removeAxiom(o, a);
					modifiedOntologies.add(o);
					if (a instanceof OWLEquivalentClassesAxiom) {
						sourceManager.addAxiom(equivalentOntology, a);
						pr.println("Axiom: " + a + "\t ======  from " + o.getOntologyID().getOntologyIRI()
								+ "\t ======  to " + equivalentOntology.getOntologyID().getOntologyIRI());
					} else if (a instanceof OWLDisjointClassesAxiom) {
						sourceManager.addAxiom(disjointOntology, a);
						pr.println("Axiom: " + a + "\t ======  from " + o.getOntologyID().getOntologyIRI()
								+ "\t ======  to " + disjointOntology.getOntologyID().getOntologyIRI());
					} else {
						// take out the BFO ones
						Set<OWLClass> sig = a.getClassesInSignature();
						for (OWLClass c2 : sig) {
							if (c2.getIRI().toString().toLowerCase().contains("bfo")) {
								sourceManager.addAxiom(bfoBridgeOntology, a);
								pr.println("Axiom: " + a + "\t ======  from " + o.getOntologyID().getOntologyIRI()
										+ "\t ======  to " + bfoBridgeOntology.getOntologyID().getOntologyIRI());
								continue axiom;
							}
						}
						// save the rest in a file that matches where it is comming from
						saveAxiom(a, o);
					}
				}
				topManager.removeAxiom(o, df.getOWLDeclarationAxiom(c));
			}
		}

		// we need to make sure we keep labels around in all files
		addLabels();

		if (disjointOntology.getAxiomCount() > 0) {
			sourceManager.saveOntology(disjointOntology, new FileOutputStream(disjUrl));

			OWLImportsDeclaration im = df.getOWLImportsDeclaration(disjointOntology.getOntologyID()
					.getOntologyIRI());
			AddImport add = new AddImport(importerOntology, im);
			sourceManager.applyChange(add);
		}
		if (equivalentOntology.getAxiomCount() > 0) {
			sourceManager.saveOntology(equivalentOntology, new FileOutputStream(equivUrl));

			OWLImportsDeclaration im = df.getOWLImportsDeclaration(equivalentOntology.getOntologyID()
					.getOntologyIRI());
			AddImport add = new AddImport(importerOntology, im);
			sourceManager.applyChange(add);
		}
		if (bfoBridgeOntology.getAxiomCount() > 0) {
			sourceManager.saveOntology(bfoBridgeOntology, new FileOutputStream(bfoUrl));

			OWLImportsDeclaration im = df.getOWLImportsDeclaration(bfoBridgeOntology.getOntologyID()
					.getOntologyIRI());
			AddImport add = new AddImport(importerOntology, im);
			sourceManager.applyChange(add);
		}

		for (OWLOntology o : tempManager.getOntologies()) {
			int i = o.getOntologyID().getOntologyIRI().toString().lastIndexOf('/');
			String fileName = o.getOntologyID().getOntologyIRI().toString().substring(i + 1);
			tempManager.saveOntology(o, new FileOutputStream(
					"C:/s/svns/isf-new-layout/trunk/src/ontology/module/ero/src-tmp/" + fileName));
		}

		for (OWLOntology o : modifiedOntologies) {
			topManager.saveOntology(o);
		}

		sourceManager.saveOntology(importerOntology, new FileOutputStream(importerUrl));

		System.err.println("Was there a lable error? " + labelError);
		// sourceManager.saveOntology(sourceOntology, new
		// FileOutputStream(sourceUrl));

	}

	List<OWLOntology> ontologies = new ArrayList<OWLOntology>();

	private void addLabels() {
		ontologies.addAll(isfOntology.getImportsClosure());
		ontologies.addAll(bfoBridgeOntology.getImportsClosure());
		ontologies.addAll(disjointOntology.getImportsClosure());
		ontologies.addAll(equivalentOntology.getImportsClosure());
		ontologies.addAll(tempManager.getOntologies());

		for (OWLEntity c : bfoBridgeOntology.getSignature()) {
			OWLAnnotationAssertionAxiom label = getClassLable(c);
			if (label != null) {
				sourceManager.addAxiom(bfoBridgeOntology, label);
			}
		}

		for (OWLEntity c : disjointOntology.getSignature()) {
			OWLAnnotationAssertionAxiom label = getClassLable(c);
			if (label != null) {
				sourceManager.addAxiom(disjointOntology, label);
			}
		}

		for (OWLEntity c : equivalentOntology.getSignature()) {
			OWLAnnotationAssertionAxiom label = getClassLable(c);
			if (label != null) {
				sourceManager.addAxiom(equivalentOntology, label);
			}
		}

		for (OWLOntology o : modifiedOntologies) {
			for (OWLEntity c : o.getSignature()) {
				OWLAnnotationAssertionAxiom label = getClassLable(c);
				if (label != null) {
					topManager.addAxiom(o, label);
				}
			}

		}

		for (OWLOntology o : tempManager.getOntologies()) {
			for (OWLEntity c : o.getSignature()) {
				OWLAnnotationAssertionAxiom label = getClassLable(c);
				if (label != null) {
					tempManager.addAxiom(o, label);
				}
			}

		}

	}

	OWLAnnotationProperty labelProperty = topManager.getOWLDataFactory().getOWLAnnotationProperty(
			OWLRDFVocabulary.RDFS_LABEL.getIRI());
	boolean labelError = false;

	private OWLAnnotationAssertionAxiom getClassLable(OWLEntity c) {
		OWLAnnotationAssertionAxiom label = null;

		for (OWLOntology o2 : ontologies) {
			Set<OWLAnnotationAssertionAxiom> annotations = c.getAnnotationAssertionAxioms(o2);
			for (OWLAnnotationAssertionAxiom aaa : annotations) {
				IRI annoatationProperty = aaa.getProperty().getIRI();
				if (annoatationProperty.equals(labelProperty.getIRI())) {
					if (label == null) {
						label = aaa;
					} else if (!label.equals(aaa)) {
						System.err.println("Ontology: " + o2.getOntologyID().getOntologyIRI()
								+ " has additional label annoations " + aaa + " in addition to the one found: "
								+ label);
						labelError = true;
					}
				}
			}
		}

		if (label != null) {
			return label;
		} else {
			System.err.println("No label for class: " + c);
			labelError = true;
			return null;
		}

	}

	Set<OWLOntology> modifiedOntologies = new HashSet<OWLOntology>();

	void saveAxiom(OWLAxiom a, OWLOntology fromOntology) throws OWLOntologyCreationException {
		int i = topManager.getOntologyDocumentIRI(fromOntology).toString().lastIndexOf('/');
		String fileName = topManager.getOntologyDocumentIRI(fromOntology).toString().substring(i + 1);
		IRI tmpIRI = IRI.create("http://arg/temp-source-file/" + "src-" + fileName);
		OWLOntology o = tempManager.getOntology(tmpIRI);
		if (o == null) {
			o = tempManager.createOntology(tmpIRI);

			OWLImportsDeclaration im = df.getOWLImportsDeclaration(o.getOntologyID().getOntologyIRI());
			AddImport add = new AddImport(importerOntology, im);
			sourceManager.applyChange(add);
		}
		tempManager.addAxiom(o, a);
		pr.println("Axiom: " + a + "\t ======  from " + fromOntology.getOntologyID().getOntologyIRI()
				+ "\t ======  to " + o.getOntologyID().getOntologyIRI());
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new GenerateSources().run();
	}

}
