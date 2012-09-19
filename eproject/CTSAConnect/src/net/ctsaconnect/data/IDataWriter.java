package net.ctsaconnect.data;

import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntology;

public interface IDataWriter {

	void init();

	void setIInstanceGenerator(IInstanceDataGenerator dataGenerator);

	OWLOntology writeOneInstance() throws IOException, Exception;

	long writeAllInstances() throws IOException, Exception;

	void setAddMissingTypes(boolean add);

	void setAutoCommit(boolean autoCommit) throws IOException;

	void commit() throws IOException;

	void close() throws IOException;

}
