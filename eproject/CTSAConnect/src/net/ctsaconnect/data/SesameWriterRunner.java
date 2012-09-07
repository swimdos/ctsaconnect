package net.ctsaconnect.data;

import net.ctsaconnect.datasource.DataSource;
import net.ctsaconnect.datasource.OhsuIcd9DataSource;

import org.openrdf.repository.http.HTTPRepository;

public class SesameWriterRunner {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		// create a datasource that reads from the OHSU database and returns simple
		// data objects
		DataSource ds = new OhsuIcd9DataSource();

		// create a new instance data generator
		GenerateInstanceData_New g = new GenerateInstanceData_New(ds);
		// set the base uri
		g.setBaseInstanceUri("http://instance/data/");
		// how many simple objects to encode in a single OWLOntology object
		g.setObjectsPerOntology(1000);

		// create a sesame writer
		SesameWriter w = new SesameWriter();
		// set it's repository, this is a testing repository at ohsu
		HTTPRepository r = new HTTPRepository("http://localhost:8080/sesame", "ctsa-testing-native");
		r.setUsernameAndPassword("user", "password");
		w.setReporitory(r);
		w.init();
		// commit after each OWLOntology is written to the repository
		w.setAutoCommit(true);
		// do not add type assertions such as OWLNamedIndividual, OWLClass, etc.
		w.setAddMissingTypes(false);
		// specify a sesame context
		w.setContextUri("http://test/1");
		// set the istance generator that will provide OWLOntology objects to the
		// writer
		w.setIInstanceGenerator(g);
		// write all data
		w.writeAllInstances();
		// needed if autocommit is false
		w.commit();
		w.close();

		System.out.println("Time: " + (System.currentTimeMillis() - startTime) / 1000 / 60);

	}

}
