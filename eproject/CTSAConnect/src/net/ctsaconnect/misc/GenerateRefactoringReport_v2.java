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
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
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

	}

	List<String> getEntityTypes(IRI iri) {
		List<String> types = new ArrayList<String>();
		for (OWLEntity e : refactOntology.getEntitiesInSignature(iri, true)) {
			types.add(e.getEntityType().toString());
		}
		return types;
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

	// AnnotationBean getAnnotationBean(OWLObject object) throws IOException {
	// String value = null;
	// OWLAnnotation a = null;
	// if (object instanceof OWLEntity) {
	// OWLEntity entity = (OWLEntity) object;
	// a = entity.getAnnotations(refactOntology, isfannotation).iterator().next();
	// } else if (object instanceof OWLAxiom) {
	// OWLAxiom axiom = (OWLAxiom) object;
	// a = axiom.getAnnotations(isfannotation).iterator().next();
	// } else {
	// System.err.println("Not entity or axiom in get annotation bean");
	// }
	// value = ((OWLLiteral) a.getValue()).getLiteral();
	// return new AnnotationBean(value);
	// }

	// Set<AnnotationBean> annotationBeans = new HashSet<AnnotationBean>();

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
							System.out.println("Reached data map on line: " + line + " and field: " + field);
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

		//
		// public boolean isAddAxiom() {
		// if (act != null) {
		// return act.equals("add");
		// }
		// return false;
		// }

		public boolean isActSet() {
			if (act == null) {
				return false;
			}
			return act.length() > 0;
		}

		public boolean isUse() {
			return act.equals("use");
		}

		@Override
		public String toString() {

			return "Act:" + act + " Module:" + modules + " Approve:" + approve;
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
