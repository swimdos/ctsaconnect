package net.ctsaconnect.common;

public class Const {

	public static final String ENCOUNTER_CLASS_URI = "http://purl.obolibrary.org/obo/ARG_0000140";
	public static final String HEALTH_PRACTITIONER_CLASS_URI = "http://purl.obolibrary.org/obo/ARG_0000130";
	public static final String PATIENT_CLASS_URI = "http://purl.obolibrary.org/obo/ARG_0000051";
	public static final String HAS_DATE_DATA_PROPERTY_URI = "http://purl.obolibrary.org/obo/ARG_0000365";
	public static final String IDENTIFIER_ANNOT_PROPERTY_URI = "http://purl.obolibrary.org/obo/ARG_0000495";
	public static final String ORDER_CLASS_URI = "http://purl.obolibrary.org/obo/ARG_0000016";
	public static final String HAS_SPECIFIED_OUTPUT_URI = "http://purl.obolibrary.org/obo/OBI_0000299";
	public static final String HAS_PART_URI = "http://www.obofoundry.org/ro/ro.owl#has_part";
	public static final String DIAGNOSIS_URI = "http://purl.obolibrary.org/obo/ARG_0000037";
	public static final String HAS_PARTICIPANT_URI = "http://www.obofoundry.org/ro/ro.owl#has_participant";

	public static final String CPT_BILLING_CODE_URI = "http://purl.obolibrary.org/obo/ARG_0000442";
	public static final String ICD9_BILLING_CODE_URI = "http://purl.obolibrary.org/obo/ARG_0000123";

	public static final String RDFS_BASE_URI = "http://www.w3.org/2000/01/rdf-schema#";

	// annotation properties uris
	public static final String HAS_CODE_ANNOT_PROP_URI = "http://has_code";
	public static final String HAS_CUI_ANNOT_PROP_URI = "http://has_cui";

	// base uris
	public static final String BASE_ICD9CM_ONTOLOGY_URI = "http://purl.obolibrary.org/obo/arg/icdcode/";
	public static final String BASE_ICD9CM_CLASS_URI = BASE_ICD9CM_ONTOLOGY_URI;

	public static final String BASE_CPT_ONTOLOGY_URI = "http://purl.obolibrary.org/obo/arg/cptcode/";
	public static final String BASE_CPT_CLASS_URI = BASE_CPT_ONTOLOGY_URI;

	// ontology file names
	public static final String ICD9CM_ONTOLOGY_FILE_NAME = "icd9cm-subclasses.owl";
	public static final String CPT_ONTOLOGY_FILE_NAME = "cpt-subclasses.owl";

	// ontology iris
	public static final String ICD9CM_ONTOLOGY_URI = BASE_ICD9CM_ONTOLOGY_URI
			+ ICD9CM_ONTOLOGY_FILE_NAME;
	public static final String CPT_ONTOLOGY_URI = BASE_CPT_ONTOLOGY_URI + CPT_ONTOLOGY_FILE_NAME;

	// misc
	public static final String OWL_FILES_GENERATED_DIR_NAME = "OWLFiles_generated";

	// instance data
	public static final String CLINICAL_INSTANCE_ONTOLOGY_FILE_NAME = "clinical_instances.owl";
	public static final String CLINICAL_INSTANCE_ONTOLOGY_URI = "http://purl.obolibrary.org/obo/arg/"
			+ CLINICAL_INSTANCE_ONTOLOGY_FILE_NAME;
	public static final String BASE_CLINICAL_INSTANCE_URI = "http://dev.ohsu.eagle-i.net/i/";
	public static final String PRACTITIONER_LABEL_PREFIX = "practiotioner_";

}
