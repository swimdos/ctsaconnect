package net.ctsaconnect.merge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import net.ctsaconnect.common.Const;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.AutoIRIMapper;

public class MergeAll {

	OWLOntology original;
	OWLOntology merged;
	OWLOntologyManager man ;
	OWLOntologyManager man2;

	private void merge() throws OWLOntologyCreationException, OWLOntologyStorageException,
			FileNotFoundException {
		String isfSvnLocation = System.getenv("ISF_SVN");
		AutoIRIMapper mapper = new AutoIRIMapper(new File(isfSvnLocation + "/src/ontology"), true);
		System.out.println(mapper.getOntologyIRIs());

		// merge arg.owl
		man = OWLManager.createOWLOntologyManager();
		man.clearIRIMappers();
		man.addIRIMapper(mapper);
		man2 = OWLManager.createOWLOntologyManager();
		original = man.loadOntologyFromOntologyDocument(new File(isfSvnLocation
				+ "/src/ontology/arg.owl"));
		merged = man2.createOntology(IRI.create(Const.ARG_ONTOLOGY_URI));
		System.out.println(original.getAxiomCount(AxiomType.DECLARATION, true));
		System.out.println(original.getImportsClosure());
		for (OWLOntology o : original.getImportsClosure()) {
			man2.addAxioms(merged, o.getAxioms());
		}
		man2.saveOntology(merged, new FileOutputStream(new File(isfSvnLocation
				+ "/src/ontology-merged/" + Const.ARG_ONTOLOGY_FILE_NAME)));

		// merge clinical_module.owl
		man = OWLManager.createOWLOntologyManager();
		man.clearIRIMappers();
		man.addIRIMapper(mapper);
		man2 = OWLManager.createOWLOntologyManager();
		original = man.loadOntologyFromOntologyDocument(new File(isfSvnLocation
				+ "/src/ontology/clinical_module/clinical_module.owl"));
		merged = man2.createOntology(IRI.create(Const.CM_ONTOLOGY_URI));
		for (OWLOntology o : original.getImportsClosure()) {
			man2.addAxioms(merged, o.getAxioms());
		}
		man2.saveOntology(merged, new FileOutputStream(new File(isfSvnLocation
				+ "/src/ontology-merged/" + Const.CM_ONTOLOGY_FILE_NAME)));
		
		
		// merge clinical_module-no-icd-cpt.owl
		man = OWLManager.createOWLOntologyManager();
		man.clearIRIMappers();
		man.addIRIMapper(mapper);
		man2 = OWLManager.createOWLOntologyManager();
		original = man.loadOntologyFromOntologyDocument(new File(isfSvnLocation
				+ "/src/ontology/clinical_module/clinical_module_no_icd_cpt.owl"));
		merged = man2.createOntology(IRI.create(Const.CM_BASE_URI+"clinical_module_no_icd_cpt.owl"));
		for (OWLOntology o : original.getImportsClosure()) {
			man2.addAxioms(merged, o.getAxioms());
		}
		man2.saveOntology(merged, new FileOutputStream(new File(isfSvnLocation
				+ "/src/ontology-merged/" + "clinical_module_no_icd_cpt.owl")));

	}

	/**
	 * @param args
	 * @throws OWLOntologyCreationException
	 * @throws FileNotFoundException
	 * @throws OWLOntologyStorageException
	 */
	public static void main(String[] args) throws OWLOntologyCreationException,
			OWLOntologyStorageException, FileNotFoundException {
		new MergeAll().merge();

	}
}