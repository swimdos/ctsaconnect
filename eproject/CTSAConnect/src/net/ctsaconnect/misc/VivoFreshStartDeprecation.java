package net.ctsaconnect.misc;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.util.AutoIRIMapper;

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
	OWLOntology vivoOrig;
	OWLOntology vivo;
	OWLOntology vivoDep;

	private void run() throws Exception {
		System.out.println(System.getProperty("ISF_SVN_DIR") + "/src/ontology/imports/vivo");
		manOrig.addIRIMapper(new AutoIRIMapper(new File("C:/s/svns/connect-isf-root/trunk/src/ontology/imports/vivo"), true));
		vivoOrig = manOrig.loadOntologyFromOntologyDocument(new File("C:/s/svns/connect-isf-root/trunk/src/ontology/imports/vivo/vivo-core-public-1.5.owl"));
		System.out.println(vivoOrig.getTBoxAxioms(true).size());

		man2.addIRIMapper(new AutoIRIMapper(new File(System.getProperty("ISF_SVN_DIR")
				+ "/src/ontology/module/vivo"), true));
		vivo = man2.loadOntologyFromOntologyDocument(new File(System.getProperty("ISF_SVN_DIR")
				+ "/src/ontology/module/vivo/vivo.owl"));
		System.out.println(vivo.getTBoxAxioms(true).size());

		vivoDep = man2.loadOntologyFromOntologyDocument(new File(System.getProperty("ISF_SVN_DIR")
				+ "/src/ontology/module/vivo/vivo-deprecated.owl"));
		System.out.println(vivoDep.getTBoxAxioms(true).size());

		man2.removeAxioms(vivoDep, vivoDep.getAxioms());

		Set<OWLAxiom> origAxioms = new HashSet<OWLAxiom>();
		Set<OWLAxiom> newAxioms = new HashSet<OWLAxiom>();

		for (OWLOntology o : vivoOrig.getImportsClosure()) {
			origAxioms.addAll(o.getAxioms());
		}

		for (OWLOntology o : vivo.getImportsClosure()) {
			newAxioms.addAll(o.getAxioms());
		}

		for (OWLAxiom a : origAxioms) {
			if (!newAxioms.contains(a)) {
				man2.addAxiom(vivoDep, a);
			}
		}

		OWLDataFactory df = man2.getOWLDataFactory();
		for (OWLEntity e : vivoDep.getSignature()) {
			if (!vivo.containsEntityInSignature(e, true)) {
				if (e instanceof OWLClass) {
					man2.addAxiom(
							vivoDep,
							df.getOWLSubClassOfAxiom((OWLClass) e,
									df.getOWLClass(IRI.create("http://isf/deprecated_class"))));
				} else if (e instanceof OWLObjectProperty) {
					man2.addAxiom(
							vivoDep,
							df.getOWLSubObjectPropertyOfAxiom((OWLObjectProperty) e,
									df.getOWLObjectProperty(IRI.create("http://isf/deprecated_op"))));

				} else if (e instanceof OWLDataProperty) {
					man2.addAxiom(
							vivoDep,
							df.getOWLSubDataPropertyOfAxiom((OWLDataProperty) e,
									df.getOWLDataProperty(IRI.create("http://isf/deprecated_dp"))));
				}
			}
		}

		System.out.println(vivoDep.getTBoxAxioms(true).size());
		man2.saveOntology(vivoDep);
	}

}
