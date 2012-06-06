package net.ctsaconnect.common;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class OWLUtil {

	private static OWLDataFactory df = OWLManager.getOWLDataFactory();

	public static OWLClass uaddClassDeclaration(OWLOntology ontology, String classIri) {
		OWLDeclarationAxiom da = ugetDeclaration(ugetOWLClass(classIri));
		ontology.getOWLOntologyManager().addAxiom(ontology, da);
		return (OWLClass) da.getEntity();
	}

	public static OWLAnnotationAssertionAxiom uaddLabel(OWLOntology ontology, OWLEntity entity,
			String label) {
		OWLAnnotationAssertionAxiom aa = df.getOWLAnnotationAssertionAxiom(entity.getIRI(),
				ugetLabelAnnotation(label, "en"));
		ontology.getOWLOntologyManager().addAxiom(ontology, aa);
		return aa;
	}

	public static OWLAnnotationProperty ugetAnnotProperty(String propertyIri) {
		return df.getOWLAnnotationProperty(ugetIri(propertyIri));
	}

	public static OWLLiteral ugetDateLiteral(String date) {
		return df.getOWLLiteral(date, OWL2Datatype.XSD_DATE_TIME);

	}

	public static OWLDeclarationAxiom ugetDeclaration(OWLEntity entity) {
		return df.getOWLDeclarationAxiom(entity);
	}

	public static IRI ugetIri(String iriString) {
		return IRI.create(iriString);
	}

	public static OWLAnnotation ugetLabelAnnotation(String label, String language) {
		OWLAnnotation a = df.getOWLAnnotation(
				ugetAnnotProperty(OWLRDFVocabulary.RDFS_LABEL.toString()),
				ugetStringLiteral(label, language));
		return a;
	}

	public static OWLClass ugetOWLClass(String classIri) {
		return df.getOWLClass(ugetIri(classIri));
	}

	public static OWLLiteral ugetStringLiteral(String value, String language) {
		OWLLiteral l;
		if (language != null) {
			l = df.getOWLLiteral(value, language);
		} else {
			l = df.getOWLLiteral(value, "en");
		}
		return l;
	}

	public static OWLSubClassOfAxiom uaddSubClassAxiom(OWLOntology ontology, OWLClass subClass,
			OWLClass superClass) {
		OWLSubClassOfAxiom sa = df.getOWLSubClassOfAxiom(subClass, superClass);
		ontology.getOWLOntologyManager().addAxiom(ontology, sa);
		return sa;
	}

	public static OWLSubClassOfAxiom uaddSubClass(OWLOntology ontology, String subclassUri,
			String superClassUri) {
		return uaddSubClassAxiom(ontology, ugetOWLClass(subclassUri), ugetOWLClass(superClassUri));
	}

	public static OWLAnnotationAssertionAxiom uaddStringAnnotationAssertion(OWLOntology ontology,
			OWLEntity subject, String value, String propertyUri) {
		OWLAnnotationAssertionAxiom aa = df.getOWLAnnotationAssertionAxiom(subject.getIRI(),
				df.getOWLAnnotation(ugetAnnotProperty(propertyUri), ugetStringLiteral(value, "en")));
		ontology.getOWLOntologyManager().addAxiom(ontology, aa);
		return aa;
	}

	public static OWLNamedIndividual ugetNamedIndividual(String uri) {
		return df.getOWLNamedIndividual(ugetIri(uri));

	}

	public static OWLNamedIndividual uaddNamedIndividual(OWLOntology ontology, String uri) {
		OWLOntologyManager man = ontology.getOWLOntologyManager();
		OWLNamedIndividual i = ugetNamedIndividual(uri);
		man.addAxiom(ontology, df.getOWLDeclarationAxiom(i));
		return i;
	}

	public static OWLClassAssertionAxiom uaddClassAssertion(OWLOntology ontology,
			String individualUri, String owlClassUri) {
		return uaddClassAssertion(ontology, ugetNamedIndividual(individualUri),
				ugetOWLClass(owlClassUri));
	}

	public static OWLClassAssertionAxiom uaddClassAssertion(OWLOntology ontology,
			OWLIndividual individual, OWLClass owlClass) {
		OWLOntologyManager man = ontology.getOWLOntologyManager();
		OWLClassAssertionAxiom a = df.getOWLClassAssertionAxiom(owlClass, individual);
		man.addAxiom(ontology, a);
		return a;
	}

	public static OWLDataPropertyAssertionAxiom uaddDataAssertion(OWLOntology ontology,
			OWLIndividual individual, OWLDataProperty property, String value, OWL2Datatype type) {
		OWLDataPropertyAssertionAxiom a = df.getOWLDataPropertyAssertionAxiom(property, individual,
				ugetOwlLiteral(value, type));
		OWLOntologyManager man = ontology.getOWLOntologyManager();
		man.addAxiom(ontology, a);
		return a;
	}

	public static OWLLiteral ugetOwlLiteral(String value, OWL2Datatype type) {
		return df.getOWLLiteral(value, type);
	}

	public static OWLDataProperty uaddDataProperty(OWLOntology ontology, String dataPropertyUri) {
		OWLOntologyManager man = ontology.getOWLOntologyManager();
		OWLDataProperty p = df.getOWLDataProperty(ugetIri(dataPropertyUri));
		man.addAxiom(ontology, df.getOWLDeclarationAxiom(p));
		return p;
	}

	public static OWLObjectProperty uaddObjectProperty(OWLOntology ontology, String objectPropertyUri) {
		OWLOntologyManager man = ontology.getOWLOntologyManager();
		OWLObjectProperty p = df.getOWLObjectProperty(ugetIri(objectPropertyUri));
		man.addAxiom(ontology, df.getOWLDeclarationAxiom(p));
		return p;
	}

	public static OWLObjectPropertyAssertionAxiom uaddObjectAssertion(OWLOntology ontology,
			OWLObjectProperty objectProperty, OWLIndividual fromIndividual, OWLIndividual toIndividual) {
		OWLObjectPropertyAssertionAxiom a = df.getOWLObjectPropertyAssertionAxiom(objectProperty,
				fromIndividual, toIndividual);
		OWLOntologyManager man = ontology.getOWLOntologyManager();
		man.addAxiom(ontology, a);
		return a;
	}
}
