# Introduction #

These SPARQL Queries created by Carlo and Shahim allow you to get statistical information about your dataset.


1) A query that returns the count for unique codes for each practitioner

For ICD:
```
PREFIX arg: <http://purl.obolibrary.org/obo/>
PREFIX obi: <http://purl.obolibrary.org/obo/>
SELECT  ?provider (COUNT(DISTINCT ?code_value) AS ?unique_icd_codes)
WHERE
{
  ?encounter <http://www.obofoundry.org/ro/ro.owl#has_participant> ?provider.
  ?provider a arg:ARG_0000130.
  ?encounter <http://purl.obolibrary.org/obo/OBI_0000299> ?diagnosis.
  ?diagnosis <http://www.obofoundry.org/ro/ro.owl#has_part> ?codes.
  ?diagnosis a obi:OBI_0000075.
  ?codes  a ?code_type.
  ?code_type arg:ARG_0000033 ?code_value.
}
group by ?provider 
```

And for CPT:

```
PREFIX arg: <http://purl.obolibrary.org/obo/>
PREFIX obi: <http://purl.obolibrary.org/obo/>
SELECT  ?provider (COUNT(DISTINCT ?code_value) AS ?unique_cpt_codes)
WHERE
{
  ?encounter <http://www.obofoundry.org/ro/ro.owl#has_participant> ?provider.
  ?provider a arg:ARG_0000130.
  ?encounter <http://purl.obolibrary.org/obo/OBI_0000299> ?order.
  ?order <http://www.obofoundry.org/ro/ro.owl#has_part> ?codes.
  ?order a arg:ARG_0000016.
  ?codes  a ?code_type.
  ?code_type arg:ARG_0000033 ?code_value.
}
group by ?provider 
```

2) A query for the total number of codes used by each practitioner.
Pretty obvious:
```
PREFIX arg: <http://purl.obolibrary.org/obo/>
PREFIX obi: <http://purl.obolibrary.org/obo/>
SELECT  ?provider (COUNT(?code_value) AS ?icd_code_count)
WHERE
{
  ?encounter <http://www.obofoundry.org/ro/ro.owl#has_participant> ?provider.
  ?provider a arg:ARG_0000130.
  ?encounter <http://purl.obolibrary.org/obo/OBI_0000299> ?diagnosis.
  ?diagnosis <http://www.obofoundry.org/ro/ro.owl#has_part> ?codes.
  ?diagnosis a obi:OBI_0000075.
  ?codes  a ?code_type.
  ?code_type arg:ARG_0000033 ?code_value.
}
group by ?provider 
```

And for CPT:

```
PREFIX arg: <http://purl.obolibrary.org/obo/>
PREFIX obi: <http://purl.obolibrary.org/obo/>
SELECT  ?provider (COUNT(?code_value) AS ?cpt_code_count)
WHERE
{
  ?encounter <http://www.obofoundry.org/ro/ro.owl#has_participant> ?provider.
  ?provider a arg:ARG_0000130.
  ?encounter <http://purl.obolibrary.org/obo/OBI_0000299> ?order.
  ?order <http://www.obofoundry.org/ro/ro.owl#has_part> ?codes.
  ?order a arg:ARG_0000016.
  ?codes  a ?code_type.
  ?code_type arg:ARG_0000033 ?code_value.     }
group by ?provider 
```


3) A query that return the original  input data in a tabular form. (The aggregates of occurrences of codes and unique patents for practitioner)

The query below is for ICD codes:
```
PREFIX arg: <http://purl.obolibrary.org/obo/>
PREFIX arginst: <http://dev.ohsu.eagle-i.net/i/>
PREFIX obi: <http://purl.obolibrary.org/obo/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
 
SELECT    ?provider  ?code_value  (COUNT( ?code ) as ?code_count) 
  (count(distinct ?patient) as ?unique_patient_count) ?code_label
WHERE
{
  ?encounter <http://www.obofoundry.org/ro/ro.owl#has_participant> ?provider.
  ?provider a arg:ARG_0000130.
  ?encounter <http://purl.obolibrary.org/obo/OBI_0000299> ?diagnosis.
  ?diagnosis a obi:OBI_0000075.
  ?encounter <http://www.obofoundry.org/ro/ro.owl#has_participant> ?patient.
  ?patient a arg:ARG_0000051.
  ?diagnosis <http://www.obofoundry.org/ro/ro.owl#has_part> ?code.
  ?code  a ?code_type.
  ?code_type arg:ARG_0000033 ?code_value.
  ?code_type rdfs:label ?code_label.
}
group by ?provider ?code_value ?code_label
order by ?provider ?code_value
```

In order to get the CPT is enough to bind the output of the encounter to be of type order as below (I've also changed the name of the variable for readability sake)

```
PREFIX arg: <http://purl.obolibrary.org/obo/>
PREFIX arginst: <http://dev.ohsu.eagle-i.net/i/>
PREFIX obi: <http://purl.obolibrary.org/obo/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

SELECT    ?provider  ?code_value  (COUNT( ?code ) as ?code_count) (count(distinct ?patient) as ?unique_patient_count) ?code_label
WHERE
{
  ?encounter <http://www.obofoundry.org/ro/ro.owl#has_participant> ?provider.
  ?provider a arg:ARG_0000130.
  ?encounter <http://purl.obolibrary.org/obo/OBI_0000299> ?order.
  ?order a arg:ARG_0000016.
  ?encounter <http://www.obofoundry.org/ro/ro.owl#has_participant> ?patient.
  ?patient a arg:ARG_0000051.
  ?order <http://www.obofoundry.org/ro/ro.owl#has_part> ?code.
  ?code  a ?code_type.
  ?code_type arg:ARG_0000033 ?code_value.
  ?code_type rdfs:label ?code_label.
}
group by ?provider ?code_value ?code_label
order by ?provider ?code_value  
```

4)
A sample query over the "specialty" data that is encoded according to the "assign" model. The query shows specialty results for an NPI number according to the specialty data from the NUCC at http://www.nucc.org/index.php?option=com_content&view=article&id=14&Itemid=125

```
PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>
PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>
PREFIX owl:<http://www.w3.org/2002/07/owl#>
PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX xml:<http://www.w3.org/XML/1998/namespace>
PREFIX arg:<http://purl.obolibrary.org/obo/>

select ?assignor ?assignee ?specialty ?specialtyType
where
{
 ?assigner arg:ARG_2000030 ?assignment.  ## OP: creates
 ?assignment arg:ARG_2000024 ?assignee.  ## OP: assignment_of
 ?assignment arg:ARG_2000019 ?assigned.  ## OP: assignes
 ?assigned rdf:type ?specialtyClass.
 ?specialtyClass rdfs:label ?specialty.
 ?specialtyClass rdfs:subClassOf ?specialtySuper.
 ?specialtySuper rdfs:label ?specialtyType.
 FILTER (?assignor =  <http://ohsu.dev.eagle-i.net/i/1003018730>).
}
```

The results will show the assignor and assignee being the same agent (a self-assignment), the assigned specialties, whether they are a general specialty or a sub-specialty, and the type of the provider (a physician).