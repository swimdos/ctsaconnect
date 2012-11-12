package net.ctsaconnect.misc;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.ctsaconnect.common.Const;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLRendererException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxObjectRenderer;

public class ManchesterRenderer extends ManchesterOWLSyntaxObjectRenderer {
	StringWriter sw = null;
	private StringWriter stringWriter;

	private ManchesterRenderer(Writer writer, ShortFormProvider entityShortFormProvider) {
		super(writer, entityShortFormProvider);
	}

	public static ManchesterRenderer getRenderer(OWLOntologyManager manager) {

		StringWriter sw = new StringWriter();

		// setup rendering
		List<OWLAnnotationProperty> renderingProps = new ArrayList<OWLAnnotationProperty>();
		OWLAnnotationProperty ap = OWLManager.getOWLDataFactory().getOWLAnnotationProperty(
				IRI.create(Const.REFACT_LABEL));
		renderingProps.add(ap);
		ap = OWLManager.getOWLDataFactory().getOWLAnnotationProperty(
				OWLRDFVocabulary.RDFS_LABEL.getIRI());
		renderingProps.add(ap);
		HashMap<OWLAnnotationProperty, List<String>> map = new HashMap<OWLAnnotationProperty, List<String>>();
		ArrayList<String> langs = new ArrayList<String>();
		// langs.add("en");
		// langs.add("en-us");
		// langs.add("");
		map.put(ap, langs);
		AnnotationValueShortFormProvider sfp = new AnnotationValueShortFormProvider(renderingProps,
				map, manager);

		ManchesterRenderer mr = new ManchesterRenderer(sw, sfp);
		mr.setStringWriter(sw);
		mr.setUseTabbing(false);
		mr.setUseWrapping(false);

		return mr;
	}

	private void setStringWriter(StringWriter stringWriter) {
		this.stringWriter = stringWriter;
	}

	public StringWriter getStringWriter() {
		return stringWriter;
	}

	@Override
	public String toString() {
		return stringWriter.toString();
	}

	public String clearRenderer() {
		String s = stringWriter.toString();
		stringWriter.getBuffer().delete(0, stringWriter.getBuffer().length());
		return s;
	}

	public String renderOWLObject(OWLObject object) throws OWLRendererException {
		object.accept(this);
		this.flush();
		return getStringWriter().toString();
	}
}
