package net.ctsaconnect.misc;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.AutoIRIMapper;

public class ARGPunnedProperties {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new ARGPunnedProperties().run();

	}

	OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	OWLOntology argApp;



	private void run() throws Exception {
		AutoIRIMapper mapper = new AutoIRIMapper(
				new File("C:/s/svns/isf-new-layout/trunk/src/ontology"), true);
		man.addIRIMapper(mapper);

		argApp = man.loadOntologyFromOntologyDocument(new File(
				"C:/s/svns/isf-new-layout/trunk/src/ontology/app-views/eagle-i/arg-app.owl"));

		Set<OWLEntity> entities = argApp.getSignature(true);
		Set<IRI> iris = new HashSet<IRI>();

		for (OWLEntity e : entities) {
			iris.add(e.getIRI());
		}

		for (IRI iri : iris) {
			Set<OWLEntity> entities2 = argApp.getEntitiesInSignature(iri, true);
			if (entities2.size() > 1) {
				for (OWLEntity e : entities2) {
					System.out.println(e + " ==> " + e.getEntityType());
				}
			}
		}

		System.exit(0);

	}


}
