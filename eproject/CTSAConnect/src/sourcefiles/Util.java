package sourcefiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

public class Util {

	static OWLDataFactory df = OWLManager.getOWLDataFactory();

	public static Set<OWLClass> getTopClasses(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				Util.class.getResourceAsStream(fileName)));
		Set<OWLClass> classes = new HashSet<OWLClass>();
		String line;
		while ((line = br.readLine()) != null) {
			classes.add(df.getOWLClass(IRI.create(line)));
		}
		return classes;
	}

	public static Set<OWLClass> getSubs(OWLOntology ontology, OWLClass clazz) {
		Set<OWLClass> classes = new LinkedHashSet<OWLClass>();
		Set<OWLClass> seen = new LinkedHashSet<OWLClass>();
		getSubsRecursive(ontology, clazz, classes, seen);
		return classes;
	}

	private static void getSubsRecursive(OWLOntology ontology, OWLClass clazz, Set<OWLClass> results,
			Set<OWLClass> seen) {
		if (seen.contains(clazz))
			return;
		seen.add(clazz);

		Set<OWLAxiom> axioms = ontology.getReferencingAxioms(clazz, true);

		for (OWLAxiom a : axioms) {
			if (a instanceof OWLSubClassOfAxiom) {
				OWLSubClassOfAxiom suba = (OWLSubClassOfAxiom) a;
				Set<OWLClass> parentClasses = getTopClassesInIntersectionExpression(suba.getSuperClass());
				Set<OWLClass> childClasses = getTopClassesInUnionExpression(suba.getSubClass());

				// if one of the parents is this class
				if (parentClasses.contains(clazz)) {
//					System.out.println(a);
					for (OWLClass child : childClasses) {
						results.add(child);
						getSubsRecursive(ontology, child, results, seen);
					}
				}
			} else if (a instanceof OWLEquivalentClassesAxiom) {
				OWLEquivalentClassesAxiom eq = (OWLEquivalentClassesAxiom) a;
				Set<OWLClassExpression> equalces = eq.getClassExpressionsMinus(clazz);
				for (OWLClassExpression equalce : equalces) {
					Set<OWLClass> equalClasses = getTopClassesInUnionExpression(equalce);
					for (OWLClass equalClass : equalClasses) {
						results.add(equalClass);
						getSubsRecursive(ontology, equalClass, results, seen);
					}
				}
			}
		}
	}

	static public Set<OWLClass> getTopClassesInIntersectionExpression(OWLClassExpression ce) {
		Set<OWLClass> classes = new HashSet<OWLClass>();
		ce = ce.getNNF();
		if (ce instanceof OWLClass) {
			classes.add((OWLClass) ce);
		} else if (ce instanceof OWLObjectIntersectionOf) {
			OWLObjectIntersectionOf oi = (OWLObjectIntersectionOf) ce;
			for (OWLClassExpression ce2 : oi.getOperands()) {
				if (ce2 instanceof OWLClass) {
					classes.add((OWLClass) ce2);
				}
			}
		}
		return classes;
	}

	static public Set<OWLClass> getTopClassesInUnionExpression(OWLClassExpression ce) {
		Set<OWLClass> classes = new HashSet<OWLClass>();
		ce = ce.getNNF();
		if (ce instanceof OWLClass) {
			classes.add((OWLClass) ce);
		} else if (ce instanceof OWLObjectUnionOf) {
			OWLObjectUnionOf oi = (OWLObjectUnionOf) ce;
			for (OWLClassExpression ce2 : oi.getOperands()) {
				if (ce2 instanceof OWLClass) {
					classes.add((OWLClass) ce2);
				}
			}
		}
		return classes;
	}

}
