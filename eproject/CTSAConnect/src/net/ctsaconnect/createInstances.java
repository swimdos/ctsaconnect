package net.ctsaconnect;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;


/**
 * 
 * @author torniai@ohsum01.ohsu.edu
 *TO DO:
 *
 * - Add parameters (out File source instance file / source ontology)
 * - Refactor some methods
 * - Have the ontology look up existing practitioners (future / optional parameter)
 * - Add support for Mysql table to generate the sampleData
 * - Create a tests with SPARQL queries with the proper result (queries are already in)
 * - modify the clinical-instance.owl with the proper URIs and synch the URIs
 * - Test the generated instances when we have the CPT and ICD owl files.
 * 
 * 
 * Assumption: this is a one shot script now: it assumes to have a clean ontology with no instances.
 * In the future we can add a target ontology where append the data ->  will perform the look up for instances there rather that in the t Arraylists
 * 
 */

public class createInstances {

	
	
	

	private static String encounterClassURI = "http://purl.obolibrary.org/obo/ARG_0000140";
	private static String healthcarepractitionerClassURI="http://purl.obolibrary.org/obo/ARG_0000130";
	private static String patientClassURI = "http://purl.obolibrary.org/obo/ARG_0000051";
	private static String hasdateURI = "http://purl.obolibrary.org/obo/ARG_0000365"; 
	private static String identifierAnnoPropertyURI ="http://purl.obolibrary.org/obo/ARG_0000495";
	private static String orderClassURI = "http://purl.obolibrary.org/obo/ARG_0000006";
	private static String hasspecifiedoutputURL ="http://purl.obolibrary.org/obo/OBI_0000299";
	private static String haspartURI = "http://www.obofoundry.org/ro/ro.owl#has_part";
	private static String baseCPTSUbclassURI = "http://purl.obolibrary.org/obo/arg/cptcode/";
	private static String baseICDSUbclassURI = "http://purl.obolibrary.org/obo/arg/icdcode/";
	private static String diagnosisURL = "http://purl.obolibrary.org/obo/ARG_0000037";
	private static String hasparticipantURL = "http://www.obofoundry.org/ro/ro.owl#has_participant";
	private static IRI diagnosisClassIRI = IRI.create(diagnosisURL);
	private static IRI hasspecifiedoutputIRI = IRI.create(hasspecifiedoutputURL);
	private static IRI haspartIRI = IRI.create(haspartURI);
	private static IRI healthcarepractitionerClassIRI = IRI.create(healthcarepractitionerClassURI);
	private static IRI patientClassIRI = IRI.create(patientClassURI);
	private static IRI encounterCLassIRI = IRI.create(encounterClassURI);
	private static IRI hasdateIRI = IRI.create(hasdateURI);
	private static IRI IdentifierAnnoIRI = IRI.create(identifierAnnoPropertyURI);
	private static IRI orderClassIRI = IRI.create(orderClassURI);
	private static IRI hasparticipantIRI = IRI.create(hasparticipantURL);
	private static IRI datetimeIRI = IRI.create("http://www.w3.org/2001/XMLSchema#dateTime");
	
	

	private static String outFile="./clinical_instances.owl";

	
	private static IRI ontoIRI =  IRI.create("http://purl.obolibrary.org/obo/arg/clinical_instances.owl");

	 
	private static String basicinstanceURI= "http://purl.obolibrary.org/obo/arg/i/";
	
	
	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
	 
		 

		 
		 List<simpleData> testData = new ArrayList<simpleData>();
		 testData.add(new simpleData("1234567", "91120", "",1, 1 ));
		 testData.add(new simpleData("1234567", "", "555.1",4, 1 ));
		 testData.add(new simpleData("1234567", "91120", "",8, 6 ));
		 testData.add(new simpleData("1234568", "91322", "",10, 5 ));
		 Boolean isCPT=false;
		 Boolean isICD=false;
		 
		 /**
		  * The results for the dummy data set should be:
		  *
		  *
		 	Encounter: 13
		 	Patient: 13
		 	Practitioners: 2
		 	CPT 91120 instance = 9
		 	CPT 91322 instances = 10
		 	Diagnoses = 1
		 	Order = 12
		 	
		 	How many CPT 91322 practitioner 1234567 has ordered?
		 	
			SPARQL QUERIES:
			PREFIX ARG: <http://purl.obolibrary.org/obo/>
			PREFIX ARGINST: <http://purl.obolibrary.org/obo/arg/i/>
			SELECT DISTINCT ?codes
			WHERE
			{
			?encounter a ARG:ARG_0000140.
			?encounter <http://www.obofoundry.org/ro/ro.owl#has_participant> ARGINST:1234567.
			?encoounter <http://purl.obolibrary.org/obo/OBI_0000299> ?order.
			?order <http://www.obofoundry.org/ro/ro.owl#has_part> ?codes.
			?codes a <http://purl.obolibrary.org/obo/arg/cptcode/91322>
			}
			
			
			
			
			How many unique patients practitioner 1234567 has visited?
			
			PREFIX ARG: <http://purl.obolibrary.org/obo/>
			PREFIX ARGINST: <http://purl.obolibrary.org/obo/arg/i/>
			SELECT DISTINCT ?patient
			WHERE
			{
			?encounter a ARG:ARG_0000140.
			?encounter <http://www.obofoundry.org/ro/ro.owl#has_participant> ARGINST:1234567.
			?encounter <http://www.obofoundry.org/ro/ro.owl#has_participant> ?patient.
			?patient a ARG:ARG_0000051.
			}
			
			
			ALGORITHM:
			For each  simpledata
		   		if practitioner  !exists 
		   			create instance of practitioner
		   			add practitioner to the arraylist for future lookup
		   	
		   	
		   		
		   		for each unique visit
		   			create patient instance 
		   			create encounter instance
		   			
		   			// Process CPT
		   			if CPT code != ""
		   				create the CPT instance
		   				create the order instance
		   				add the statements
		   					 encoutner_instance has_specified_output order_instance
		   					 order_instance has_part CPT_code_instance
		   					
		   			
		   			// Process ICD 9 
		   			if ICD code !=""
		   				create the ICD code instance
		   				create the diagnosis instance
		   				add the statements
		   					 encoutner_instance has_specified_output diagnosis_instance
		   					 order_instance has_part ICD_code_instance
		   			
		   	if the total occurrences > unique_patients
		   			create  occurrences - unique_patients instances of the code (CPT or ICD)
		   			add statements to the last encounter
		   				 
 */
		
	 
		 OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		 OWLDataFactory df = manager.getOWLDataFactory();
		 OWLOntology onto = manager.createOntology(ontoIRI);
		 
		 // Create the empty arraylist of instances IRIs
		 List<String> practitionerIDSet = new ArrayList<String>();
		 List<String> patientIDSet = new ArrayList<String>();
		 List<String> encounterIDSet = new ArrayList<String>();
		 List<String> orderIDSet = new ArrayList<String>();
		 List<String> CPTCodeIDSet = new ArrayList<String>();
		 List<String> ICDCodeIDSet = new ArrayList<String>();
		 List<String> diagnosisCodeIDSet = new ArrayList<String>();
		 
		 for (int i=0; i<testData.size();i++)
		 {
			 System.out.println("------------------------");
			 System.out.println("Processing row " +(i+1));
			 // Maybe the following can be extracted as a method so that it can be reused 
			 String practitionerID = testData.get(i).practitionerID;
			 // check if the practitioner ID exists
			 if (!practitionerIDSet.contains(practitionerID))
			 {
				 System.out.println("Practitioner doesn't exists.");
				 
				 // Create the Practitioner instance use the ID to create the IRI
				 // Here we can have a function that we want to reuse for all the others but for now let's do this
				 
				IRI practitionerIndividualIRI = IRI.create(basicinstanceURI+practitionerID);
				OWLIndividual practitioner = df.getOWLNamedIndividual(practitionerIndividualIRI); 
				String practitioner_label ="practiotioner_"+practitionerID; 
				
				// Assert the type of Practitioner
				OWLClass practitionerIndividualparent = df.getOWLClass(healthcarepractitionerClassIRI);
				OWLClassAssertionAxiom classAssertion = df.getOWLClassAssertionAxiom(practitionerIndividualparent, practitioner);
				manager.addAxiom(onto, classAssertion);
				
				// Add label to practitioner
				OWLAnnotation labelanno = df.getOWLAnnotation(df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()), df.getOWLLiteral(practitioner_label, "en"));
				OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(practitionerIndividualIRI, labelanno);
				manager.applyChange(new AddAxiom(onto, ax));
				
				// Add the annotation property with the identifier
				OWLAnnotation identifieranno = df.getOWLAnnotation(df.getOWLAnnotationProperty(IdentifierAnnoIRI), df.getOWLLiteral(practitionerID, "en"));
				OWLAxiom axanno = df.getOWLAnnotationAssertionAxiom(practitionerIndividualIRI, identifieranno);
				manager.applyChange(new AddAxiom(onto, axanno));
				
				// Add practitioner to the array list
				 practitionerIDSet.add(practitionerID);
				
			 }
			 
			 	 // Declare the instance of practitioner
			 	IRI practitionerIndividualIRI = IRI.create(basicinstanceURI+practitionerID);
				OWLIndividual practitioner = df.getOWLNamedIndividual(practitionerIndividualIRI); 
				System.out.println("Practitioner already exists.");
				 
				 // Go on and process the rest of the data
				
				 // Get unique patient value
				 int uniquepatients = testData.get(i).uniquePatient;
				 
				 for (int j=0; j<uniquepatients; j++){
					 
					isCPT=false;
					isICD=false;
					// create patient instance (label practitioner_XXXXXX , annotation property identifier) // Currently UUID Need to define an hash function
					String patientID = assignID(patientIDSet);
					IRI patientIndividualIRI = IRI.create(basicinstanceURI+patientID);
					OWLIndividual patient = df.getOWLNamedIndividual(patientIndividualIRI); 

					// Assert the type of Patient
					OWLClass patientIndividualparent = df.getOWLClass(patientClassIRI);
					OWLClassAssertionAxiom classAssertion = df.getOWLClassAssertionAxiom(patientIndividualparent, patient);
					manager.addAxiom(onto, classAssertion);
					
					// Add label to Patient
					String patient_label ="patient_"+patientID; 
					OWLAnnotation labelanno = df.getOWLAnnotation(df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()), df.getOWLLiteral(patient_label, "en"));
					OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(patientIndividualIRI, labelanno);
					manager.applyChange(new AddAxiom(onto, ax));
					
					// Add the annotation property with the identifier to the patient
					OWLAnnotation identifieranno = df.getOWLAnnotation(df.getOWLAnnotationProperty(IdentifierAnnoIRI), df.getOWLLiteral(patientID, "en"));
					OWLAxiom axanno = df.getOWLAnnotationAssertionAxiom(patientIndividualIRI, identifieranno);
					manager.applyChange(new AddAxiom(onto, axanno));
					
					// create an encounter instance (label label encouneter_pract_XXXXX_pat_XXXXX, has_date -> value)
					String encounterID = assignID(encounterIDSet);
					IRI encounterIndividualIRI = IRI.create(basicinstanceURI+encounterID);
					OWLIndividual encounter = df.getOWLNamedIndividual(encounterIndividualIRI); 

					// Assert the type of encounter
					OWLClass encounterIndividualparent = df.getOWLClass(encounterCLassIRI);
					OWLClassAssertionAxiom encounterclassAssertion = df.getOWLClassAssertionAxiom(encounterIndividualparent, encounter);
					manager.addAxiom(onto, encounterclassAssertion);
					
					// Add label to encounter
					String encounter_label ="encounter_practitioner_"+practitionerID+"_patient_"+patientID; 
					OWLAnnotation encounterlabelanno = df.getOWLAnnotation(df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()), df.getOWLLiteral(encounter_label, "en"));
					OWLAxiom encounterax = df.getOWLAnnotationAssertionAxiom(encounterIndividualIRI, encounterlabelanno);
					manager.applyChange(new AddAxiom(onto, encounterax));
					
					// Add the annotation property with the identifier
					OWLAnnotation identifierencounter = df.getOWLAnnotation(df.getOWLAnnotationProperty(IdentifierAnnoIRI), df.getOWLLiteral(encounterID, "en"));
					OWLAxiom encounteraxanno = df.getOWLAnnotationAssertionAxiom(encounterIndividualIRI, identifierencounter);
					manager.applyChange(new AddAxiom(onto, encounteraxanno));
					
					
					// Need to find a way to write the proper value as xsd:DateTime
					OWLDataPropertyAssertionAxiom dataproporaxiom =df.getOWLDataPropertyAssertionAxiom(df.getOWLDataProperty(hasdateIRI), encounter, df.getOWLLiteral("10-2-2012", df.getOWLDatatype(datetimeIRI)));
					manager.applyChange(new AddAxiom(onto, dataproporaxiom));
					
					
					// Add the participants in the encounter: the patientIRI and the practitioner IRI
					// Two statements hasparticipantIRI
					
					OWLObjectProperty has_participant = df.getOWLObjectProperty(hasparticipantIRI);
					OWLObjectPropertyAssertionAxiom participantassertion = df.getOWLObjectPropertyAssertionAxiom(has_participant, encounter, patient);
					manager.applyChange(new AddAxiom(onto, participantassertion));
					OWLObjectPropertyAssertionAxiom participantassertionpractitioner = df.getOWLObjectPropertyAssertionAxiom(has_participant, encounter, practitioner);
					manager.applyChange(new AddAxiom(onto, participantassertionpractitioner));
					
					
					// Get CPTCode value 
					String CPTCode = testData.get(i).CPTCode;
					if (!CPTCode.equals(""))
					{
						isCPT = true;
						System.out.println("Adding CPT Code to the encounter");
						// Create order instance and related axioms
						// create order ID 
						String orderID = assignID(orderIDSet);
						IRI orderIndividualIRI = IRI.create(basicinstanceURI+orderID);
						OWLIndividual order = df.getOWLNamedIndividual(orderIndividualIRI); 

						
						// Assert the type of Order
						OWLClass orderIndividualparent = df.getOWLClass(orderClassIRI);
						OWLClassAssertionAxiom orderclassAssertion = df.getOWLClassAssertionAxiom(orderIndividualparent, order);
						manager.addAxiom(onto, orderclassAssertion);
						
						// Add label to Order
						String order_label ="order_"+orderID; 
						OWLAnnotation orderlabelanno = df.getOWLAnnotation(df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()), df.getOWLLiteral(order_label, "en"));
						OWLAxiom orderax = df.getOWLAnnotationAssertionAxiom(orderIndividualIRI, orderlabelanno);
						manager.applyChange(new AddAxiom(onto, orderax));
						
						
						//Add the relation between the encounter and the orderer
						// encounter instance has_specified_output order instance
						// Using http://purl.obolibrary.org/obo/OBI_0000299 for has_specified output to be changes in the ontology
						
						OWLObjectProperty has_specified_output = df.getOWLObjectProperty(hasspecifiedoutputIRI);
						OWLObjectPropertyAssertionAxiom specoutassertion = df.getOWLObjectPropertyAssertionAxiom(has_specified_output, encounter, order);
						manager.applyChange(new AddAxiom(onto, specoutassertion));
						
						
						// Process CPT Code
						// Create the CPT Instance
						// For the time being I am just creating an instance of order that has part the instance of CPT code that has identifier the identifier
						
						// Create the CPT instance
						String ctpCodeInstanceID = assignID(CPTCodeIDSet);
						IRI cptcodeinstanceIRI = IRI.create(basicinstanceURI+ctpCodeInstanceID);
						OWLIndividual cptCodeInstance = df.getOWLNamedIndividual(cptcodeinstanceIRI); 
						
						// add the type of CPT
						IRI CPTCodeIndividualparentIRI = IRI.create(baseCPTSUbclassURI+CPTCode);
						OWLClass CPTCodeIndividualparent = df.getOWLClass(CPTCodeIndividualparentIRI);
						OWLClassAssertionAxiom CPTinstanceAssertion = df.getOWLClassAssertionAxiom(CPTCodeIndividualparent, cptCodeInstance);
						manager.addAxiom(onto, CPTinstanceAssertion);
			
						// ADD CTP Code label
						String cptcode_label ="cpt_"+ctpCodeInstanceID+"_order_"+orderID; 
						OWLAnnotation cptlabelanno = df.getOWLAnnotation(df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()), df.getOWLLiteral(cptcode_label, "en"));
						OWLAxiom cptlabelax = df.getOWLAnnotationAssertionAxiom(cptcodeinstanceIRI, cptlabelanno);
						manager.applyChange(new AddAxiom(onto, cptlabelax));
						
						// Add the CPT instance part_of the order instance
						OWLObjectProperty has_part = df.getOWLObjectProperty(haspartIRI);
						OWLObjectPropertyAssertionAxiom codeorderassertion = df.getOWLObjectPropertyAssertionAxiom(has_part, order, cptCodeInstance);
						manager.applyChange(new AddAxiom(onto, codeorderassertion));
		
					}
					
					else{
						//System.out.println("CPT Code empty!");
					}
					
					// Get ICD9 Code and process it
					String ICDCode = testData.get(i).ICD9Code;
					
					// replace . with _
					ICDCode = ICDCode.replace(".","_");
					
					if (!ICDCode.equals(""))
					{
						System.out.println("Adding ICD Code to the encounter");
						// Probably have to understand if there was a CPT code or a ICD9 code
						isICD = true;
						// Create diagnosis instance and related axioms
						// create diagnosis ID 
						String diagnosisID = assignID(diagnosisCodeIDSet);
						IRI diagnosisIndividualIRI = IRI.create(basicinstanceURI+diagnosisID);
						OWLIndividual diagnosis = df.getOWLNamedIndividual(diagnosisIndividualIRI); 

						// Assert the type of Diagnosis
						OWLClass orderIndividualparent = df.getOWLClass(diagnosisClassIRI);
						OWLClassAssertionAxiom orderclassAssertion = df.getOWLClassAssertionAxiom(orderIndividualparent, diagnosis);
						manager.addAxiom(onto, orderclassAssertion);
						
						// Add label to Diagnosis
						String diagnosis_label ="diagnosis_"+diagnosisID; 
						OWLAnnotation diagnosislabelanno = df.getOWLAnnotation(df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()), df.getOWLLiteral(diagnosis_label, "en"));
						OWLAxiom orderax = df.getOWLAnnotationAssertionAxiom(diagnosisIndividualIRI, diagnosislabelanno);
						manager.applyChange(new AddAxiom(onto, orderax));
						
						
						//Add the relation between the encounter and the orderer
						// encounter instance has_specified_output order instance
						// Using http://purl.obolibrary.org/obo/OBI_0000299 for has_specified output to be changes in the ontology
						
						OWLObjectProperty has_specified_output = df.getOWLObjectProperty(hasspecifiedoutputIRI);
						OWLObjectPropertyAssertionAxiom specoutassertion = df.getOWLObjectPropertyAssertionAxiom(has_specified_output, encounter, diagnosis);
						manager.applyChange(new AddAxiom(onto, specoutassertion));
						// Process ICD9 Code
						//System.out.println("ICD9 Code not empty!");
						// Create the CPT Instance
						// For the time being I am just creating an instance of order that has part the instance of CPT code that has identifier the identifier
						
						// Create the ICD9 instance
						String icdCodeInstanceID = assignID(ICDCodeIDSet);
						IRI icdcodeinstanceIRI = IRI.create(basicinstanceURI+icdCodeInstanceID);
						OWLIndividual icdCodeInstance = df.getOWLNamedIndividual(icdcodeinstanceIRI); 
						
						// add the type of ICD9
						IRI ICDCodeIndividualparentIRI = IRI.create(baseICDSUbclassURI+ICDCode);
						OWLClass ICDCodeIndividualparent = df.getOWLClass(ICDCodeIndividualparentIRI);
						OWLClassAssertionAxiom ICDinstanceAssertion = df.getOWLClassAssertionAxiom(ICDCodeIndividualparent, icdCodeInstance);
						manager.addAxiom(onto, ICDinstanceAssertion);
			
						// ADD ICD9 Code label
						String icdcode_label ="idc_"+icdCodeInstanceID+"_diagnosis_"+diagnosisID; 
						OWLAnnotation icdlabelanno = df.getOWLAnnotation(df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()), df.getOWLLiteral(icdcode_label, "en"));
						OWLAxiom icdlabelax = df.getOWLAnnotationAssertionAxiom(icdcodeinstanceIRI, icdlabelanno);
						manager.applyChange(new AddAxiom(onto, icdlabelax));
						
						// Add the ICD9 instance part_of the order instance
						OWLObjectProperty has_part = df.getOWLObjectProperty(haspartIRI);
						OWLObjectPropertyAssertionAxiom codeorderassertion = df.getOWLObjectPropertyAssertionAxiom(has_part, diagnosis, icdCodeInstance);
						manager.applyChange(new AddAxiom(onto, codeorderassertion));
						
					}
					
					else{
						//System.out.println("ICD9 Code empty!");
					}
					
					
					
				 }
				 
				 
				 // here attach the remaining data
				// If the total codes are > than the unique patients we append the codes to the last encounter
				
				 int additionalcodes = testData.get(i).codeOccurrences-uniquepatients; // Calculate the numbers of codes to be added
				 if (additionalcodes>0)
				 System.out.println("Adding " + additionalcodes+ " more codes to the encounter.");
				 {
					for (int y=0; y<additionalcodes; y++)
				 	{
						// If the code is a CPT code:
						if (isCPT)
						{
							// Get the last CPT Code 
							String CPTCode = testData.get(i).CPTCode;
							
							// Create a new instance of the code
							String ctpCodeInstanceID = assignID(CPTCodeIDSet);
							IRI cptcodeinstanceIRI = IRI.create(basicinstanceURI+ctpCodeInstanceID);
							OWLIndividual cptCodeInstance = df.getOWLNamedIndividual(cptcodeinstanceIRI); 
							
							// add the type of CPT
							IRI CPTCodeIndividualparentIRI = IRI.create(baseCPTSUbclassURI+CPTCode);
							OWLClass CPTCodeIndividualparent = df.getOWLClass(CPTCodeIndividualparentIRI);
							OWLClassAssertionAxiom CPTinstanceAssertion = df.getOWLClassAssertionAxiom(CPTCodeIndividualparent, cptCodeInstance);
							manager.addAxiom(onto, CPTinstanceAssertion);
				
							// ADD CTP Code label
							// Get the last order ID
							String orderID=orderIDSet.get(orderIDSet.size()-1);
							
							String cptcode_label ="cpt_"+ctpCodeInstanceID+"_order_"+orderID; 
							OWLAnnotation cptlabelanno = df.getOWLAnnotation(df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()), df.getOWLLiteral(cptcode_label, "en"));
							OWLAxiom cptlabelax = df.getOWLAnnotationAssertionAxiom(cptcodeinstanceIRI, cptlabelanno);
							manager.applyChange(new AddAxiom(onto, cptlabelax));
							
							// Add the CPT instance part_of the order instance
							// Get the order instance
							IRI orderIndividualIRI = IRI.create(basicinstanceURI+orderID);
							OWLIndividual order = df.getOWLNamedIndividual(orderIndividualIRI); 
							OWLObjectProperty has_part = df.getOWLObjectProperty(haspartIRI);
							OWLObjectPropertyAssertionAxiom codeorderassertion = df.getOWLObjectPropertyAssertionAxiom(has_part, order, cptCodeInstance);
							manager.applyChange(new AddAxiom(onto, codeorderassertion));
						}
						
						if (isICD){
							// Get the last ICD Code 
							String ICDCode = testData.get(i).ICD9Code;
							ICDCode = ICDCode.replace(".","_");

							// Get the last order ID 
							String diagnosisID=orderIDSet.get(diagnosisCodeIDSet.size()-1);
							
							// Create the ICD9 instance
							String icdCodeInstanceID = assignID(ICDCodeIDSet);
							IRI icdcodeinstanceIRI = IRI.create(basicinstanceURI+icdCodeInstanceID);
							OWLIndividual icdCodeInstance = df.getOWLNamedIndividual(icdcodeinstanceIRI); 
							
							// add the type of ICD9
							IRI ICDCodeIndividualparentIRI = IRI.create(baseICDSUbclassURI+ICDCode);
							OWLClass ICDCodeIndividualparent = df.getOWLClass(ICDCodeIndividualparentIRI);
							OWLClassAssertionAxiom ICDinstanceAssertion = df.getOWLClassAssertionAxiom(ICDCodeIndividualparent, icdCodeInstance);
							manager.addAxiom(onto, ICDinstanceAssertion);
				
							// ADD ICD9 Code label
							String icdcode_label ="idc_"+icdCodeInstanceID+"_diagnosis_"+diagnosisID; 
							OWLAnnotation icdlabelanno = df.getOWLAnnotation(df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()), df.getOWLLiteral(icdcode_label, "en"));
							OWLAxiom icdlabelax = df.getOWLAnnotationAssertionAxiom(icdcodeinstanceIRI, icdlabelanno);
							manager.applyChange(new AddAxiom(onto, icdlabelax));
							
							// Add the ICD9 instance part_of the order instance
							IRI diagnoisiIndividualIRI = IRI.create(basicinstanceURI+diagnosisID);
							OWLIndividual diagnosis = df.getOWLNamedIndividual(diagnoisiIndividualIRI); 
							OWLObjectProperty has_part = df.getOWLObjectProperty(haspartIRI);
							OWLObjectPropertyAssertionAxiom codeorderassertion = df.getOWLObjectPropertyAssertionAxiom(has_part, diagnosis, icdCodeInstance);
							manager.applyChange(new AddAxiom(onto, codeorderassertion));
						}
						
				 	}
				 }
				 
		
	
		 
		 }
		 
		 System.out.println("Practitioners: " +practitionerIDSet.size());
		 System.out.println("Patients: " +patientIDSet.size());
		 System.out.println("Encounter: " + encounterIDSet.size());
		 System.out.println("CPT Codes: " + CPTCodeIDSet.size());
		 System.out.println("ICD Codes: " + ICDCodeIDSet.size());
		 System.out.println("Order: " + orderIDSet.size());
		 System.out.println("Diagnosis: " + diagnosisCodeIDSet.size());
		 
		 
		 // Write the ontology
		 
		 RDFXMLOntologyFormat  rdfxmlFormat = new  RDFXMLOntologyFormat();
			
		 // write onto in the new format
		 File file = new File(outFile);
		 
		 System.out.println("Saving new file: " + outFile);
		 manager.saveOntology(onto, rdfxmlFormat, IRI.create(file.toURI()));
		 System.out.println("Done!");
		 
		 
	 }


	private static String assignID(List<String> IDSet) {
		// TODO Auto-generated method stub 
		
		UUID id = UUID.randomUUID();
		String newID= String.valueOf(id);;
		if (!IDSet.contains(id)){
			// Id is valud
			//System.out.println("Generated a new ID");
			// Remove the dashes for the URI
			//patientID = patientID.replace("-", "");
			IDSet.add(newID);
		}
		
		else {
			System.out.println("Existing Patient ID");
			assignID(IDSet);
		}
		return newID;
	}
	 
}
	 