package net.ctsaconnect.data;

import static com.essaid.owlapi.util.OWLAddUtil.*;
import static com.essaid.owlapi.util.OWLCreateUtil.*;
import static net.ctsaconnect.common.Const.*;

import java.util.ArrayList;
import java.util.List;

import net.ctsaconnect.common.Util;
import net.ctsaconnect.datasource.DataSource;
import net.ctsaconnect.datasource.SimpleDataObject;

import org.joda.time.DateTime;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

/**
 * 
 * @author carlo shahim drspeeedo
 * @version 0.2
 * @since 2012-07-19
 */
public class GenerateInstanceData_New implements IInstanceDataGenerator {

	protected String startDate = "01-01-2009";
	protected String endDate = "01-01-2010";
	protected String baseURI = "";

	private OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	private OWLOntology individualsOntology;

	/**
	 * Source of data that is generated into instance data. Should be aggregate
	 * data to use this class
	 */
	protected DataSource ds;

	/**
	 * Allows the creation of if(debug) { System.out.pringln("Message"); } in the
	 * code which can be switched off at will
	 */
	private boolean debug = true; // TODO svwilliams switch this to using a
																// standard logger with a log rate (all, errors,
																// warning, etc.)
	private int objectCount;

	@Override
	public void setObjectsPerOntology(int objectCoutnt) {
		this.objectCount = objectCoutnt;

	}

	@Override
	public void setBaseInstanceUri(String baseUri) {
		this.baseURI = baseUri;
	}

	/**
	 * GenerateInstanceData Constructor
	 * 
	 * Instead of using the test DataSource that is known at the creation of
	 * GenerateInstanceData
	 * 
	 * @param incomingDS
	 *          The datasource that instance data should be generated from
	 */
	public GenerateInstanceData_New(DataSource incomingDS) {
		this.ds = incomingDS;
		ds.iterator();
	}

	long counter = 0;

	/**
	 * generates the fake instance data based on the aggregate dataset that you
	 * provide
	 * 
	 * @throws Exception
	 */
	@Override
	public OWLOntology getInstanceData() throws Exception {
		int recordsPerOntology = 1;
		if (individualsOntology != null) {
			man.removeOntology(individualsOntology);
		}
		// create the ontology object
		individualsOntology = man.createOntology();

		OWLDataProperty hasDateProperty = oucGetDataProperty(HAS_DATE_DATA_PROPERTY_URI);

		OWLObjectProperty hasParticipant = oucGetObjectProperty(HAS_PARTICIPANT_URI);
		OWLObjectProperty hasPart = oucGetObjectProperty(HAS_PART_URI);
		OWLObjectProperty hasOutput = oucGetObjectProperty(HAS_SPECIFIED_OUTPUT_URI);

		boolean ontologyUsed = false;
		// for each data object
		while (!(recordsPerOntology > objectCount) && ds.hasNext()) {
			ontologyUsed = true;
			++counter;
			++recordsPerOntology;
			SimpleDataObject sdo = ds.next();
			// for (SimpleDataObject sdo : this.ds) {

			// practitioner owl individual
			OWLNamedIndividual practitionerInd = oucGetNamedIndividual(baseURI + sdo.practitionerID);
			ouaAddLabelAnnotation(individualsOntology, practitionerInd, PRACTITIONER_LABEL_PREFIX
					+ sdo.practitionerID, null);
			ouaAddClassAssertionAxiom(individualsOntology, oucGetClass(HEALTH_PRACTITIONER_CLASS_URI),
					practitionerInd);

			// add all the diagnosis or order owl individuals to a list
			List<OWLNamedIndividual> diagOrderList = new ArrayList<OWLNamedIndividual>();
			boolean isDiagnosis = false, isOrder = false;

			// the type of code
			if (sdo.ICD9Code != null && !sdo.ICD9Code.trim().equals("")) {
				isDiagnosis = true;
			} else if (sdo.CPTCode != null && !sdo.CPTCode.trim().equals("")) {
				isOrder = true;
			}

			// create the diagnosis/order owl individuals
			for (int i = 0; i < sdo.codeOccurrences; ++i) {
				String wholeId = Util.getRandomId(null);
				// a "whole" is the diagnosis or order information artifact and the
				// "part" is the code reference
				OWLNamedIndividual wholeInd = oucGetNamedIndividual(baseURI + wholeId);
				String partIndId = Util.getRandomId(null);
				OWLNamedIndividual partInd = oucGetNamedIndividual(baseURI + partIndId);

				if (isDiagnosis) {
					ouaAddClassAssertionAxiom(individualsOntology, oucGetClass(DIAGNOSIS_URI), wholeInd);
					ouaAddLabelAnnotation(individualsOntology, wholeInd, "diagnosis_" + wholeId, null);
					ouaAddClassAssertionAxiom(individualsOntology, oucGetClass(BASE_ICD9CM_CLASS_URI
							+ sdo.ICD9Code.replace(".", "_")), partInd);
					ouaAddLabelAnnotation(individualsOntology, partInd, "icd_" + partIndId + "_diagnosis_"
							+ wholeId, null);

				} else if (isOrder) {
					ouaAddClassAssertionAxiom(individualsOntology, oucGetClass(ORDER_CLASS_URI), wholeInd);
					ouaAddLabelAnnotation(individualsOntology, wholeInd, "order_" + wholeId, null);
					ouaAddClassAssertionAxiom(individualsOntology, oucGetClass(BASE_CPT_CLASS_URI
							+ sdo.CPTCode), partInd);
					ouaAddLabelAnnotation(individualsOntology, partInd, "cpt_" + partIndId + "_order_"
							+ wholeId, null);
				}

				ouaAddObjectAssertion(individualsOntology, hasPart, wholeInd, partInd);
				diagOrderList.add(wholeInd);
			}

			// create encounters, one encounter for each patient. This could be
			// changed to create one encounter for each code and instead of adding
			// multiple codes to an encounter, we would add multiple encounters to a
			// patient to "finish" relating all the data to patients.
			OWLNamedIndividual encounterInd = null;
			for (int i = 0; i < sdo.uniquePatient; ++i) {
				String patientId = Util.getRandomId(null);
				OWLNamedIndividual patientInd = oucGetNamedIndividual(baseURI + patientId);
				ouaAddClassAssertionAxiom(individualsOntology, oucGetClass(PATIENT_CLASS_URI), patientInd);
				ouaAddLabelAnnotation(individualsOntology, patientInd, "patient_" + patientId, null);
				ouaAddStringAnnotation(individualsOntology, IDENTIFIER_ANNOT_PROPERTY_URI, patientInd,
						patientId, null);

				// encounter
				String encounterId = Util.getRandomId(null);
				DateTime encounterDate = Util.randomDate(startDate, endDate);
				encounterInd = oucGetNamedIndividual(baseURI + encounterId);
				ouaAddClassAssertionAxiom(individualsOntology, oucGetClass(ENCOUNTER_CLASS_URI),
						encounterInd);
				// TODO:Shahim change to constants
				ouaAddLabelAnnotation(individualsOntology, encounterInd, "encounter_practitioner_"
						+ sdo.practitionerID + "_patient_" + patientId, null);
				ouaAddStringAnnotation(individualsOntology, IDENTIFIER_ANNOT_PROPERTY_URI, encounterInd,
						encounterId, null);
				ouaAddDataAssertion(individualsOntology, hasDateProperty, encounterInd,
						encounterDate.toString("yyyy-MM-dd") + "T00:00:00", OWL2Datatype.XSD_DATE_TIME);
				ouaAddObjectAssertion(individualsOntology, hasParticipant, encounterInd, patientInd);
				ouaAddObjectAssertion(individualsOntology, hasParticipant, encounterInd, practitionerInd);
				// remove and use one individual from the diagnosis or order list
				ouaAddObjectAssertion(individualsOntology, hasOutput, encounterInd,
						diagOrderList.remove(diagOrderList.size() - 1));
			}

			// add the remaining list of codes to the the last encounter
			for (OWLNamedIndividual ni : diagOrderList) {
				ouaAddObjectAssertion(individualsOntology, hasOutput, encounterInd, ni);
			}

			//
		}
		if (this.debug) {
			System.out.print("Instance: " + counter + " with axiom count: ");
		}
		System.out.println(individualsOntology.getAxiomCount());
		if (ontologyUsed) {
			return individualsOntology;
		} else {
			return null;
		}
	}

	/**
	 * @param args
	 */

}
