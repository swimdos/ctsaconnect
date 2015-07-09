# Introduction #

We have published an initial dataset of expertise data generated in the context of the CTSAConnect project.
In order to get access to the SPARQL endpoint containing these data please contact one of the Administrators of this project.

Below we give some example of SPARQL queries that can be executed against this dataset.

1) A query that returns the URI of the Health Care Providers in the dataset:


```
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX obo: <http://purl.obolibrary.org/obo/>
PREFIX vivo: <http://vivoweb.org/ontology/core#> 

SELECT   * where
{
?provider a obo:ARG_0000130.
}
```


2) A query to get the expertise for a provider [for http://ohsu.dev.eagle-i.net/i/1235281379](example.md)

```
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX obo: <http://purl.obolibrary.org/obo/>
PREFIX vivo: <http://vivoweb.org/ontology/core#>
SELECT    ?expertise ?label ?weight
WHERE
{
## Select the expertise for  provider http://ohsu.dev.eagle-i.net/i/1235281379
<http://ohsu.dev.eagle-i.net/i/1235281379> obo:BFO_0000086 ?expertise.

## Select measurements contributing to expertise
?expertise_measurement obo:IAO_0000221 ?expertise.

## Select the weight and the label for measurements
?expertise_measurement obo:ARG_2000012 ?label.
?expertise_measurement  obo:IAO_0000004 ?weight. 

}
```


3) A query to retrieve the algorithm execution information

```
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX obo: <http://purl.obolibrary.org/obo/>
PREFIX vivo: <http://vivoweb.org/ontology/core#>

SELECT DISTINCT ?algorithm_execution ?date ?description
WHERE
{
## Select a measurement contributing to a particular expertise.
?expertise_measurement obo:IAO_0000221 <http://ohsu.dev.eagle-i.net/i/exp/1235281379>.

##Identify the algorithm execution tht generated that measurement
?expertise_measurement obo:OBI_0000312 ?algorithm_execution.

## Identify the execution_date instance and get its date value. 
?algorithm_execution vivo:dateTimeValue ?execution_date.
?execution_date vivo:dateTime ?date.

## Gets description of the algorithm execution
?algorithm_execution vivo:description ?description.
}

```