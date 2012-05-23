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
 * 1) Create unique identifier for all the instances use UUID + hash methods
 * 2) For the moment just explore the creation of steps starting from the simple dates:
 * 	Practitioner ID, CPT Code Value,  Code occurrences,  Number of Unique Patients
 * 3) Have jut the creation of the instances
 * 	- method to create same hash from same ID (for practitioner)
 * 
 * 
 * Assumption: this will be one shot creation now (meaning we won't be appending anything for the moment 
 * to our ontology). In the future we will have the ontology instances file  as an input and we will perform the look up for instances there.
 * 
 * From each row assuming contains the structure in Simple data the algorithm would be:
 * 	 1) Look up if the instance of provider exists.
 * 		if so
 */

public class createInstances {

	
	// Here I define all the URI for the reference classes we will need (currently they are just placeholder theu will change)
	// Encounter
	// Practitioner
	// CPT code
	// ICD 9 Code
	// Patient
	

	private static String encounterClassURI = "http://purl.obolibrary.org/obo/ARG_0000033";
	private static String healthcarepractitionerClassURI="http://purl.obolibrary.org/obo/ARG_0000130";
	private static String patientClassURI = "http://purl.obolibrary.org/obo/ARG_0000051";
	private static String hasdateURI = "http://purl.obolibrary.org/obo/ARG_0000140"; 
	private static String identifierAnnoPropertyURI ="http://purl.obolibrary.org/obo/ARG_0000495";
	private static String orderClassURI = "http://purl.obolibrary.org/obo/ARG_0000006";
	private static String CPTbillingCodeURI = "http://purl.obolibrary.org/obo/ARG_0000442";
	private static String hasspecifiedoutputURL ="http://purl.obolibrary.org/obo/OBI_0000299";
	private static String haspartURI = "http://www.obofoundry.org/ro/ro.owl#has_part";
	private static String baseCPTSUbclassURI = "http://purl.obolibrary.org/obo/arg/cptcode/";
	private static String baseICDSUbclassURI = "http://purl.obolibrary.org/obo/arg/icdcode/";
	private static String hasparticipantURL = "http://www.obofoundry.org/ro/ro.owl#has_participant";
	private static IRI hasspecifiedoutputIRI = IRI.create(hasspecifiedoutputURL);
	private static IRI haspartIRI = IRI.create(haspartURI);
	private static IRI  CPTbillingCodeIRI = IRI.create(CPTbillingCodeURI);
	private static IRI healthcarepractitionerClassIRI = IRI.create(healthcarepractitionerClassURI);
	private static IRI patientClassIRI = IRI.create(patientClassURI);
	private static IRI encounterCLassIRI = IRI.create(encounterClassURI);
	private static IRI hasdateIRI = IRI.create(hasdateURI);
	private static IRI IdentifierAnnoIRI = IRI.create(identifierAnnoPropertyURI);
	private static IRI orderClassIRI = IRI.create(orderClassURI);
	private static IRI hasparticipantIRI = IRI.create(hasparticipantURL);
	
	

	private static String outFile="./clinical_instances.owl";

	
	private static IRI ontoIRI =  IRI.create("http://purl.obolibrary.org/obo/arg/clinical_instances.owl");

	 
	private static String basicinstanceURI= "http://purl.obolibrary.org/obo/arg/i/";
	
	
	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
	 
		 /* For each simpledata
		  * 	Check if practitioner exists (currently implemented as an arraylist)
		  * 	If not exists 
		  * 		create instance of practitioner (label practitioner_XXXXXX annotation property identifier)
		  * 		add practitioner to the arraylist for future lookup
		  * 	
		  * 	
		  * 	else // Process the CPT code
		  * 		 for each unique visits
		  * 			create patient instance (label practitioner_XXXXXX , annotation property identifier) // Currently UUID
		  * 			
		  * 			create an encounter instance (label label encouneter_pract_XXXXX_pat_XXXXX, has_date -> value)
		  * 			
		  * 			// Process CPT
		  * 			if CPT code != ""
		  * 				create the CPT instance (with the proper parent retrieved using the identifier annotation properties) 
		  * 				// Note need to script the creation of a first flat list of classes for CPT / ICD9 / RXnorm (discuss with Shaim)
		  * 				// The label of will be related to the instance of an encounter
		  * 
		  * 				create the order instance
		  * 				add the statements
		  * 					 encoutner_instance has_specified_output order_instance
		  * 					 order_instance has_part CPT_code_instance
		  * 					
		  * 			
		  * 			// Process ICD 9 
		  * 			if ICD code !=""
		  * 				create the ICD code instance (with the proper parent retrieved by a future owl file using the identifier annotation property)
		  * 				
		  * 				create the diagnosis instance
		  * 				add the statements
		  * 					 encoutner_instance has_specified_output diagnosis_instance
		  * 					 order_instance has_part ICD_code_instance
		  * 			
		  * 	 if the total occurrences > unique visits
		  * 	 
		  * 
		  *
		  */
		 // Create a simple data sets ( it will be retrieved by a dataabase)
		 
		 List<simpleData> testData = new ArrayList<simpleData>();
		 testData.add(new simpleData("1234567", "91120", "",1, 1 ));
		// testData.add(new simpleData("1234567", "91120", "",125, 125 ));
		// testData.add(new simpleData("1234567", "", "555.1",200, 147 ));
		// testData.add(new simpleData("1234568", "91322", "",201, 103 ));
		 
		 // create the ontology
		 OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		 OWLDataFactory df = manager.getOWLDataFactory();
		 OWLOntology onto = manager.createOntology(ontoIRI);
		 
		 // Create the empty arraylist of instances IRIs
		 List<String> practitionerIDSet = new ArrayList<String>();
		 List<String> patientIDSet = new ArrayList<String>();
		 List<String> encounterIDSet = new ArrayList<String>();
		 List<String> orderIDSet = new ArrayList<String>();
		 List<String> CPTCodeIDSet = new ArrayList<String>();
		 
		 for (int i=0; i<testData.size();i++)
		 {
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
			 
			 	 // Declare anyay the instance of practictioner
			 	IRI practitionerIndividualIRI = IRI.create(basicinstanceURI+practitionerID);
				OWLIndividual practitioner = df.getOWLNamedIndividual(practitionerIndividualIRI); 
				 System.out.println("Practitioner already exists.");
				 
				 // Go on and process the rest of the data
				
				 // Get unique patient value
				 int uniquepatients = testData.get(i).uniquePatient;
				 
				 for (int j=0; j<uniquepatients; j++){
					 
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
					
					// Add has date Annotation starting
					// create the date value
					//RDFSDatatype xsdDate = owlModel.getRDFSDatatypeByName("xsd:date");
				    //OWLDataProperty dateProperty = owlModel.createOWLDatatypeProperty("dateProperty", xsdDate);
				   // RDFSLiteral dateLiteral = owlModel.createRDFSLiteral("1971-07-06", xsdDate);

					
					// Need to find a way to write the proper value as xsd:DateTime
					OWLDataPropertyAssertionAxiom dataproporaxiom =df.getOWLDataPropertyAssertionAxiom(df.getOWLDataProperty(hasdateIRI), encounter, df.getOWLLiteral("10-2-2012", "xsd:datetime"));
					manager.applyChange(new AddAxiom(onto, dataproporaxiom));
					
					
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
					
					
					//Add the relation between the encounter and the order
					// encounter instance has_specified_output order instance
					// Using http://purl.obolibrary.org/obo/OBI_0000299 for has_specified output to be changes in the ontology
					
					OWLObjectProperty has_specified_output = df.getOWLObjectProperty(hasspecifiedoutputIRI);
					OWLObjectPropertyAssertionAxiom specoutassertion = df.getOWLObjectPropertyAssertionAxiom(has_specified_output, encounter, order);
					manager.applyChange(new AddAxiom(onto, specoutassertion));
					
					
					
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
						// Process CPT Code
						System.out.println("CPT Code not empty!");
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
						System.out.println("CPT Code empty!");
					}
					
					// Get ICD9 Code and process it
					
					
					
					
				 }
				 
				 // Here need to check if the unique patient is less than the total number will need to generate for X times 
				 // where X = total - unique patients
				 // We should definetly extract a method: create instance set
				 
		 System.out.println(patientIDSet);
		 
		 }
		 
		 
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
			System.out.println("Generated a new ID");
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
	 