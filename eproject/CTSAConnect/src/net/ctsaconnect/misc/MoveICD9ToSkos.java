package net.ctsaconnect.misc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class MoveICD9ToSkos {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new MoveICD9ToSkos().run();

	}

	OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	OWLOntology oldOntology;
	OWLOntology newOntology;
	OWLOntology icd9_1;
	OWLOntology icd9_2;
	OWLOntology icd9_3;
	OWLOntology icd9_4;
	OWLDataFactory df = man.getOWLDataFactory();
	OWLClass billingConcept = df
			.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/ARG_0000123"));
	OWLDatatype icd9dt = df.getOWLDatatype(IRI
			.create("http://purl.obolibrary.org/obo/arg/datatype/icd9cm"));

	OWLDatatype umls9dt = df.getOWLDatatype(IRI
			.create("http://purl.obolibrary.org/obo/arg/datatype/umls"));

	// ICD9 skos concept scheme
	OWLNamedIndividual icd9Scheme = df.getOWLNamedIndividual(IRI
			.create("http://purl.obolibrary.org/obo/ARG_2000388"));

	private void run() throws Exception {

		AutoIRIMapper mapper = new AutoIRIMapper(new File(System.getProperty("ISF_SVN_DIR")
				+ "/src/ontology/module/icd9"), true);
		man.addIRIMapper(mapper);
		oldOntology = man.loadOntologyFromOntologyDocument(new File(System.getProperty("ISF_SVN_DIR")
				+ "/src/ontology/module/icd9/icd9cm-hierarchy.owl"));

		newOntology = man.loadOntologyFromOntologyDocument(new File(System.getProperty("ISF_SVN_DIR")
				+ "/src/ontology/module/vocabulary-m/icd9.owl"));

		icd9_1 = man.createOntology(IRI
				.create("http://purl.obolibrary.org/obo/arg/module/vocabulary-m/icd9-disease.owl"));
		icd9_2 = man.createOntology(IRI
				.create("http://purl.obolibrary.org/obo/arg/module/vocabulary-m/icd9-procedure.owl"));
		icd9_3 = man.createOntology(IRI
				.create("http://purl.obolibrary.org/obo/arg/module/vocabulary-m/icd9-injury.owl"));
		icd9_4 = man.createOntology(IRI
				.create("http://purl.obolibrary.org/obo/arg/module/vocabulary-m/icd9-status.owl"));

		Set<OWLSubClassOfAxiom> subaxioms = new HashSet<OWLSubClassOfAxiom>();
		for (OWLOntology o : oldOntology.getImportsClosure()) {
			subaxioms.addAll(o.getAxioms(AxiomType.SUBCLASS_OF));
		}

		doTree(df.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/arg/icdcode/001-999_99")), icd9_1);
		doTree(df.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/arg/icdcode/00-99_99")), icd9_2);
		doTree(df.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/arg/icdcode/E000-E999_9")), icd9_3);
		doTree(df.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/arg/icdcode/V01-V91_99")), icd9_4);

		man.saveOntology(icd9_1, new FileOutputStream(new File(System.getProperty("ISF_SVN_DIR")
				+ "/src/ontology/module/vocabulary-m/icd9-disease.owl")));
		man.saveOntology(icd9_2, new FileOutputStream(new File(System.getProperty("ISF_SVN_DIR")
				+ "/src/ontology/module/vocabulary-m/icd9-procedure.owl")));
		man.saveOntology(icd9_3, new FileOutputStream(new File(System.getProperty("ISF_SVN_DIR")
				+ "/src/ontology/module/vocabulary-m/icd9-injury.owl")));
		man.saveOntology(icd9_4, new FileOutputStream(new File(System.getProperty("ISF_SVN_DIR")
				+ "/src/ontology/module/vocabulary-m/icd9-status.owl")));

	}

	void doTree(OWLClass superClass, OWLOntology ontology) {
		Set<OWLClassExpression> classes = superClass.getSubClasses(oldOntology.getImportsClosure());
		OWLNamedIndividual superInd = df.getOWLNamedIndividual(superClass.getIRI());
		man.addAxiom(ontology, df.getOWLClassAssertionAxiom(billingConcept, superInd));

		Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();
		for (OWLOntology o : oldOntology.getImportsClosure()) {
			annotations.addAll(superClass.getAnnotations(o));
		}

		// do the scheme
		man.addAxiom(ontology, df.getOWLObjectPropertyAssertionAxiom(
				df.getOWLObjectProperty(IRI.create("http://www.w3.org/2004/02/skos/core#inScheme")),
				superInd, icd9Scheme));

		for (OWLAnnotation a : annotations) {
			// do the label annotation
			if (a.getProperty().getIRI().toString().equals(OWLRDFVocabulary.RDFS_LABEL.getIRI().toString())) {
				man.addAxiom(ontology, df.getOWLAnnotationAssertionAxiom(superClass.getIRI(), a));
				// man.addAxiom(ontology,
				// df.getOWLAnnotationAssertionAxiom(superClass.getIRI(), df
				// .getOWLAnnotation(df.getOWLAnnotationProperty(IRI
				// .create("http://www.w3.org/2004/02/skos/core#prefLabel")),
				// a.getValue())));

			}

			// do the notation
			if (a.getProperty().getIRI().toString().equals("http://purl.obolibrary.org/obo/ARG_0000033")) {
				String code = ((OWLLiteral) a.getValue()).getLiteral();
				man.addAxiom(ontology, df.getOWLDataPropertyAssertionAxiom(
						df.getOWLDataProperty(IRI.create("http://www.w3.org/2004/02/skos/core#notation")),
						superInd, df.getOWLLiteral(code, icd9dt)));

			}
			if (a.getProperty().getIRI().toString().equals("http://purl.obolibrary.org/obo/ARG_0000034")) {
				String code = ((OWLLiteral) a.getValue()).getLiteral();
				man.addAxiom(ontology, df.getOWLDataPropertyAssertionAxiom(
						df.getOWLDataProperty(IRI.create("http://www.w3.org/2004/02/skos/core#notation")),
						superInd, df.getOWLLiteral(code, umls9dt)));

			}
		}

		for (OWLClassExpression ce : classes) {
			if (ce instanceof OWLClass) {
				OWLClass subClass = (OWLClass) ce;
				OWLNamedIndividual subInd = df.getOWLNamedIndividual(subClass.getIRI());
				man.addAxiom(ontology, df.getOWLClassAssertionAxiom(billingConcept, subInd));
				// has broader
				man.addAxiom(ontology, df.getOWLObjectPropertyAssertionAxiom(
						df.getOWLObjectProperty(IRI.create("http://www.w3.org/2004/02/skos/core#broader")),
						subInd, superInd));
				doTree(subClass, ontology);

			}
		}
	}
}
