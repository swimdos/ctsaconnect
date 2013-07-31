# demonstrator.py
# small script to process .csv file containing information on doctors to produce VIVO RDF

import csv, vivotools
from string import Template

doctorURI = "<http://localhost/individual/n1665>"

expertise  = Template(
"""
<$uri> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.ifomis.org/bfo/1.1/snap#DependentContinuant> .
<$uri> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.ifomis.org/bfo/1.1/snap#SpecificallyDependentContinuant> .
<$uri> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.ifomis.org/bfo/1.1#Entity> .
<$uri> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> .
<$uri> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.ifomis.org/bfo/1.1/snap#Continuant> .
<$uri> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.ifomis.org/bfo/1.1/snap#Quality> .
"""
)

doctor_expertise = Template(
"""
$doctorURI <http://purl.obolibrary.org/obo/BFO_0000086> <$uri> .
"""
)

expertise_measurement = Template(
"""
<$uri2> <http://purl.obolibrary.org/obo/IAO_0000221> <$uri> .
<$uri2> <http://purl.obolibrary.org/obo/ARG_2000012> "$label" .
<$uri2> <http://purl.obolibrary.org/obo/IAO_0000004> "$weight" .
"""
)

a = open("expert438xslx.csv","rb")
experts = csv.reader(a)
doctorURI = "<http://localhost/individual/n1665>"

i = 1

for row in experts:
	print "# START processing row " + str(i) + "\n"
	# create expertise objects
	uri = vivotools.get_vivo_uri()
	print expertise.substitute(uri = uri)

	# create link from doctor to expertise
	print doctor_expertise.substitute(uri = uri, doctorURI = doctorURI)
	
	# create expertise measurement
	uri2 = vivotools.get_vivo_uri()
	label = row[2]
	weight =  float(row[8]) * 100
	print expertise_measurement.substitute(uri = uri, uri2 = uri2, label = label, weight = weight)
	print "# END processing row " + str(i) + "\n"
	i += 1


