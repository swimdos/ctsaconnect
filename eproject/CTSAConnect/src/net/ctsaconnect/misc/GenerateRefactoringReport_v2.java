package net.ctsaconnect.misc;

import static com.essaid.owlapi.util.OWLCreateUtil.*;
import static net.ctsaconnect.common.Const.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class GenerateRefactoringReport_v2 {

	// TODO
	// add a column for isf_todo annotations
	//

	public static final String CATALOG_URL = "http://connect-isf.googlecode.com/svn/trunk/src/work-area/catalog-v001.xml";
	public static final String SVN_TRUNK_ROOT = System.getProperty("isf.svn.trunk.root");

	OWLOntology refactOntology = null;
	OWLOntologyManager man = null;
	ManchesterRenderer mr = null;
	StringWriter newWriter = new StringWriter();
	StringWriter approvedWriter = new StringWriter();
	StringWriter excelNewWriter = new StringWriter();
	StringWriter excelApprovedWriter = new StringWriter();

	public static void main(String[] args) throws Exception {
		new GenerateRefactoringReport_v2().generate();

	}

	public void generate() throws Exception {
		man = OWLManager.createOWLOntologyManager();

		// System.out.println(SVN_TRUNK_ROOT);
		XMLCatalog catalog = CatalogUtilities.parseDocument(new URL(CATALOG_URL));
		// this is the OWLAPI mapper that can be used to configure a manager to
		// resolve URLs based on the catalog entries.
		XMLCatalogIRIMapper xmlm = new XMLCatalogIRIMapper(catalog);

//@formatter:off	
		

		
		man.addIRIMapper(xmlm);
		// Open the ontology from a local file
		// The SVN_TRUNK_ROOT needs to point to the root of trunk checkout.
		refactOntology = man.loadOntologyFromOntologyDocument(new File(SVN_TRUNK_ROOT+"/src/work-area/arg-refactoring.owl"));
		mr = ManchesterRenderer.getRenderer(man);
		// open the ontology from SVN
		//refactOntology = man.loadOntologyFromOntologyDocument(IRI.create("http://connect-isf.googlecode.com/svn/trunk/src/work-area/arg-refactoring.owl"));

		populateData();
		
		// for each class:
		// 		list replacements and possible ones, first the pending ones then the approved ones
		//					- render class name, property, and the value.
		
		// for each axiom:
		//  render the axiom, render the annotation (property and comment)
		//  If there is another annotation on the same axiom that indicates approved/disapproved
		//  
		

		//@formatter:on

		List<OWLEntity> entities = new ArrayList<OWLEntity>();
		entities.addAll(refactOntology.getClassesInSignature());
		entities.addAll(refactOntology.getObjectPropertiesInSignature());
		entities.addAll(refactOntology.getDataPropertiesInSignature());
		Collections.sort(entities);

		for (OWLEntity e : entities) {
			writeOwlEntityReport(e);
		}

		System.out
				.print("Manual notes\tEntity type\tEntity name\tEntity URI\tChange\tEntity type\tEntity name\t");
		System.out
				.print("Entity URI\tReason\tComment\tApprove\tApproval\tModule\tOriginal annotation\n");
		System.out.println(excelNewWriter.toString());
		// System.out.println("===  OLD CHANGES  Excel ===\n");
		// System.out.println(excelApprovedWriter.toString());
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
	OWLAnnotationProperty isfannotation = oucGetAnnotationProperty(REFACT_ANNOTATION);

	public void writeOwlEntityReport(OWLEntity entity) throws Exception {
		mr.clearRenderer();
		mr.renderOWLObject(entity);
		String entityName = mr.toString();
		Set<OWLAnnotationAssertionAxiom> aaxioms = entity.getAnnotationAssertionAxioms(refactOntology);
		// boolean entityLineWritten = false;
		for (OWLAnnotationAssertionAxiom axiom : aaxioms) {
			IRI replaced;
			IRI replacement;
			List<String> types;
			boolean relacedByFlag = false;
			if (axiom.getProperty().equals(replaces)) {
				replacement = (IRI) axiom.getSubject();
				replaced = (IRI) axiom.getValue();
			} else

			if (axiom.getProperty().equals(replacedBy)) {
				replaced = (IRI) axiom.getSubject();
				replacement = (IRI) axiom.getValue();
				relacedByFlag = true;
			} else {
				continue;
			}

			AnnotationBean bean = pairToBean.get(replaced.toString() + replacement);

			excelNewWriter.append("-\t");
			for (String type : getEntityTypes(entity.getIRI())) {
				excelNewWriter.append(type + " ");
			}
			excelNewWriter.append("\t" + entityName + "\t");
			excelNewWriter.append(entity.getIRI() + "\t");

			if (relacedByFlag) {
				excelNewWriter.append("REPLACED BY\t");
				types = getEntityTypes(replacement);
				for (String s : types) {
					excelNewWriter.append(s + " ");
				}
				excelNewWriter.append("\t");
				OWLEntity e = refactOntology.getEntitiesInSignature(replacement, true).iterator().next();
				mr.clearRenderer();
				mr.renderOWLObject(e);
				excelNewWriter.append(mr.toString() + "\t");
				excelNewWriter.append(replacement + "\t");

			} else {
				excelNewWriter.append("REPLACES\t");
				types = getEntityTypes(replaced);
				for (String s : types) {
					excelNewWriter.append(s + " ");
				}
				excelNewWriter.append("\t");
				OWLEntity e = refactOntology.getEntitiesInSignature(replaced, true).iterator().next();
				mr.clearRenderer();
				mr.renderOWLObject(e);
				excelNewWriter.append(mr.toString() + "\t");
				excelNewWriter.append(replaced + "\t");
			}
			if (bean != null) {

				excelNewWriter.append(bean.getReasons().toString() + "\t");
				excelNewWriter.append(bean.getComments().toString() + "\t");
				excelNewWriter.append(bean.getApprove() + "\t");
				if (bean.isApproveSet()) {
					if (bean.isApproved()) {
						excelNewWriter.append("APPROVED\t");
					} else {
						excelNewWriter.append("NOT_APPROVED\t");
					}
				} else {
					excelNewWriter.append("UNDECIDED\t");
				}

				excelNewWriter.append(bean.getModules().toString() + "\t" + bean.getOriginal() + "\n");
			} else {
				excelNewWriter.append("-\t-\t-\t-\t-\t-\n");
			}

		}

		// do the entity declarations
		Iterator<OWLAnnotation> i = entity.getAnnotations(refactOntology, isfannotation).iterator();
		if (i.hasNext()) {
			String value = ((OWLLiteral) i.next().getValue()).getLiteral();
			AnnotationBean b = new AnnotationBean(value);
			mr.clearRenderer();
			mr.renderOWLObject(entity);
			excelNewWriter.append("-\t");
			for (String type : getEntityTypes(entity.getIRI())) {
				excelNewWriter.append(type + " ");
			}
			excelNewWriter.append("\t" + mr.toString() + "\t");
			excelNewWriter.append(entity.getIRI() + "\t");
			if (b.isUse()) {
				excelNewWriter.append("USE\t");
			} else {
				excelNewWriter.append("DONT_USE\t");
			}
			excelNewWriter.append("\t\t\t");
			excelNewWriter.append(b.getReasons().toString() + "\t");
			excelNewWriter.append(b.getComments().toString() + "\t");
			excelNewWriter.append(b.getApprove() + "\t");
			if (b.isApproveSet()) {
				if (b.isApproved()) {
					excelNewWriter.append("APPROVED\t");
				} else {
					excelNewWriter.append("NOT_APPROVED\t");
				}
			} else {
				excelNewWriter.append("UNDECIDED\t");
			}
			excelNewWriter.append(b.getModules().toString() + "\t");
			excelNewWriter.append(b.getOriginal() + "\n");

		}

		// do the axioms
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

		for (OWLAxiom axiom : definingAxioms) {
			i = axiom.getAnnotations(isfannotation).iterator();
			if (i.hasNext()) {
				String value = ((OWLLiteral) i.next().getValue()).getLiteral();
				AnnotationBean b = new AnnotationBean(value);
				mr.clearRenderer();
				mr.renderOWLObject(entity);
				excelNewWriter.append("-\t");
				for (String type : getEntityTypes(entity.getIRI())) {
					excelNewWriter.append(type + " ");
				}
				excelNewWriter.append("\t" + mr.toString() + "\t");
				excelNewWriter.append(entity.getIRI() + "\t");
				if (b.isUse()) {
					excelNewWriter.append("USE\t");
				} else {
					excelNewWriter.append("DONT_USE\t");
				}
				excelNewWriter.append("\t\t\t");
				excelNewWriter.append(b.getReasons().toString() + "\t");
				excelNewWriter.append(b.getComments().toString() + "\t");
				excelNewWriter.append(b.getApprove() + "\t");
				if (b.isApproveSet()) {
					if (b.isApproved()) {
						excelNewWriter.append("APPROVED\t");
					} else {
						excelNewWriter.append("NOT_APPROVED\t");
					}
				} else {
					excelNewWriter.append("UNDECIDED\t");
				}
				excelNewWriter.append(b.getModules().toString() + "\t");
				excelNewWriter.append(b.getOriginal() + "\n");

			}
		}
		// Set<OWLAxiom> references = refactOntology.getaxiom

		// StringWriter writer = null;
		// StringWriter writerExcel = null;
		// for (OWLAxiom axiom : definingAxioms) {
		// Set<OWLAnnotation> nestedAnnotations = axiom.getAnnotations();
		// if (useAxiom(nestedAnnotations)) {
		// boolean needsPadding = false; // Google spreadsheet ignores successive
		// // tabs with no content in between
		// boolean multiple = checkMultipleApprove(nestedAnnotations); // TODO
		// if (isNew(nestedAnnotations)) {
		// writer = newWriter;
		// writerExcel = excelNewWriter;
		// } else {
		// writer = approvedWriter;
		// writerExcel = excelApprovedWriter;
		//
		// }
		//
		// // manual notes field
		// writerExcel.append("-\t");
		//
		// mr.renderOWLObject(entity);
		// // if (!entityLineWritten) {
		// writer.append(entity.getEntityType().toString() + ": \"" + mr + "\" <" +
		// entity.getIRI()
		// + ">\n");
		// writerExcel.append(entity.getEntityType().toString() + "\t" + mr + "\t" +
		// entity.getIRI()
		// + "\t");
		// // entityLineWritten = true;
		// // }
		// mr.clearRenderer();
		// mr.renderOWLObject(axiom);
		// for (OWLAnnotation a : axiom.getAnnotations(added)) {
		// writer.append("  Add axiom: " + mr.toString() + "\n");
		// writerExcel.append("Add axiom\t-\t" + mr.toString() + "\t-\t");
		// writer.append("  Reason: " + ((OWLLiteral) a.getValue()).getLiteral() +
		// "\n");
		// writerExcel.append(((OWLLiteral) a.getValue()).getLiteral() + "\t");
		// }
		// for (OWLAnnotation a : axiom.getAnnotations(removed)) {
		// writer.append("  Remove axiom: " + mr.toString() + "\n");
		// writerExcel.append("Remove axiom\t-\t" + mr.toString() + "\t-\t");
		// writer.append("  Reason: " + ((OWLLiteral) a.getValue()).getLiteral() +
		// "\n");
		// writerExcel.append(((OWLLiteral) a.getValue()).getLiteral() + "\t");
		// }
		// mr.clearRenderer();
		// // writerExcel.append("-\t");
		// needsPadding = true;
		// for (OWLAnnotation a : axiom.getAnnotations(comment)) {
		// writer.append("  Comment: " + ((OWLLiteral) a.getValue()).getLiteral() +
		// "\n");
		// writerExcel.append(((OWLLiteral) a.getValue()).getLiteral() + " - ");
		// needsPadding = false;
		// }
		// if (needsPadding) {
		// writerExcel.append(" - \t ");
		// } else {
		// writerExcel.append("\t");
		// }
		// needsPadding = true;
		// boolean hasApprove = false;
		// for (OWLAnnotation a : axiom.getAnnotations(approved)) {
		// writer.append("  Approval: " + ((OWLLiteral) a.getValue()).getLiteral() +
		// "\n");
		// writerExcel.append(((OWLLiteral) a.getValue()).getLiteral() + " - ");
		// needsPadding = false;
		// hasApprove = true;
		// }
		// if (needsPadding) {
		// writerExcel.append(" - \t ");
		// } else {
		// writerExcel.append("\t");
		// }
		//
		// if (hasApprove) {
		//
		// if (writerExcel != excelApprovedWriter) {
		// writerExcel.append("NOT-SURE\t");
		// } else {
		// if (approvedReplacementYes) {
		// writerExcel.append("NO\t");
		//
		// } else if (approvedReplacementNo) {
		// writerExcel.append("YES\t");
		//
		// }
		// }
		// } else {
		// writerExcel.append(" - \t");
		// }
		//
		// needsPadding = true;
		// for (OWLAnnotation a : entity.getAnnotations(refactOntology, module)) {
		// writer.append("  Module: " + ((OWLLiteral) a.getValue()).getLiteral() +
		// "\n");
		// writerExcel.append(((OWLLiteral) a.getValue()).getLiteral() + " - ");
		// needsPadding = false;
		// }
		// if (needsPadding) {
		// writerExcel.append(" - \t ");
		// } else {
		// writerExcel.append("\t");
		// }
		// writer.append("\n");
		// writerExcel.append("\n");
		// }
		// }

	}

	List<String> getEntityTypes(IRI iri) {
		List<String> types = new ArrayList<String>();
		for (OWLEntity e : refactOntology.getEntitiesInSignature(iri, true)) {
			types.add(e.getEntityType().toString());
		}
		return types;
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

	boolean approvedReplacementYes = false;
	boolean approvedReplacementNo = false;

	private boolean isNew(Set<OWLAnnotation> nestedAnnotations) {
		for (OWLAnnotation nestedA : nestedAnnotations) {
			if (nestedA.getProperty().equals(approved)) {
				if (((OWLLiteral) nestedA.getValue()).getLiteral().trim().toLowerCase().startsWith("yes")) {
					approvedReplacementYes = true;
				} else if (((OWLLiteral) nestedA.getValue()).getLiteral().trim().toLowerCase()
						.startsWith("no")) {
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
			writeEntityLine(predicate, entity, a, newWriter, excelNewWriter, nestedAnnotations);
		} else {
			writeEntityLine(predicate, entity, a, approvedWriter, excelApprovedWriter, nestedAnnotations);
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
		// first field
		writerExcel.append("-\t");
		mr.clearRenderer();
		mr.renderOWLObject(entity);
		writer.append(entity.getEntityType().toString() + ": \"" + mr + "\" <" + entity.getIRI()
				+ ">\n");
		writerExcel.append(entity.getEntityType().toString() + "\t" + mr + "\t" + entity.getIRI()
				+ "\t");
		IRI objectIri = (IRI) a.getValue();
		Set<OWLEntity> entities = refactOntology.getEntitiesInSignature(objectIri, true);
		String entityTypes = "";
		OWLEntity firstObjectEntity = null;
		for (OWLEntity e : entities) {
			entityTypes += " " + e.getEntityType();
			if (firstObjectEntity == null) {
				firstObjectEntity = e;
			}
		}
		entityTypes = entityTypes.trim();
		// for (OWLEntity e : entities) {
		mr.clearRenderer();
		mr.renderOWLObject(firstObjectEntity);
		writer.append(predicate + entityTypes + ": \"" + mr + "\" <" + objectIri + ">\n");
		writerExcel.append(predicate.trim().substring(0, predicate.trim().length() - 1) + "\t"
				+ entityTypes + "\t" + mr + "\t" + objectIri + "\t");
		// }
		mr.clearRenderer();
		Set<String> lines = new HashSet<String>();
		// now reason
		for (OWLAnnotation aAnnotation : nestedAnnotations) {
			if (aAnnotation.getProperty().equals(reason)) {
				lines.add("  Reason: " + ((OWLLiteral) aAnnotation.getValue()).getLiteral());
			}
		}
		writeOrderedLines(lines, writer, writerExcel);
		lines.clear();
		// new comments
		for (OWLAnnotation aAnnotation : nestedAnnotations) {
			if (aAnnotation.getProperty().equals(comment)) {
				lines.add("  Comment: " + ((OWLLiteral) aAnnotation.getValue()).getLiteral());
			}
		}
		writeOrderedLines(lines, writer, writerExcel);
		lines.clear();
		// now approval
		for (OWLAnnotation aAnnotation : nestedAnnotations) {
			if (aAnnotation.getProperty().equals(approved)) {
				lines.add("  Approve: " + ((OWLLiteral) aAnnotation.getValue()).getLiteral());
			}
		}
		writeOrderedLines(lines, writer, writerExcel);
		lines.clear();
		// now NEW/OLD
		for (OWLAnnotation aAnnotation : nestedAnnotations) {
			if (aAnnotation.getProperty().equals(approved)) {
				String approveComment = ((OWLLiteral) aAnnotation.getValue()).getLiteral().toLowerCase();
				if (approveComment.startsWith("yes")) {
					lines.add("  NEW-OLD: YES");

				} else if (approveComment.startsWith("no")) {
					lines.add("  NEW-OLD: NO");

				} else {
					lines.add("  NEW-OLD: NOT-SURE");

				}
			}
		}
		writeOrderedLines(lines, writer, writerExcel);
		lines.clear();
		for (OWLAnnotation aAnnotation : entity.getAnnotations(refactOntology, module)) {
			lines.add("  Module: " + ((OWLLiteral) aAnnotation.getValue()).getLiteral());
		}

		writeOrderedLines(lines, writer, writerExcel);
		lines.clear();
		writer.append("\n");
		writerExcel.append("\n");
	}

	private void writeOrderedLines(Collection<String> strings, StringWriter writer,
			StringWriter writerExcel) {

		if (strings.size() == 0) {
			writerExcel.append(" - \t");
			return;
		}
		List<String> ordered = new ArrayList<String>(strings);
		Collections.sort(ordered);
		// writerExcel.append('"');
		Iterator<String> i = ordered.iterator();
		while (i.hasNext()) {
			String s = i.next();
			writer.append(s + "\n");
			int trim = s.indexOf(':');
			writerExcel.append(s.substring(trim + 1).trim());
			if (i.hasNext()) {
				writerExcel.append(" - ");
			}
		}
		// for (String s : ordered) {
		// writer.append(s + "\n");
		// int trim = s.indexOf(':');
		// writerExcel.append(s.substring(trim + 1).trim() + " - ");
		// }
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

	void addAnnotationBean(String annotationValue) throws IOException {
		annotationBeans.add(new AnnotationBean(annotationValue));
	}

	Map<IRI, List<IRI>> replacedByMap = new HashMap<IRI, List<IRI>>();
	Map<IRI, List<IRI>> replacesMap = new HashMap<IRI, List<IRI>>();
	Map<String, AnnotationBean> pairToBean = new HashMap<String, GenerateRefactoringReport_v2.AnnotationBean>();

	private void populateData() throws IOException {
		Set<OWLAnnotationAssertionAxiom> axioms = refactOntology
				.getAxioms(AxiomType.ANNOTATION_ASSERTION);
		for (OWLAnnotationAssertionAxiom axiom : axioms) {
			IRI subject;
			IRI object;
			if (axiom.getProperty().equals(replacedBy)) {
				subject = (IRI) axiom.getSubject();
				object = (IRI) axiom.getValue();
				List<IRI> replacements = replacedByMap.get(subject);
				if (replacements == null) {
					replacements = new ArrayList<IRI>();
					replacedByMap.put(subject, replacements);
				}
				replacements.add(object);
				Iterator<OWLAnnotation> i = axiom.getAnnotations(isfannotation).iterator();
				if (i.hasNext()) {
					String value = ((OWLLiteral) i.next().getValue()).getLiteral();
					Object o = pairToBean.put(subject.toString() + object, new AnnotationBean(value));
					if (o != null) {
						System.err.println("There was a value for the IRI+IRI->bean map: " + axiom);
					}

				}
			}
			if (axiom.getProperty().equals(replaces)) {
				subject = (IRI) axiom.getSubject();
				object = (IRI) axiom.getValue();
				List<IRI> replacements = replacesMap.get(subject);
				if (replacements == null) {
					replacements = new ArrayList<IRI>();
					replacesMap.put(subject, replacements);
				}
				replacements.add(object);
			}
			if (axiom.getProperty().equals(isfannotation)) {

			}

		}

	}

	AnnotationBean getAnnotationBean(OWLObject object) throws IOException {
		String value = null;
		OWLAnnotation a = null;
		if (object instanceof OWLEntity) {
			OWLEntity entity = (OWLEntity) object;
			a = entity.getAnnotations(refactOntology, isfannotation).iterator().next();
		} else if (object instanceof OWLAxiom) {
			OWLAxiom axiom = (OWLAxiom) object;
			a = axiom.getAnnotations(isfannotation).iterator().next();
		} else {
			System.err.println("Not entity or axiom in get annotation bean");
		}
		value = ((OWLLiteral) a.getValue()).getLiteral();
		return new AnnotationBean(value);
	}

	Set<AnnotationBean> annotationBeans = new HashSet<AnnotationBean>();

	/**
	 * The Class AnnoationBean.
	 */
	static class AnnotationBean {

		String act = null;
		Map<String, List<String>> data = null;
		List<ModuleBean> modules;
		List<String> reasons;
		List<String> comments;
		String approve = null;
		List<String> types;

		public List<String> getTypes() {
			if (types == null) {
				types = new ArrayList<String>();
			}
			return types;
		}

		public List<String> getReasons() {
			if (reasons == null) {
				reasons = new ArrayList<String>();
			}
			return reasons;
		}

		public List<String> getComments() {
			if (comments == null) {
				comments = new ArrayList<String>();
			}
			return comments;
		}

		public String getApprove() {
			if (approve == null) {
				return "";
			}
			return approve;
		}

		public List<String> getTodos() {
			if (todos == null) {
				todos = new ArrayList<String>();
			}
			return todos;
		}

		List<String> todos;
		private String original = "";

		AnnotationBean(String value) throws IOException {
			original = value.replaceAll("\n", " -- ");
			StringReader sr = new StringReader(value);
			BufferedReader br = new BufferedReader(sr);
			String line;
			boolean error = false;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0) {
					int col = line.indexOf(':');
					if (col > -1) {
						String field = line.substring(0, col).trim().toLowerCase();
						if (!keys.contains(field)) {
							System.err.println("error 1");
							error = true;
						}
						if (field.equals("act")) {
							if (act != null) {
								System.err.println("error 2");
								error = true;
							}
							act = line.substring(col + 1).trim().toLowerCase();
						} else if (field.equals("reason")) {
							getReasons().add(line.substring(col + 1).trim());
						} else if (field.startsWith("com")) {
							getComments().add(line.substring(col + 1).trim());
						} else if (field.equals("todo")) {
							getTodos().add(line.substring(col + 1).trim());
						} else if (field.equals("approve")) {
							if (approve != null) {
								// there needs to be only one approve:
								System.err.println("error 3");
								error = true;
							}
							approve = line.substring(col + 1).trim();
						} else if (field.equals("type")) {
							getTypes().add(line.substring(col + 1).trim());
						} else if (field.startsWith("mod")) {
							getModules().add(new ModuleBean(line.substring(col + 1).trim()));
						} else {
							// do the data map
							List<String> datavalue = data.get(field);
							if (datavalue == null) {
								datavalue = new ArrayList<String>();
								data.put(field, datavalue);
							}
							datavalue.add(line.substring(col + 1).trim());
						}

					} else {
						error = true;
					}
				}
			}

			if (error) {
				System.err.println("Error in populating annotations: " + value);
			}
		}

		public String getOriginal() {
			return original;
		}

		Set<String> keys = new HashSet<String>(Arrays.asList(new String[] { "com", "comment", "mod",
				"module", "reason", "todo", "approve", "type", "act" }));

		public Map<String, List<String>> getData() {
			if (data == null) {
				data = new HashMap<String, List<String>>();
			}
			return data;
		}

		public List<ModuleBean> getModules() {
			if (modules == null) {
				modules = new ArrayList<ModuleBean>();
			}
			return modules;
		}

		public void setModules(List<ModuleBean> modules) {
			this.modules = modules;
		}

		public boolean isApproveSet() {
			if (approve == null) {
				return false;
			}
			return approve.toLowerCase().startsWith("yes") || approve.toLowerCase().startsWith("no");
		}

		public boolean isApproved() {
			return approve.toLowerCase().startsWith("yes");
		}

		public String getAct() {
			if (act == null) {
				return "";
			}
			return act;
		}

		public boolean isAddAxiom() {
			if (act != null) {
				return act.equals("add");
			}
			return false;
		}

		public boolean isUseSet() {
			return act.length() > 0;
		}

		public boolean isUse() {
			return act.equals("use");
		}

	}

	static class ModuleBean {
		String name;
		boolean declareSet = false;
		boolean declare = false;
		boolean approvedSet = false;
		boolean approved = false;

		public String getName() {
			return name;
		}

		public boolean isDeclareSet() {
			return declareSet;
		}

		public boolean isDeclare() {
			return declare;
		}

		public boolean isApprovedSet() {
			return approvedSet;
		}

		public boolean isApproved() {
			return approved;
		}

		ModuleBean(String value) {
			String[] values = value.split(":");
			name = values[0].toLowerCase();
			if (values.length > 1) {
				String d = values[1].toLowerCase();
				if (d.equals("d")) {
					declareSet = true;
					declare = true;
				} else if (d.equals("r")) {
					declareSet = true;
				} else {
					System.out.println("ModuleBean error: " + value);
				}
			}

			if (values.length > 2) {
				String a = values[2].toLowerCase();
				if (a.equals("y")) {
					approvedSet = true;
					approved = true;
				} else if (a.equals("n")) {
					approvedSet = true;
				} else {
					System.out.println("ModuleBean error: " + value);
				}
			}
		}

		@Override
		public String toString() {
			String s = name;
			if (declareSet) {
				if (declare) {
					s += ":d";
				} else {
					s += ":r";
				}

				if (approvedSet) {
					if (approved) {
						s += ":y";
					} else {
						s += ":n";
					}
				}
			}
			return s;
		}
	}

}
