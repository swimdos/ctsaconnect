package net.ctsaconnect.data;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.coode.owlapi.rdf.rdfxml.RDFXMLRenderer;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class SesameWriter implements IDataWriter {

	private Repository repository = null;

	private IInstanceDataGenerator dataGenerator;

	private RepositoryConnection connection;

	private RDFXMLOntologyFormat formatter;

	private URI context;

	private boolean autoCommit;

	public SesameWriter() {
	}

	@Override
	public void init() {
		try {
			repository.initialize();
			this.connection = repository.getConnection();
			this.formatter = new RDFXMLOntologyFormat();
		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	@Override
	public void setAddMissingTypes(boolean add) {
		if (add) {
			formatter.setAddMissingTypes(true);
		} else {
			formatter.setAddMissingTypes(false);
		}

	}

	@Override
	public void commit() throws IOException {
		if (!autoCommit) {
			try {
				connection.commit();
			} catch (RepositoryException e) {
				throw new IOException(e);
			}
		}

	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws IOException {
		this.autoCommit = autoCommit;
		try {
			if (autoCommit) {
				connection.setAutoCommit(true);
			} else {
				connection.setAutoCommit(false);
			}
		} catch (RepositoryException e) {
			throw new IOException(e);
		}

	}

	@Override
	public void close() throws IOException {
		try {
			connection.close();
		} catch (RepositoryException e) {
			throw new IOException(e);
		}

	}

	@Override
	public void setIInstanceGenerator(IInstanceDataGenerator dataGenerator) {
		this.dataGenerator = dataGenerator;

	}

	@Override
	public OWLOntology writeOneInstance() throws Exception {
		OWLOntology o = dataGenerator.getInstanceData();
		if (o != null) {
			writeOntology(o);
		}
		return o;
	}

	@Override
	public long writeAllInstances() throws Exception {
		OWLOntology o;
		int counter = 0;
		while ((o = dataGenerator.getInstanceData()) != null) {
			writeOntology(o);
			++counter;
		}
		return counter;
	}

	public void setContextUri(String uri) {
		this.context = connection.getValueFactory().createURI(uri);
	}

	private void writeOntology(OWLOntology ontology) throws IOException, RDFParseException,
			RepositoryException {
		OWLOntologyManager man = ontology.getOWLOntologyManager();
		man.setOntologyDocumentIRI(ontology, IRI.create("http://ontology.owl"));
		StringWriter writer = new StringWriter(20000);
		RDFXMLRenderer r = new RDFXMLRenderer(ontology.getOWLOntologyManager(), ontology, writer,
				formatter);
		r.render();
		String xml = writer.getBuffer().toString();
		// System.out.println(xml);
		StringReader stringReader = new StringReader(xml);

		connection.add(stringReader, "http://ctsa/base/uri", RDFFormat.RDFXML, context);
		r.render();
		writer.close();
		stringReader.close();
	}

	public void setReporitory(Repository repository) {
		this.repository = repository;

	}
}
