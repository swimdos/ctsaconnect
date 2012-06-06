package net.ctsaconnect.data;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import net.ctsaconnect.common.Util;

import org.joda.time.DateTime;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import static net.ctsaconnect.common.OWLUtil.*;
import static net.ctsaconnect.common.Const.*;

public class GenerateInstanceData {

	String startDate = "01-01-2009";
	String endDate = "01-01-2010";

	OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	OWLDataFactory df = man.getOWLDataFactory();
	OWLOntology individualsOntology;

	public void generate() throws Exception {

		individualsOntology = man.createOntology(ugetIri(CLINICAL_INSTANCE_ONTOLOGY_URI));
		OWLDataProperty hasDateProperty = uaddDataProperty(individualsOntology, HAS_DATE_DATA_PROPERTY_URI);
		OWLObjectProperty hasParticipant = uaddObjectProperty(individualsOntology, HAS_PARTICIPANT_URI);
		OWLObjectProperty hasPart = uaddObjectProperty(individualsOntology, HAS_PART_URI);
		OWLObjectProperty hasOutput = uaddObjectProperty(individualsOntology, HAS_SPECIFIED_OUTPUT_URI);
		DataSource ds = DataSource.getDataSource();

		// for each data
		for (SimpleDataObject sdo : ds) {
			OWLNamedIndividual practitionerInd = uaddNamedIndividual(individualsOntology, BASE_CLINICAL_INSTANCE_URI + sdo.practitionerID);
			uaddLabel(individualsOntology, practitionerInd, PRACTITIONER_LABEL_PREFIX + sdo.practitionerID);
			uaddClassAssertion(individualsOntology, practitionerInd, ugetOWLClass(HEALTH_PRACTITIONER_CLASS_URI));

			// add all the diagnosis or order individuals to a list
			List<OWLNamedIndividual> diagOrderList = new ArrayList<OWLNamedIndividual>();
			boolean isDiagnosis = false, isOrder = false;

			// the type of code
			if (sdo.ICD9Code != null && !sdo.ICD9Code.trim().equals("")) {
				isOrder = true;
			} else if (sdo.CPTCode != null && !sdo.CPTCode.trim().equals("")) {
				isDiagnosis = true;
			}

			// create the individuals
			for (int i = 0; i < sdo.codeOccurrences; ++i) {
				String wholeId = Util.getRandomId(null);
				OWLNamedIndividual wholeInd = uaddNamedIndividual(individualsOntology, BASE_CLINICAL_INSTANCE_URI + wholeId);
				String partIndId = Util.getRandomId(null);
				OWLNamedIndividual partInd = uaddNamedIndividual(individualsOntology, BASE_CLINICAL_INSTANCE_URI + partIndId);

				if (isDiagnosis) {
					uaddClassAssertion(individualsOntology, wholeInd, ugetOWLClass(DIAGNOSIS_URI));
					uaddLabel(individualsOntology, wholeInd, "diagnosis_" + wholeId);
					uaddClassAssertion(individualsOntology, partInd, ugetOWLClass(BASE_ICD9CM_CLASS_URI + sdo.ICD9Code.replace(".", "_")));
					uaddLabel(individualsOntology, partInd, "icd_" + partIndId + "_diagnosis_" + wholeId);

				} else if (isOrder) {
					uaddClassAssertion(individualsOntology, wholeInd, ugetOWLClass(ORDER_CLASS_URI));
					uaddLabel(individualsOntology, wholeInd, "order_" + wholeId);
					uaddClassAssertion(individualsOntology, partInd, ugetOWLClass(BASE_ICD9CM_CLASS_URI + sdo.CPTCode));
					uaddLabel(individualsOntology, partInd, "cpt_" + partIndId + "_order_" + wholeId);
				}

				uaddObjectAssertion(individualsOntology, hasPart, wholeInd, partInd);
				diagOrderList.add(wholeInd);
			}

			// create encounters
			OWLNamedIndividual encounterInd = null;
			for (int i = 0; i < sdo.uniquePatient; ++i) {
				String patientId = Util.getRandomId(null);
				OWLNamedIndividual patientInd = uaddNamedIndividual(individualsOntology, BASE_CLINICAL_INSTANCE_URI + patientId);
				uaddClassAssertion(individualsOntology, patientInd, ugetOWLClass(PATIENT_CLASS_URI));
				uaddLabel(individualsOntology, patientInd, patientId);
				uaddStringAnnotationAssertion(individualsOntology, patientInd, patientId, IDENTIFIER_ANNOT_PROPERTY_URI);

				// encounter
				String encounterId = Util.getRandomId(null);
				DateTime encounterDate = Util.randomDate(startDate, endDate);
				encounterInd = uaddNamedIndividual(individualsOntology, BASE_CLINICAL_INSTANCE_URI + encounterId);
				uaddClassAssertion(individualsOntology, encounterInd, ugetOWLClass(ENCOUNTER_CLASS_URI));
				// TODO:Shahim change to constants
				uaddLabel(individualsOntology, encounterInd, "encounter_practitioner_" + sdo.practitionerID + "_patient_" + patientId);
				uaddStringAnnotationAssertion(individualsOntology, encounterInd, encounterId, IDENTIFIER_ANNOT_PROPERTY_URI);
				uaddDataAssertion(individualsOntology, encounterInd, hasDateProperty, encounterDate.toString("dd-MM-yyyy"), OWL2Datatype.XSD_DATE_TIME);
				uaddObjectAssertion(individualsOntology, hasParticipant, encounterInd, patientInd);
				uaddObjectAssertion(individualsOntology, hasParticipant, encounterInd, practitionerInd);
				// remove and use one individual from the diagnosis or order list
				uaddObjectAssertion(individualsOntology, hasOutput, encounterInd, diagOrderList.remove(diagOrderList.size() - 1));
			}

			// add the remaining list the the last encounter
			for (OWLNamedIndividual ni : diagOrderList) {
				uaddObjectAssertion(individualsOntology, hasOutput, encounterInd, ni);
			}

		}

		man.saveOntology(individualsOntology, new RDFXMLOntologyFormat(), new FileOutputStream(new File(OWL_FILES_GENERATED_DIR_NAME + File.separator
				+ CLINICAL_INSTANCE_ONTOLOGY_FILE_NAME)));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new GenerateInstanceData().generate();
	}

}
