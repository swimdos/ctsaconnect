package net.ctsaconnect.common;

public class Const {

	// ontologies

	// ARG
	public static final String ARG_ONTOLOGY_FILE_NAME = "arg.owl";
	public static final String ARG_BASE_URI = "http://purl.obolibrary.org/obo/";
	public static final String ARG_ONTOLOGY_URI = ARG_BASE_URI + ARG_ONTOLOGY_FILE_NAME;
	public static final String ARG_ENTITY_URI_PREFIX = ARG_BASE_URI + "ARG_";

	// Clinical module
	public static final String CM_ONTOLOGY_FILE_NAME = "clinical_module.owl";
	public static final String CM_BASE_URI = "http://purl.obolibrary.org/obo/arg/";
	public static final String CM_ONTOLOGY_URI = CM_BASE_URI + CM_ONTOLOGY_FILE_NAME;
	public static final String CM_ENTITY_URI_PREFIX = ARG_BASE_URI + "ARG_";

	// "health care encounter" from OGMS
	public static final String ENCOUNTER_CLASS_URI = "http://purl.obolibrary.org/obo/OGMS_0000097";
	// "health care provider role" from OBI
	public static final String HEALTH_PRACTITIONER_CLASS_URI = "http://purl.obolibrary.org/obo/ARG_0000130";
	// "patient"
	public static final String PATIENT_CLASS_URI = "http://purl.obolibrary.org/obo/ARG_0000051";
	public static final String HAS_DATE_DATA_PROPERTY_URI = "http://purl.obolibrary.org/obo/ARG_0000365";
	// AP to record an identifier for the entity (other than the URI)
	public static final String IDENTIFIER_ANNOT_PROPERTY_URI = "http://purl.obolibrary.org/obo/ARG_0000495";
	public static final String ORDER_CLASS_URI = "http://purl.obolibrary.org/obo/ARG_0000016";
	public static final String HAS_SPECIFIED_OUTPUT_URI = "http://purl.obolibrary.org/obo/OBI_0000299";
	// "diagnosis textual entity"
	public static final String DIAGNOSIS_URI = "http://purl.obolibrary.org/obo/OBI_0000075";

	// property URIs
	public static final String HAS_PART_URI = "http://www.obofoundry.org/ro/ro.owl#has_part";
	public static final String HAS_PARTICIPANT_URI = "http://www.obofoundry.org/ro/ro.owl#has_participant";

	// annotation properties uris
	public static final String HAS_CODE_ANNOT_PROP_URI = "http://purl.obolibrary.org/obo/ARG_0000033";
	public static final String HAS_CUI_ANNOT_PROP_URI = "http://purl.obolibrary.org/obo/ARG_0000034";
	public static final String HAS_DEF_ANNOT_PROP_URI = "http://purl.obolibrary.org/obo/IAO_0000115";
	// public static final String MAPPED_TO_ANNOT_PROP_URI = "http://mapped_to";

	public static final String CPT_BILLING_CODE_URI = "http://purl.obolibrary.org/obo/ARG_0000442";
	public static final String ICD9_BILLING_CODE_URI = "http://purl.obolibrary.org/obo/ARG_0000123";

	public static final String RDFS_BASE_URI = "http://www.w3.org/2000/01/rdf-schema#";

	// ICD9
	public static final String BASE_ICD9CM_ONTOLOGY_URI = "http://purl.obolibrary.org/obo/arg/icdcode/";
	public static final String BASE_ICD9CM_CLASS_URI = BASE_ICD9CM_ONTOLOGY_URI;
	public static final String ICD9CM_ONTOLOGY_FILE_NAME = "icd9cm-classes.owl";
	public static final String ICD9CM_ONTOLOGY_URI = BASE_ICD9CM_ONTOLOGY_URI
			+ ICD9CM_ONTOLOGY_FILE_NAME;
	public static final String ICD9CM_SUB_AXIOMS_ONTOLOGY_FILE_NAME = "icd9cm-subaxioms.owl";
	public static final String ICD9CM_SUB_AXIOMS_ONTOLOGY_URI = BASE_ICD9CM_ONTOLOGY_URI
			+ ICD9CM_SUB_AXIOMS_ONTOLOGY_FILE_NAME;

	// CPT
	public static final String BASE_CPT_ONTOLOGY_URI = "http://purl.obolibrary.org/obo/arg/cptcode/";
	public static final String BASE_CPT_CLASS_URI = BASE_CPT_ONTOLOGY_URI;
	public static final String CPT_ONTOLOGY_FILE_NAME = "cpt-classes.owl";
	public static final String CPT_ONTOLOGY_URI = BASE_CPT_ONTOLOGY_URI + CPT_ONTOLOGY_FILE_NAME;

	// RxNorm
	public static final String BASE_RXN_ONTOLOGY_URI = "http://purl.obolibrary.org/obo/arg/rxncode/";
	public static final String BASE_RXN_CLASS_URI = BASE_RXN_ONTOLOGY_URI;
	public static final String RXN_ONTOLOGY_FILE_NAME = "rxn-classes.owl";
	public static final String RXN_ONTOLOGY_URI = BASE_RXN_ONTOLOGY_URI + RXN_ONTOLOGY_FILE_NAME;

	// Mesh, and ICD9 to Mesh mapping
	public static final String MESH_BASE_ONTOLOGY_URI = "http://purl.obolibrary.org/obo/arg/mesh/";
	public static final String MESH_BASE_CLASS_URI = MESH_BASE_ONTOLOGY_URI;
	public static final String ICD9_MESH_SAME_CUI_FILE_NAME = "icd9-mesh-same-cui.owl";
	public static final String ICD9_MESH_SAME_CUI_URI = MESH_BASE_ONTOLOGY_URI
			+ ICD9_MESH_SAME_CUI_FILE_NAME;
	public static final String ICD9_MESH_SIMILAR_CUI_FILE_NAME = "icd9-mesh-similar-cui.owl";
	public static final String ICD9_MESH_SIMILAR_CUI_ONTOLOGY_URI = MESH_BASE_ONTOLOGY_URI
			+ ICD9_MESH_SIMILAR_CUI_FILE_NAME;
	public static final String ICD9_MESH_BROADER_CUI_FILE_NAME = "icd9-mesh-broader-cui.owl";
	public static final String ICD9_MESH_BROADER_CUI_ONTOLOGY_URI = MESH_BASE_ONTOLOGY_URI
			+ ICD9_MESH_BROADER_CUI_FILE_NAME;
	// mesh descriptors and subclass axioms
	public static final String MESH_DESCRIPTOR_FILE_NAME = "mesh-descriptor.owl";
	public static final String MESH_DESCRIPTOR_ONTOLOGY_URI = MESH_BASE_ONTOLOGY_URI
			+ MESH_DESCRIPTOR_FILE_NAME;
	public static final String MESH_DESCRIPTOR_DEFINITION_FILE_NAME = "mesh-descriptor-definition.owl";
	public static final String MESH_DESCRIPTOR_DEFINITION_ONTOLOGY_URI = MESH_BASE_ONTOLOGY_URI
			+ MESH_DESCRIPTOR_DEFINITION_FILE_NAME;
	public static final String MESH_DESCRIPTOR_SUBCLASS_AXIOM_FILE_NAME = "mesh-descriptor-subclass-axiom.owl";
	public static final String MESH_DESCRIPTOR_SUBCLASS_AXIOM_ONTOLOGY_URI = MESH_BASE_ONTOLOGY_URI
			+ MESH_DESCRIPTOR_SUBCLASS_AXIOM_FILE_NAME;

	// misc
	public static final String OWL_FILES_GENERATED_DIR_NAME = "OWLFiles_generated";

	// instance data
	public static final String CLINICAL_INSTANCE_ONTOLOGY_FILE_NAME = "clinical_instances.owl";
	public static final String CLINICAL_INSTANCE_ONTOLOGY_URI = "http://purl.obolibrary.org/obo/arg/"
			+ CLINICAL_INSTANCE_ONTOLOGY_FILE_NAME;
	public static final String BASE_CLINICAL_INSTANCE_URI = "http://dev.ohsu.eagle-i.net/i/";
	public static final String PRACTITIONER_LABEL_PREFIX = "practiotioner_";

	// constants related to the refactoring owl file and related scripts

	public static final String REFACT_AXIOM_ADDED_IRI = "http://arg-refactoring.owl/_isf_axiom_added";
	public static final String REFACT_AXIOM_REMOVED_IRI = "http://arg-refactoring.owl/_isf_axiom_removed";
	public static final String REFACT_COMMENT = "http://arg-refactoring.owl/_isf_comment";
	public static final String REFACT_REPLACED_BY_IRI = "http://arg-refactoring.owl/_isf_replaced_by";
	public static final String REFACT_REPLACES_IRI = "http://arg-refactoring.owl/_isf_replaces";
	public static final String REFACT_POSSIBLE_REPLACED_BY_IRI = "http://arg-refactoring.owl/_isf_possible_replaced_by";
	public static final String REFACT_POSSIBLE_REPLACES_IRI = "http://arg-refactoring.owl/_isf_possible_replaces";
	public static final String REFACT_REASON_IRI = "http://arg-refactoring.owl/_isf_reason";
	public static final String REFACT_APPROVED_IRI = "http://arg-refactoring.owl/_isf_approved";
	public static final String REFACT_MODULE = "http://arg-refactoring.owl/_isf_module";
	public static final String REFACT_ANNOTATION = "http://arg-refactoring.owl/_isf_annotation";
	public static final String REFACT_REUSED_AS = "http://arg-refactoring.owl/_isf_reused_as";
	public static final String REFACT_LABEL = "http://arg-refactoring.owl/_isf_label";
}
