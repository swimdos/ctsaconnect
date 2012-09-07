package net.ctsaconnect.misc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxObjectRenderer;

/**
 * Generates the arg.owl properties output for the google doc spreadsheet. It
 * needs to be changed to add line breaks between the class descriptions as seen
 * in the online document.
 */
public class PropertiesList {

	OWLOntologyManager man = OWLManager.createOWLOntologyManager();

	OWLOntology ontology;
	// Map<IRI, EntityInfo> entities = new HashMap<IRI,
	// PropertiesList.EntityInfo>();
	OWLOntology merged;
	ManchesterOWLSyntaxObjectRenderer or;
	StringWriter sw;

	private void run() throws Exception {

		// setup rendering
		List<OWLAnnotationProperty> renderingProps = new ArrayList<OWLAnnotationProperty>();
		OWLAnnotationProperty ap = man.getOWLDataFactory().getOWLAnnotationProperty(
				OWLRDFVocabulary.RDFS_LABEL.getIRI());
		renderingProps.add(ap);
		HashMap<OWLAnnotationProperty, List<String>> map = new HashMap<OWLAnnotationProperty, List<String>>();
		ArrayList<String> langs = new ArrayList<String>();
		// langs.add("en");
		// langs.add("en-us");
		// langs.add("");
		map.put(ap, langs);
		AnnotationValueShortFormProvider sfp = new AnnotationValueShortFormProvider(renderingProps,
				map, man);
		sw = new StringWriter();
		or = new ManchesterOWLSyntaxObjectRenderer(sw, sfp);
		or.setUseWrapping(false);
		or.setUseTabbing(false);
		AutoIRIMapper am = new AutoIRIMapper(new File(System.getProperty("ISF_SVN")), true);

		man.clearIRIMappers();
		man.addIRIMapper(new OWLOntologyIRIMapper() {

			@Override
			public IRI getDocumentIRI(IRI ontologyIRI) {
				System.out.println("Called with:" + ontologyIRI);
				return null;
			}
		});
		man.addIRIMapper(new SimpleIRIMapper(
				IRI.create("http://vivoweb.org/ontology/core"),
				IRI.create(new File(
						"C:/shahim/svns/connect-isf-root/trunk/src/ontology/imports/vivo/vivo-core-public-1.5.owl")
						.toURI())));
		man.addIRIMapper(new SimpleIRIMapper(IRI
				.create("http://vivoweb.org/ontology/core/bfo-extension"), IRI.create(new File(
				"C:/shahim/svns/connect-isf-root/trunk/src/ontology/imports/vivo/mapping/vivo-bfo-1.5.owl")
				.toURI())));

		// //////////////////

		man.addIRIMapper(new SimpleIRIMapper(IRI
				.create("http://vivoweb.org/ontology/core/vivo-bibo-public-1.5.owl"), IRI.create(new File(
				"C:/shahim/svns/connect-isf-root/trunk/src/ontology/imports/vivo/vivo-bibo-public-1.5.owl")
				.toURI())));

		man.addIRIMapper(new SimpleIRIMapper(
				IRI.create("http://vivoweb.org/ontology/core/vivo-fabio-public-1.5.owl"),
				IRI.create(new File(
						"C:/shahim/svns/connect-isf-root/trunk/src/ontology/imports/vivo/vivo-fabio-public-1.5.owl")
						.toURI())));

		man.addIRIMapper(new SimpleIRIMapper(
				IRI.create("http://vivoweb.org/ontology/core/geopolitical.tbox.ver1.1-11-18-11.owl"),
				IRI.create(new File(
						"C:/shahim/svns/connect-isf-root/trunk/src/ontology/imports/vivo/geopolitical.tbox.ver1.1-11-18-11.owl")
						.toURI())));

		man.addIRIMapper(new SimpleIRIMapper(IRI
				.create("http://vivoweb.org/ontology/core/documentStatus.owl"), IRI.create(new File(
				"C:/shahim/svns/connect-isf-root/trunk/src/ontology/imports/vivo/documentStatus.owl")
				.toURI())));

		man.addIRIMapper(new SimpleIRIMapper(IRI
				.create("http://vivoweb.org/ontology/core/vivo-pws-public-1.5.owl"), IRI.create(new File(
				"C:/shahim/svns/connect-isf-root/trunk/src/ontology/imports/vivo/vivo-pws-public-1.5.owl")
				.toURI())));

		man.addIRIMapper(new SimpleIRIMapper(IRI
				.create("http://vitro.mannlib.cornell.edu/ns/vitro/0.7"), IRI.create(new File(
				"C:/shahim/svns/connect-isf-root/trunk/src/ontology/imports/vitro-0.7.owl").toURI())));

		man.addIRIMapper(new SimpleIRIMapper(IRI
				.create("http://vivoweb.org/ontology/core/vivo-skos-public-1.5.owl"), IRI.create(new File(
				"C:/shahim/svns/connect-isf-root/trunk/src/ontology/imports/vivo/vivo-skos-public-1.5.owl")
				.toURI())));

		man.addIRIMapper(new SimpleIRIMapper(
				IRI.create("http://vivoweb.org/ontology/core/vivo-event-public-1.5.owl"),
				IRI.create(new File(
						"C:/shahim/svns/connect-isf-root/trunk/src/ontology/imports/vivo/vivo-event-public-1.5.owl")
						.toURI())));

		man.addIRIMapper(new SimpleIRIMapper(
				IRI.create("http://vivoweb.org/ontology/core/vivo-dcelements-public-1.5.owl"),
				IRI.create(new File(
						"C:/shahim/svns/connect-isf-root/trunk/src/ontology/imports/vivo/vivo-dcelements-public-1.5.owl")
						.toURI())));

		man.addIRIMapper(new SimpleIRIMapper(
				IRI.create("http://vivoweb.org/ontology/core/vivo-dcterms-public-1.5.owl"),
				IRI.create(new File(
						"C:/shahim/svns/connect-isf-root/trunk/src/ontology/imports/vivo/vivo-dcterms-public-1.5.owl")
						.toURI())));

		man.addIRIMapper(new SimpleIRIMapper(IRI
				.create("http://vivoweb.org/ontology/core/vivo-foaf-public-1.5.owl"), IRI.create(new File(
				"C:/shahim/svns/connect-isf-root/trunk/src/ontology/imports/vivo/vivo-foaf-public-1.5.owl")
				.toURI())));

		man.addIRIMapper(new SimpleIRIMapper(
				IRI.create("http://vivoweb.org/ontology/core/dateTimeValuePrecision.owl"),
				IRI.create(new File(
						"C:/shahim/svns/connect-isf-root/trunk/src/ontology/imports/vivo/dateTimeValuePrecision.owl")
						.toURI())));

		man.addIRIMapper(new SimpleIRIMapper(IRI
				.create("http://vivoweb.org/ontology/core/vivo-c4o-public-1.5.owl"), IRI.create(new File(
				"C:/shahim/svns/connect-isf-root/trunk/src/ontology/imports/vivo/vivo-c4o-public-1.5.owl")
				.toURI())));

		man.addIRIMapper(am);
		// System.out.println(System.getProperties());
		ontology = man.loadOntologyFromOntologyDocument(new File(System.getProperty("ISF_SVN")
				+ "/arg.owl"));
		merged = man.createOntology();
		for (OWLOntology o : ontology.getImportsClosure()) {
			man.addAxioms(merged, o.getAxioms());
		}
		ontology = merged;
		man.saveOntology(merged, new FileOutputStream(new File("arg-merged.owl")));

		System.out.println(ontology.getImportsClosure());
		List<OWLEntity> properties = new ArrayList<OWLEntity>();
		// properties.addAll(ontology.getAnnotationPropertiesInSignature());
		properties.addAll(ontology.getObjectPropertiesInSignature(true));
		properties.addAll(ontology.getDataPropertiesInSignature(true));
		for (OWLEntity op : properties) {
			if (op instanceof OWLObjectProperty) {
				OWLObjectProperty op1 = (OWLObjectProperty) op;
				op1.accept(or);
				or.flush();
				sw.getBuffer().delete(0, sw.getBuffer().length());
				String path = getSuperPath(op1);
				System.out.print(path.replace('\n', ' '));
				System.out.print("|" + op1.getIRI() + "|" + op1.getEntityType().getName() + "|");
				System.out.print('"');
				for (OWLClassExpression ce : op1.getDomains(ontology.getImportsClosure())) {
					ce.accept(or);
					or.flush();
					System.out.print(sw.getBuffer().toString().replace('\n', ' '));
					sw.getBuffer().delete(0, sw.getBuffer().length());
					System.out.print(";" + (char) 10);
				}
				System.out.print('"');
				System.out.print("|");
				System.out.print('"');
				for (OWLClassExpression ce : op1.getRanges(ontology.getImportsClosure())) {
					ce.accept(or);
					or.flush();
					System.out.print(sw.getBuffer().toString().replace('\n', ' '));
					sw.getBuffer().delete(0, sw.getBuffer().length());
					System.out.print(";" + (char) 10);
				}
				System.out.print('"');
				System.out.print("|");
				Set<OWLClassAxiom> classAxioms = new HashSet<OWLClassAxiom>();
				for (OWLOntology o : ontology.getImportsClosure()) {
					for (OWLLogicalAxiom la : o.getLogicalAxioms()) {
						if (la instanceof OWLClassAxiom && la.getObjectPropertiesInSignature().contains(op1)) {
							classAxioms.add((OWLClassAxiom) la);
						}
					}
				}

				System.out.print('"');
				for (OWLClassAxiom ca : classAxioms) {
					ca.accept(or);
					or.flush();
					System.out.print(sw.getBuffer().toString().replace('\n', ' '));
					sw.getBuffer().delete(0, sw.getBuffer().length());
					System.out.print(";" + (char) 10);
				}
				System.out.print('"');
			}

			if (op instanceof OWLDataProperty) {
				OWLDataProperty op1 = (OWLDataProperty) op;
				op1.accept(or);
				or.flush();
				sw.getBuffer().delete(0, sw.getBuffer().length());
				String path = getSuperPath(op1);
				System.out.print(path.replace('\n', ' '));
				System.out.print("|" + op1.getIRI() + "|" + op1.getEntityType().getName() + "|");
				System.out.print('"');
				for (OWLClassExpression ce : op1.getDomains(ontology.getImportsClosure())) {
					ce.accept(or);
					or.flush();
					System.out.print(sw.getBuffer().toString().replace('\n', ' '));
					sw.getBuffer().delete(0, sw.getBuffer().length());
					System.out.print(";" + (char) 10);
				}
				System.out.print('"');
				System.out.print("|");
				System.out.print('"');
				for (OWLDataRange ce : op1.getRanges(ontology.getImportsClosure())) {
					ce.accept(or);
					or.flush();
					System.out.print(sw.getBuffer().toString().replace('\n', ' '));
					sw.getBuffer().delete(0, sw.getBuffer().length());
					System.out.print(";" + (char) 10);
				}
				System.out.print('"');
				System.out.print("|");
				Set<OWLClassAxiom> classAxioms = new HashSet<OWLClassAxiom>();
				for (OWLOntology o : ontology.getImportsClosure()) {
					for (OWLLogicalAxiom la : o.getLogicalAxioms()) {
						if (la instanceof OWLClassAxiom && la.getDataPropertiesInSignature().contains(op1)) {
							classAxioms.add((OWLClassAxiom) la);
						}
					}
				}

				System.out.print('"');
				for (OWLClassAxiom ca : classAxioms) {
					ca.accept(or);
					or.flush();
					System.out.print(sw.getBuffer().toString().replace('\n', ' '));
					sw.getBuffer().delete(0, sw.getBuffer().length());
					System.out.print(";" + (char) 10);
				}
				System.out.print('"');
			}
			System.out.println();

		}
		IRI i = IRI.create("http://vivoweb.org/ontology/core#addressState");
		ontology.getSignature(true);
		Set<OWLEntity> es = ontology.getEntitiesInSignature(i, true);
		System.out.println(es);

		// for (EntityInfo entityInfo : entities.values()) {
		// // System.out.println(ei.labels + "  " + ei.entity);
		// entityInfo.entity.accept(or);
		// System.out.print("Property: ");
		// or.flush();
		// System.out.print("\tIRI: " + entityInfo.entity.getIRI());
		// System.out.print("\tDomain: " + entityInfo.entity.getIRI());
		//
		// System.out.println();
		// }

	}

	private String getSuperPath(OWLObjectProperty op) throws Exception {
		String path = "";
		Set<OWLSubObjectPropertyOfAxiom> s = merged.getObjectSubPropertyAxiomsForSubProperty(op);

		for (OWLSubObjectPropertyOfAxiom a : s) {
			OWLObjectPropertyExpression ope = a.getSuperProperty();
			if (ope instanceof OWLObjectProperty) {

				path = getSuperPath((OWLObjectProperty) ope);
			}
		}
		op.accept(or);
		or.flush();
		path += sw.getBuffer().toString() + " -> ";
		sw.getBuffer().delete(0, sw.getBuffer().length());
		return path;
	}

	private String getSuperPath(OWLDataProperty op) throws Exception {
		String path = "";
		Set<OWLSubDataPropertyOfAxiom> s = merged.getDataSubPropertyAxiomsForSubProperty(op);

		for (OWLSubDataPropertyOfAxiom a : s) {
			OWLDataPropertyExpression ope = a.getSuperProperty();
			if (ope instanceof OWLDataProperty) {

				path = getSuperPath((OWLDataProperty) ope);
			}
		}
		op.accept(or);
		or.flush();
		path += sw.getBuffer().toString() + " -> ";
		sw.getBuffer().delete(0, sw.getBuffer().length());
		return path;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new PropertiesList().run();

	}

	// public static class EntityInfo {
	// OWLEntity entity;
	// Set<EntityType> types = new HashSet<EntityType>();
	// Set<OWLClassExpression> domains = new HashSet<OWLClassExpression>();
	// Set<OWLClassExpression> ranges = new HashSet<OWLClassExpression>();
	// Set<String> labels = new HashSet<String>();
	//
	// EntityInfo(OWLEntity entity) {
	// this.entity = entity;
	//
	// }
	//
	// @Override
	// public int hashCode() {
	// return entity.hashCode();
	// }
	//
	// @Override
	// public boolean equals(Object obj) {
	// return entity.equals(obj);
	// }
	// }

}
