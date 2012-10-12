package net.ctsaconnect.misc;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.owlapi.XMLCatalogIRIMapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.essaid.owlapi.visitor.AnnotationCollector;

import static net.ctsaconnect.common.Const.*;
import static com.essaid.owlapi.util.OWLQueryUtil.*;
import static com.essaid.owlapi.util.OWLCreateUtil.*;

public class GenerateRefactoringReport {

	public static final String CATALOG_URL = "http://connect-isf.googlecode.com/svn/trunk/src/work-area/catalog-v001.xml";
	public static final String SVN_TRUNK_ROOT = System.getProperty("isf.svn.trunk.root");

	OWLOntology refactOntology = null;
	OWLOntologyManager man = null;
	ManchesterRenderer mr = null;
	StringWriter newWriter = new StringWriter();
	StringWriter approvedWriter = new StringWriter();
	StringWriter newWriterExcel = new StringWriter();
	StringWriter approvedWriterExcel = new StringWriter();

	public static void main(String[] args) throws Exception {
		new GenerateRefactoringReport().generate();

	}

	public void generate() throws Exception {
		man = OWLManager.createOWLOntologyManager();
		mr = ManchesterRenderer.getRenderer(man);

		// System.out.println(SVN_TRUNK_ROOT);
		XMLCatalog catalog = CatalogUtilities.parseDocument(new URL(CATALOG_URL));
		// this is the OWLAPI mapper that can be used to configure a manager to
		// resolve URLs based on the catalog entries.
		XMLCatalogIRIMapper xmlm = new XMLCatalogIRIMapper(catalog);

//@formatter:off	
		
//		System.out.println("http://vivoweb.org/ontology/core is mapped to: "
//				+ xmlm.getDocumentIRI(IRI.create("http://vivoweb.org/ontology/core")));
//		
//		//this loads a local catalog and resolves an ontology URI. The output
//		//should look something like:
//		//http://vivoweb.org/ontology/core/vivo-skos-public-1.5.owl is mapped to:
//		//      file:/C:/shahim/svns/connect-isf-root/trunk/src/ontology/clinical_module/vivo/vivo-skos-public-1.5.owl
//
//		XMLCatalogIRIMapper localXmlm = new XMLCatalogIRIMapper(new File(SVN_TRUNK_ROOT
//				+ "/src/ontology/clinical_module/catalog-v001.xml"));
//		System.out.println("http://vivoweb.org/ontology/core/vivo-skos-public-1.5.owl is mapped to: "
//				+ xmlm.getDocumentIRI(IRI
//						.create("http://vivoweb.org/ontology/core/vivo-skos-public-1.5.owl")));
		

		
		man.addIRIMapper(xmlm);
		refactOntology = man.loadOntologyFromOntologyDocument(new File(SVN_TRUNK_ROOT+"/src/work-area/arg-refactoring.owl"));

		
		// for each class:
		// 		list replacements and possible ones, first the pending ones then the approved ones
		//					- render class name, property, and the value.
		
		// for each axiom:
		//  render the axiom, render the annotation (property and comment)
		//  If there is another annotation on the same axiom that indicates approved/disapproved
		//  
		

		//@formatter:on

		List<OWLClass> classes = new ArrayList(refactOntology.getClassesInSignature());
		Collections.sort(classes);
		for (OWLClass c : classes) {
			writeOwlEntityReport(c);
		}
		classes = null;
		List<OWLObjectProperty> oProperties = new ArrayList<OWLObjectProperty>(
				refactOntology.getObjectPropertiesInSignature());
		Collections.sort(oProperties);
		for (OWLObjectProperty op : oProperties) {
			writeOwlEntityReport(op);
		}
		List<OWLDataProperty> dProperties = new ArrayList<OWLDataProperty>(
				refactOntology.getDataPropertiesInSignature());
		Collections.sort(dProperties);
		for (OWLDataProperty op : dProperties) {
			writeOwlEntityReport(op);
		}

		System.out.println("===  NEW CHANGES  ===\n");
		System.out.println(newWriter.toString());
		System.out.println("===  OLD CHANGES  ===\n");
		System.out.println(approvedWriter.toString());
		// System.out.println("===  NEW CHANGES Excel ===\n");
		System.out.println(newWriterExcel.toString());
		// System.out.println("===  OLD CHANGES  Excel ===\n");
		System.out.println(approvedWriterExcel.toString());
	}

	OWLAnnotationProperty replaces = oucGetAnnotationProperty(REFACT_REPLACES_IRI);
	OWLAnnotationProperty replacedBy = oucGetAnnotationProperty(REFACT_REPLACED_BY_IRI);
	OWLAnnotationProperty mayReplaces = oucGetAnnotationProperty(REFACT_POSSIBLE_REPLACES_IRI);
	OWLAnnotationProperty mayReplacedBy = oucGetAnnotationProperty(REFACT_POSSIBLE_REPLACED_BY_IRI);
	OWLAnnotationProperty reason = oucGetAnnotationProperty(REFACT_REASON_IRI);
	OWLAnnotationProperty comment = oucGetAnnotationProperty(REFACT_COMMENT);
	OWLAnnotationProperty approved = oucGetAnnotationProperty(REFACT_APPROVED_IRI);
	OWLAnnotationProperty added = oucGetAnnotationProperty(REFACT_AXIOM_ADDED_IRI);
	OWLAnnotationProperty removed = oucGetAnnotationProperty(REFACT_AXIOM_REMOVED_IRI);
	OWLAnnotationProperty module = oucGetAnnotationProperty(REFACT_MODULE);

	public void writeOwlEntityReport(OWLEntity entity) throws Exception {

		Set<OWLAnnotationAssertionAxiom> aaxioms = entity.getAnnotationAssertionAxioms(refactOntology);
		// boolean entityLineWritten = false;
		for (OWLAnnotationAssertionAxiom axiom : aaxioms) {
			if (axiom.getProperty().equals(replaces)) {
				writeMapping(entity, axiom, "  Replaces: ");
				// entityLineWritten = true;
			}
			if (axiom.getProperty().equals(mayReplaces)) {
				writeMapping(entity, axiom, "  May replace: ");
				// entityLineWritten = true;
			}
			if (axiom.getProperty().equals(replacedBy)) {
				writeMapping(entity, axiom, "  Replaced by: ");
				// entityLineWritten = true;
			}
			if (axiom.getProperty().equals(mayReplacedBy)) {
				writeMapping(entity, axiom, "  May be replaced by: ");
				// entityLineWritten = true;
			}
		}
		List<OWLAxiom> definingAxioms = new ArrayList<OWLAxiom>();
		if (entity instanceof OWLClass) {
			definingAxioms.addAll(refactOntology.getAxioms((OWLClass) entity));
		}
		if (entity instanceof OWLObjectProperty) {
			definingAxioms.addAll(refactOntology.getAxioms((OWLObjectProperty) entity));
		}
		if (entity instanceof OWLDataProperty) {
			definingAxioms.addAll(refactOntology.getAxioms((OWLDataProperty) entity));
		}
		Collections.sort(definingAxioms);
		// Set<OWLAxiom> references = refactOntology.getaxiom

		StringWriter writer = null;
		StringWriter writerExcel = null;
		for (OWLAxiom axiom : definingAxioms) {
			Set<OWLAnnotation> nested = axiom.getAnnotations();
			if (useAxiom(nested)) {
				boolean multiple = checkMultipleApprove(nested); // TODO
				if (isNew(nested)) {
					writer = newWriter;
					writerExcel = newWriterExcel;
				} else {
					writer = approvedWriter;
					writerExcel = approvedWriterExcel;

				}
				mr.renderOWLObject(entity);
				// if (!entityLineWritten) {
				writer.append(entity.getEntityType().toString() + ": \"" + mr + "\" <" + entity.getIRI()
						+ ">\n");
				writerExcel.append(entity.getEntityType().toString() + "\t" + mr + "\t" + entity.getIRI()
						+ "\t");
				// entityLineWritten = true;
				// }
				mr.clearRenderer();
				mr.renderOWLObject(axiom);
				for (OWLAnnotation a : axiom.getAnnotations(added)) {
					writer.append("  Add axiom: " + mr.toString() + "\n");
					writerExcel.append("Add axiom\t--\t" + mr.toString() + "\t--\t");
					writer.append("  Reason: " + ((OWLLiteral) a.getValue()).getLiteral() + "\n");
					writerExcel.append(((OWLLiteral) a.getValue()).getLiteral() + "\t");
				}
				for (OWLAnnotation a : axiom.getAnnotations(removed)) {
					writer.append("  Remove axiom: " + mr.toString() + "\n");
					writerExcel.append("Remove axiom\t--\t" + mr.toString() + "\t--\t");
					writer.append("  Reason: " + ((OWLLiteral) a.getValue()).getLiteral() + "\n");
					writerExcel.append(((OWLLiteral) a.getValue()).getLiteral() + "\t");
				}
				mr.clearRenderer();
				// writerExcel.append("--\t");
				for (OWLAnnotation a : axiom.getAnnotations(comment)) {
					writer.append("  Comment: " + ((OWLLiteral) a.getValue()).getLiteral() + "\n");
					writerExcel.append(((OWLLiteral) a.getValue()).getLiteral() + " -- ");
				}
				for (OWLAnnotation a : axiom.getAnnotations(approved)) {
					writer.append("  Approval: " + ((OWLLiteral) a.getValue()).getLiteral() + "\n");
					writer.append(((OWLLiteral) a.getValue()).getLiteral() + " -- ");
				}

				for (OWLAnnotation a : entity.getAnnotations(refactOntology, module)) {
					writer.append("  Module: " + ((OWLLiteral) a.getValue()).getLiteral() + "\n");
					writer.append("Module\t" + ((OWLLiteral) a.getValue()).getLiteral() + "\t");
				}
				writer.append("\n");
				writerExcel.append("\n");
			}
		}

	}

	private boolean useAxiom(Set<OWLAnnotation> annotations) {
		boolean use = false;
		for (OWLAnnotation a : annotations) {
			if (a.getProperty().equals(added) || a.getProperty().equals(removed)) {
				use = true;
			}
		}
		return use;
	}

	private boolean isNew(Set<OWLAnnotation> nestedAnnotations) {
		boolean approvedReplacementYes = false;
		boolean approvedReplacementNo = false;
		for (OWLAnnotation nestedA : nestedAnnotations) {
			if (nestedA.getProperty().equals(approved)) {
				if (nestedA.getValue().toString().trim().toLowerCase().startsWith("\"yes")) {
					approvedReplacementYes = true;
				} else if (nestedA.getValue().toString().trim().toLowerCase().startsWith("\"no")) {
					approvedReplacementNo = true;
				} else {
					// System.out.println("WARNING: " + a + " is not yes/no");
				}
			}
		}

		return approvedReplacementNo == false && approvedReplacementYes == false;
	}

	private void writeMapping(OWLEntity entity, OWLAnnotationAssertionAxiom axiom, String predicate)
			throws Exception {
		Set<OWLAnnotation> nestedAnnotations = axiom.getAnnotations();
		checkMultipleApprove(nestedAnnotations);
		OWLAnnotation a = axiom.getAnnotation();
		// Set<OWLEntity> entities =
		// refactOntology.getEntitiesInSignature((IRI)a.getValue());
		// for (OWLEntity e : entities) {
		if (isNew(nestedAnnotations)) {
			writeEntityLine(predicate, entity, a, newWriter, newWriterExcel, nestedAnnotations);
		} else {
			writeEntityLine(predicate, entity, a, approvedWriter, approvedWriterExcel, nestedAnnotations);
		}
		// }
	}

	private boolean checkMultipleApprove(Set<OWLAnnotation> annotations) {
		int count = 0;
		for (OWLAnnotation a : annotations) {
			if (a.getProperty().equals(approved))
				++count;
		}
		if (count > 1) {
			return true;
		}
		return false;
	}

	private void writeEntityLine(String predicate, OWLEntity entity, OWLAnnotation a,
			StringWriter writer, StringWriter writerExcel, Set<OWLAnnotation> nestedAnnotations)
			throws Exception {
		mr.renderOWLObject(entity);
		writer.append(entity.getEntityType().toString() + ": \"" + mr + "\" <" + entity.getIRI()
				+ ">\n");
		writerExcel.append(entity.getEntityType().toString() + "\t" + mr + "\t" + entity.getIRI()
				+ "\t");
		Set<OWLEntity> entities = refactOntology.getEntitiesInSignature((IRI) a.getValue(), true);
		String entityTypes = "";
		for (OWLEntity e : entities) {
			entityTypes += " " + e.getEntityType();
		}
		entityTypes = entityTypes.trim();
		// for (OWLEntity e : entities) {
		mr.clearRenderer();
		mr.renderOWLObject(entity);
		writer.append(predicate + entityTypes + ": \"" + mr + "\" <" + entity.getIRI() + ">\n");
		writerExcel.append(predicate.trim().substring(0, predicate.trim().length() - 1) + "\t"
				+ entityTypes + "\t" + mr + "\t" + entity.getIRI() + "\t");
		// }
		mr.clearRenderer();
		Set<String> lines = new HashSet<String>();
		// now reason
		for (OWLAnnotation aAnnotation : nestedAnnotations) {
			if (aAnnotation.getProperty().equals(reason)) {
				lines.add("  Reason: " + aAnnotation.getValue().toString());
			}
		}
		writeOrderedLines(lines, writer, writerExcel);
		lines.clear();
		// new comments
		for (OWLAnnotation aAnnotation : nestedAnnotations) {
			if (aAnnotation.getProperty().equals(comment)) {
				lines.add("  Comment: " + aAnnotation.getValue().toString());
			}
		}
		writeOrderedLines(lines, writer, writerExcel);
		lines.clear();
		// now approval
		for (OWLAnnotation aAnnotation : nestedAnnotations) {
			if (aAnnotation.getProperty().equals(approved)) {
				lines.add("  Approve: " + aAnnotation.getValue().toString());
			}
		}
		for (OWLAnnotation aAnnotation : entity.getAnnotations(refactOntology, module)) {
			lines.add("  Module: " + aAnnotation.getValue().toString());
		}

		writeOrderedLines(lines, writer, writerExcel);
		lines.clear();
		writer.append("\n");
		writerExcel.append("\n");
	}

	private void writeOrderedLines(Collection<String> strings, StringWriter writer,
			StringWriter writerExcel) {
		List<String> ordered = new ArrayList<String>(strings);
		Collections.sort(ordered);
		// writerExcel.append('"');
		for (String s : ordered) {
			writer.append(s + "\n");
			int trim = s.indexOf(':');
			writerExcel.append(s.substring(trim + 1).trim() + " -- ");
		}
		writerExcel.append("\t");
	}

	public void generateNew() {

	}

	public void generateApproved() {

	}

	public void generateDataPropertyReport() {

	}

	public List<String> getAnnotationAxiomComments(OWLObject object) {
		List<String> comments = new ArrayList<String>();

		if (object instanceof OWLEntity) {
			OWLEntity entity = (OWLEntity) object;
			// entity.getAnnotations(ontology, annotationProperty)
		} else if (object instanceof OWLAxiom) {
			OWLAxiom axiom = (OWLAxiom) object;
		}

		Collections.sort(comments);
		return comments;

	}

}
