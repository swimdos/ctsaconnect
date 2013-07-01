package net.ctsaconnect.misc;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class VivoFreshStartDeprecation {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new VivoFreshStartDeprecation().run();

	}

	OWLOntologyManager manOrig = OWLManager.createOWLOntologyManager();
	OWLOntologyManager man2 = OWLManager.createOWLOntologyManager();
	OWLOntologyManager man3 = OWLManager.createOWLOntologyManager();
	OWLOntology vivoOrig;
	OWLOntology vivo;
	OWLOntology vivoApp;
	OWLOntology fullArg;
	private OWLOntology vivoDep;

	private void run() throws Exception {
		System.out.println(System.getProperty("ISF_SVN_DIR") + "/src/ontology/imports/vivo");
		manOrig.addIRIMapper(new AutoIRIMapper(new File(
				"C:/s/svns/connect-isf-root/trunk/src/ontology/imports/vivo"), true));
		vivoOrig = manOrig.loadOntologyFromOntologyDocument(new File(
				"C:/s/svns/connect-isf-root/trunk/src/ontology/imports/vivo/vivo-core-public-1.5.owl"));
//		System.out.println(vivoOrig.getTBoxAxioms(true).size());

		man2.addIRIMapper(new AutoIRIMapper(new File(System.getProperty("ISF_SVN_DIR")
				+ "/src/ontology/module"), true));
		vivo = man2.loadOntologyFromOntologyDocument(new File(System.getProperty("ISF_SVN_DIR")
				+ "/src/ontology/module/vivo/vivo.owl"));

		man3.addIRIMapper(new AutoIRIMapper(new File(System.getProperty("ISF_SVN_DIR")
				+ "/src/ontology"), true));
		fullArg = man3.loadOntologyFromOntologyDocument(new File(System.getProperty("ISF_SVN_DIR")
				+ "/src/ontology/arg.owl"));

		vivoDep = man3.loadOntologyFromOntologyDocument(new File(System.getProperty("ISF_SVN_DIR")
				+ "/src/ontology/app-views/vivo/vivo-deprecated.owl"));

//		System.out.println(vivo.getTBoxAxioms(true).size());

		vivoApp = man2.loadOntologyFromOntologyDocument(new File(System.getProperty("ISF_SVN_DIR")
				+ "/src/ontology/app-views/vivo/vivo-app.owl"));
//		System.out.println(vivoApp.getTBoxAxioms(true).size());

//		// don't clear since there will be manual edits of this file.
//		man2.removeAxioms(vivoApp, vivoApp.getAxioms());

		Set<OWLAxiom> origAxioms = new HashSet<OWLAxiom>();
		Set<OWLAxiom> newAxioms = new HashSet<OWLAxiom>();

		for (OWLOntology o : vivoOrig.getImportsClosure()) {
			origAxioms.addAll(o.getAxioms());
		}

		for (OWLOntology o : vivo.getImportsClosure()) {
			newAxioms.addAll(o.getAxioms());
		}

		for (OWLAxiom a : origAxioms) {

			// if(a.getSignature().contains(man2.getOWLDataFactory().getOWLClass(IRI.create("http://purl.org/ontology/bibo/DocumentStatus")))){
			// System.out.println("found");
			// }

			if (!newAxioms.contains(a) && !vivoDep.getAxioms().contains(a)) {
				man2.addAxiom(vivoApp, a);
				Set<OWLEntity> entities = a.getSignature();
				for (OWLEntity e : entities) {
					man2.addAxiom(vivoApp, man2.getOWLDataFactory().getOWLDeclarationAxiom(e));
				}
			}
		}

		OWLDataFactory df = man2.getOWLDataFactory();

		for (OWLEntity e : vivoApp.getSignature()) {

			OWLAnnotationAssertionAxiom aa = getLable(e);
			if (aa != null) {
				man2.addAxiom(vivoApp, aa);
			}
			if (!vivo.containsEntityInSignature(e, true)) {
				if (e instanceof OWLClass) {
					man2.addAxiom(
							vivoApp,
							df.getOWLSubClassOfAxiom((OWLClass) e,
									df.getOWLClass(IRI.create("http://isf/deprecated_class"))));
				} else if (e instanceof OWLObjectProperty) {
					man2.addAxiom(
							vivoApp,
							df.getOWLSubObjectPropertyOfAxiom((OWLObjectProperty) e,
									df.getOWLObjectProperty(IRI.create("http://isf/deprecated_op"))));

				} else if (e instanceof OWLDataProperty) {
					man2.addAxiom(
							vivoApp,
							df.getOWLSubDataPropertyOfAxiom((OWLDataProperty) e,
									df.getOWLDataProperty(IRI.create("http://isf/deprecated_dp"))));
				}
			} else {

				if (e instanceof OWLClass) {
					man2.removeAxiom(
							vivoApp,
							df.getOWLSubClassOfAxiom((OWLClass) e,
									df.getOWLClass(IRI.create("http://isf/deprecated_class"))));
				} else if (e instanceof OWLObjectProperty) {
					man2.removeAxiom(
							vivoApp,
							df.getOWLSubObjectPropertyOfAxiom((OWLObjectProperty) e,
									df.getOWLObjectProperty(IRI.create("http://isf/deprecated_op"))));

				} else if (e instanceof OWLDataProperty) {
					man2.removeAxiom(
							vivoApp,
							df.getOWLSubDataPropertyOfAxiom((OWLDataProperty) e,
									df.getOWLDataProperty(IRI.create("http://isf/deprecated_dp"))));
				}

			}
		}

		System.out.println(vivoApp.getTBoxAxioms(true).size());
		man2.saveOntology(vivoApp);
	}

	OWLAnnotationAssertionAxiom getLable(OWLEntity entity) {

		Set<OWLAnnotationAssertionAxiom> axioms = new HashSet<OWLAnnotationAssertionAxiom>();
		for (OWLOntology o : fullArg.getImportsClosure()) {
			axioms.addAll(o.getAnnotationAssertionAxioms(entity.getIRI()));
		}
		for (OWLAnnotationAssertionAxiom a : axioms) {
			if (a.getAnnotation().getProperty().getIRI().equals(OWLRDFVocabulary.RDFS_LABEL.getIRI())) {
				return a;
			}
		}

		return null;
	}

}
