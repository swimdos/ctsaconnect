package net.ctsaconnect.misc;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxObjectRenderer;

public class ARGPropertySpreadsheet {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new ARGPropertySpreadsheet().run();

	}

	OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	OWLOntology argApp;

	static String inPropertyGroupIri = "http://eagle-i.org/ont/app/1.0/inPropertyGroup";
	static IRI inGroupIri = IRI.create(inPropertyGroupIri);
	static OWLAnnotationProperty inGroupProp = OWLDataFactoryImpl.getInstance()
			.getOWLAnnotationProperty(inGroupIri);

	static String domainConstraint = "http://eagle-i.org/ont/app/1.0/domainConstraint";
	static IRI domainIri = IRI.create(domainConstraint);
	static OWLAnnotationProperty domainProp = OWLDataFactoryImpl.getInstance()
			.getOWLAnnotationProperty(domainIri);

	static String rangeConstraint = "http://eagle-i.org/ont/app/1.0/rangeConstraint";
	static IRI rangeIri = IRI.create(rangeConstraint);
	static OWLAnnotationProperty rangeProp = OWLDataFactoryImpl.getInstance()
			.getOWLAnnotationProperty(rangeIri);

	private void run() throws Exception {
		AutoIRIMapper mapper = new AutoIRIMapper(
				new File("C:/s/svns/isf-new-layout/trunk/src/ontology"), true);
		man.addIRIMapper(mapper);

		argApp = man.loadOntologyFromOntologyDocument(new File(
				"C:/s/svns/isf-new-layout/trunk/src/ontology/app-views/eagle-i/arg-app.owl"));

		List<OWLObjectProperty> ops = new ArrayList<OWLObjectProperty>();
		ArrayList<OWLDataProperty> dps = new ArrayList<OWLDataProperty>();
		final StringWriter sw = new StringWriter();
		List<String> langs = new ArrayList<String>();
		// langs.add("en");
		// langs.add("en-us");
		// langs.add("en");

		List<OWLAnnotationProperty> aps = new ArrayList<OWLAnnotationProperty>();
		OWLAnnotationProperty ap = man.getOWLDataFactory().getOWLAnnotationProperty(
				OWLRDFVocabulary.RDFS_LABEL.getIRI());
		aps.add(ap);

		Map<OWLAnnotationProperty, List<String>> map = new HashMap<OWLAnnotationProperty, List<String>>();
		map.put(ap, langs);

		ShortFormProvider sf = new AnnotationValueShortFormProvider(aps, map, man);

		final ManchesterOWLSyntaxObjectRenderer r = new ManchesterOWLSyntaxObjectRenderer(sw, sf);
		r.setUseWrapping(false);
		r.setUseTabbing(false);

		for (OWLEntity e : argApp.getSignature(true)) {
			if (e instanceof OWLProperty) {
				OWLProperty p = (OWLProperty) e;
				if (getParentProperty(p).size() == 0) {
					if (p instanceof OWLObjectProperty) {
						ops.add((OWLObjectProperty) p);
					} else if (p instanceof OWLDataProperty) {
						dps.add((OWLDataProperty) p);

					}
				}
			}
		}

		List<OWLProperty> properties = new ArrayList<OWLProperty>();
		Collections.sort(ops, new Comparator<OWLProperty>() {
			@Override
			public int compare(OWLProperty o1, OWLProperty o2) {
				o1.accept(r);
				String o1lable = sw.getBuffer().toString();
				sw.getBuffer().setLength(0);

				o2.accept(r);
				String o2lable = sw.getBuffer().toString();
				sw.getBuffer().setLength(0);

				// TODO Auto-generated method stub
				return o1lable.compareTo(o2lable);
			}
		});
		properties.addAll(ops);
		Collections.sort(dps, new Comparator<OWLProperty>() {
			@Override
			public int compare(OWLProperty o1, OWLProperty o2) {
				o1.accept(r);
				String o1lable = sw.getBuffer().toString();
				sw.getBuffer().setLength(0);
				
				o2.accept(r);
				String o2lable = sw.getBuffer().toString();
				sw.getBuffer().setLength(0);
				
				// TODO Auto-generated method stub
				return o1lable.compareTo(o2lable);
			}
		});
		properties.addAll(dps);

		System.out
				.println("NUMBER\tLEVEL\tLABEL\tTYPE\tIRI\tDOMAIN\tRANGE\tANNOTATION_DOMAINS\tANNOTATION_RANGES\tANNOTATION_GROUP");

		for (OWLProperty p : properties) {

			processEntry(++count, level, r, sw, p);

		}

	}

	int count = 0;
	int level = 1;

	void processEntry(int count, int level, ManchesterOWLSyntaxObjectRenderer r, StringWriter sw,
			OWLProperty p) {
		System.out.print(count + "\t" + count + "." + level + "\t");
		p.accept(r);
		System.out.print(sw.getBuffer().toString() + "\t");
		sw.getBuffer().setLength(0);
		System.out.print(p.getEntityType() + "\t");
		System.out.print(p.getIRI() + "\t");
		Set<OWLClassExpression> ces = p.getDomains(argApp.getImportsClosure());
		System.out.print(renderObjects(p.getDomains(argApp.getImportsClosure()), r, sw) + "\t");
		System.out.print(renderObjects(p.getRanges(argApp.getImportsClosure()), r, sw) + "\t");

		// get asserted domains
		List<String> domains = getAnnotationValues(p, domainProp);
		Set<OWLEntity> entiteis = new HashSet<OWLEntity>();
		for (String domain : domains) {
			// System.out.println();
			// System.out.println(domain);
			IRI domainIri = IRI.create(domain);
			entiteis.addAll(argApp.getEntitiesInSignature(domainIri, true));
		}
		System.out.print(renderObjects(entiteis, r, sw) + "\t");

		// get asserted ranges
		List<String> ranges = getAnnotationValues(p, rangeProp);
		entiteis = new HashSet<OWLEntity>();
		for (String range : ranges) {
			// System.out.println();
			// System.out.println(domain);
			IRI rangeIri = IRI.create(range);
			entiteis.addAll(argApp.getEntitiesInSignature(rangeIri, true));
		}
		System.out.print(renderObjects(entiteis, r, sw)+"\t");

		System.out.println(getAnnotationValues(p, inGroupProp));
		
		
		// do subs
		for (OWLProperty sub : getSubProperty(p)) {
			processEntry(this.count, ++this.level, r, sw, sub);
			--this.level;
		}
	}

	List<String> renderObjects(Set<? extends OWLObject> ces, ManchesterOWLSyntaxObjectRenderer r,
			StringWriter sw) {
		List<String> renderings = new ArrayList<String>();
		for (OWLObject ce : ces) {
			ce.accept(r);
			renderings.add(sw.getBuffer().toString());
			sw.getBuffer().setLength(0);

		}
		Collections.sort(renderings);
		return renderings;
	}

	Set<OWLProperty> getParentProperty(OWLProperty property) {
		return property.getSuperProperties(argApp.getImportsClosure());
	}

	List<OWLProperty> getSubProperty(OWLProperty property) {
		List<OWLProperty> properties = new ArrayList<OWLProperty>();
		properties.addAll(property.getSubProperties(argApp.getImportsClosure()));
		Collections.sort(properties);
		return properties;
	}

	List<String> getAnnotationValues(OWLEntity entity, OWLAnnotationProperty annotationProperty) {
		List<String> annotations = new ArrayList<String>();
		for (OWLOntology o : argApp.getImportsClosure()) {
			Set<OWLAnnotation> as = entity.getAnnotations(o, annotationProperty);
			for (OWLAnnotation a : as) {

				if (a.getValue() instanceof OWLLiteral) {
					annotations.add(((OWLLiteral) a.getValue()).getLiteral().toString());
				} else {
					annotations.add(a.getValue().toString());
				}
			}
		}
		Collections.sort(annotations);
		return annotations;
	}

}
