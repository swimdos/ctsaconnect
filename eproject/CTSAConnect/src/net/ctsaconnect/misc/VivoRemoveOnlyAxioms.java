package net.ctsaconnect.misc;

import java.io.File;
import java.io.FileOutputStream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.AutoIRIMapper;

public class VivoRemoveOnlyAxioms {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new VivoRemoveOnlyAxioms().run();

	}

	OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	OWLOntology vivo;
	OWLOntology vivoApp;

	private void run() throws Exception {
		AutoIRIMapper mapper = new AutoIRIMapper(new File(
				"C:/s/svns/isf-new-layout/trunk/src/ontology/module/vivo"), true);
		man.addIRIMapper(mapper);

		vivo = man.loadOntologyFromOntologyDocument(new File(
				"C:/s/svns/isf-new-layout/trunk/src/ontology/module/vivo/vivo.owl"));

		vivoApp = man.loadOntologyFromOntologyDocument(new File(
				"C:/s/svns/isf-new-layout/trunk/src/ontology/app-views/vivo/vivo-app.owl"));

		for (OWLOntology o : vivo.getImportsClosure()) {
			for (OWLAxiom axiom : o.getAxioms()) {
				if (axiom instanceof OWLSubClassOfAxiom) {
					OWLSubClassOfAxiom subax = (OWLSubClassOfAxiom) axiom;
					OWLClassExpression ce = subax.getSuperClass();
					if (ce instanceof OWLObjectAllValuesFrom || ce instanceof OWLDataAllValuesFrom) {
						System.out.println(axiom);
						man.removeAxiom(o, axiom);
						man.addAxiom(vivoApp, axiom);
					}
				}
			}
		}

		for (OWLOntology o : vivo.getImportsClosure()) {
			try {
				man.saveOntology(o);
			} catch (Exception e) {
				System.out.println("Couldn't save: " + o.getOntologyID());
			}
		}

		man.saveOntology(vivoApp);
	}

}
