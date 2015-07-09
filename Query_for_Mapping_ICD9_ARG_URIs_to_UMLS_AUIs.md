# Introduction #

In this page we describe the SPARQL query we are using to align ICD9 codes URI used in the ISF with the UMLS AUIs URIs


# SPARQL Query #

The query below can be executed at The SPARQL query can be executed at <http://link.informatics.stonybrook.edu/sparql/>.

```
SELECT distinct ?argdx ?argdxcui ?auiicd9 ?auiicd9label ?auiicd9aui ?cuiicd9 ?cuiicd9label ?code ?termtypelabel
{
{?argdx <http://purl.obolibrary.org/obo/ARG_0000033> ?code .
?argdx  <http://purl.obolibrary.org/obo/ARG_0000034> ?argdxcui .
?auiicd9 <http://link.informatics.stonybrook.edu/umls/AUI/CODE> ?code .
?auiicd9 <http://link.informatics.stonybrook.edu/umls/hasTermType> <http://link.informatics.stonybrook.edu/umls/TTY/PT> .
?auiicd9 <http://link.informatics.stonybrook.edu/umls/hasTermType> ?termtype .
?termtype rdfs:label ?termtypelabel .
?auiicd9 <http://link.informatics.stonybrook.edu/umls/hasSAB> <http://link.informatics.stonybrook.edu/umls/SAB/ICD9CM> .
?auiicd9 <http://link.informatics.stonybrook.edu/umls/AUI/AUI> ?auiicd9aui .
?auiicd9 rdfs:label ?auiicd9label .
?auiicd9 <http://link.informatics.stonybrook.edu/umls/hasCUI> ?cuiicd9 .
?cuiicd9 <http://link.informatics.stonybrook.edu/umls/CUI/CUI> ?cuiicd9label .
}
union
{?argdx <http://purl.obolibrary.org/obo/ARG_0000033> ?code .
?argdx  <http://purl.obolibrary.org/obo/ARG_0000034> ?argdxcui .
?auiicd9 <http://link.informatics.stonybrook.edu/umls/AUI/CODE> ?code .
?auiicd9 <http://link.informatics.stonybrook.edu/umls/hasTermType> <http://link.informatics.stonybrook.edu/umls/TTY/HT> .
?auiicd9 <http://link.informatics.stonybrook.edu/umls/hasTermType> ?termtype .
?termtype rdfs:label ?termtypelabel .
?auiicd9 <http://link.informatics.stonybrook.edu/umls/hasSAB> <http://link.informatics.stonybrook.edu/umls/SAB/ICD9CM> .
?auiicd9 <http://link.informatics.stonybrook.edu/umls/AUI/AUI> ?auiicd9aui .
?auiicd9 rdfs:label ?auiicd9label .
?auiicd9 <http://link.informatics.stonybrook.edu/umls/hasCUI> ?cuiicd9 .
?cuiicd9 <http://link.informatics.stonybrook.edu/umls/CUI/CUI> ?cuiicd9label .
}} order by ?argdx
```