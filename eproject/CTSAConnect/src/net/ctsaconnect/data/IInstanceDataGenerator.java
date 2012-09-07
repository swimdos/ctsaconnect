package net.ctsaconnect.data;

import org.semanticweb.owlapi.model.OWLOntology;

public interface IInstanceDataGenerator {

	OWLOntology getInstanceData() throws Exception;

	void setObjectsPerOntology(int objectCoutnt);

	void setBaseInstanceUri(String baseUri);

}
