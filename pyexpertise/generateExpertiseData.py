#!/usr/bin/python

#Insert BSD License (http://opensource.org/licenses/BSD-2-Clause)

##############################################################################
# Copyright (c) 2012\
# Carlo Torniai (carlotorniai at gmail.com)
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without modification,
# are permitted provided that the following conditions are met:
#
# 1. Redistributions of source code must retain the above copyright notice, this
# list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice,
# this list of conditions and the following disclaimer in the documentation
# and/or other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
# THE POSSIBILITY OF SUCH DAMAGE.
##############################################################################

####
# TO DO:
#  - Parametrize the values for the algorithm Execution Instance
#  - Have a wrapper here to get an instance of expertise measurement to be translated in RDF
#  - We need to create just one instance of the execution algorithm and related date
#   - Currently using a uuid URI for the instance of date.
#
#    Note that this script requires rdflib installation
#    https://github.com/RDFLib/rdflib
####


import logging
import time
import uuid
import Connection
import dataGenConst
from rdflib.graph import Graph
from rdflib.term import URIRef, Literal, BNode
from rdflib.namespace import Namespace, RDF, RDFS, XSD


store = Graph()
# Bind a few prefix, namespace pairs.
store.bind("dc", "http://http://purl.org/dc/elements/1.1/")
store.bind("foaf", "http://xmlns.com/foaf/0.1/")
store.bind("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
store.bind("obo", "http://purl.obolibrary.org/obo/")
store.bind("vivo", "http://vivoweb.org/ontology/core#")

# Create the identifier for the algorithm execution
algExexURI= URIRef("http://ohsu.dev.eagle-i.net/i/exp/measure_algorithm_01")


def startLogger():
#===============================================================================
# Configure how we want rdflib logger to log messages
#===============================================================================

    _logger = logging.getLogger("rdflib")
    _logger.setLevel(logging.DEBUG)
    _hdlr = logging.StreamHandler()
    _hdlr.setFormatter(logging.Formatter('%(name)s %(levelname)s: %(message)s'))
    _logger.addHandler(_hdlr)

def createAlgorithmExecutionInstance():

    # Unique ID generation
    algddateExecID = uuid.uuid4()
    # Create a data execution instance
    # Constant Execution Algorithm execution
    localtime = time.localtime()
    timeString = time.strftime("%Y%m%d%H%M%S", localtime)
    print timeString
    # Questions:
    # Is that ok to use this URI randomly generated for the instance of execution date?
    #print time.strftime('%Y-%m-%dT%X%z')
    exectime = time.strftime('%Y-%m-%dT%X%z')
    execDateTimeInst = URIRef(dataGenConst.base_instance_uri + str(algddateExecID))
    store.add((execDateTimeInst, RDF.type, dataGenConst.DateTimeValue_Class))
    store.add((execDateTimeInst, dataGenConst.dateTime_Prop, Literal(exectime, datatype=XSD.dateTime)))
    # Add triples using store's add method.
    # Adding the type of
    store.add((algExexURI, RDF.type, dataGenConst.exp_measurment_process_Class))
    store.add((algExexURI, RDFS.label, Literal("measure_algorithm_0")))
    store.add((algExexURI, dataGenConst.description_AnnProp, Literal("The algorithm used for measurement was the verison 1.0 leveraging weight calculated from       code frequency and patients percentage per provide")))
    store.add((algExexURI, dataGenConst.dateTimeValue_Obj_Prop, execDateTimeInst))


def createExpertandExpertisetriples(npi):
#===============================================================================
#   Creates  measurement related to the expert and related expertise
#===============================================================================
    # Create the expert instance
    expert_URI = URIRef(dataGenConst.base_instance_uri+npi)

    #Create Experience)instance
    experience_URI = URIRef(dataGenConst.expertise_base_uri+npi)

    #Add expert triples to store

    store.add((expert_URI, RDF.type, dataGenConst.health_care_provider_Class))
    store.add((expert_URI, RDFS.label, Literal("Health Care provider" + npi)))
    store.add((expert_URI, dataGenConst.has_quality_Obj_Prop, experience_URI))

    # Add experience triples
    store.add((experience_URI, RDF.type, dataGenConst.quality_Class))
    store.add((experience_URI, RDFS.label, Literal(npi+"_expertise")))
    return experience_URI

def createMeasurementTriples (npi, icd, measure_label, value, experience_URI, algExexURI):
#==============================================================================
#   Writes triples related to a expertise measurement
#===============================================================================
    # Create the expertise measurement IRI
    expertise_measurement_URI = URIRef(dataGenConst.base_instance_uri+npi+"_"+icd)

    # Create ICD URI
    #Here currently we generate the URI in the following way
    icd_uri = URIRef(dataGenConst.icd_base_uri+icd)

    # Add triples
    store.add((expertise_measurement_URI, RDF.type, dataGenConst.expertise_measurment))
    store.add((expertise_measurement_URI, RDFS.label, Literal(npi+"_"+icd+"_measurement")))
    store.add((expertise_measurement_URI, dataGenConst.has_measurement_value_Data_Prop, Literal(value, datatype=XSD.float)))
    store.add((expertise_measurement_URI, dataGenConst.has_measurement_label_Data_Prop, Literal(measure_label)))
    store.add((expertise_measurement_URI, dataGenConst.is_quality_measurement_of_Obj_Prop, experience_URI))
    store.add((expertise_measurement_URI, dataGenConst.is_specified_output_Obj_Prop, algExexURI))
    store.add((expertise_measurement_URI, dataGenConst.has_measurement_unit_label_Obj_Prop, icd_uri))





def main():

    startLogger()
    createAlgorithmExecutionInstance()
    getExpertiseData()
    # Here assuming a data structure coming out of the DB to contain the following fields per row
    npi="1013113257"
    measure_label="Depressive disorder, not elsewhere classified"
    measure_value="12.8049"
    icd="301"

    experience_URI = createExpertandExpertisetriples(npi)
    createMeasurementTriples(npi, icd, measure_label, measure_value, experience_URI, algExexURI)

    # Iterate over triples in store and print them out.
    #print "--- printing raw triples ---"
    #for s, p, o in store:
    #    print s, p, o


    # Serialize the store as RDF/XML to the file expertise.rdf.
    store.serialize("expertise.rdf", format="pretty-xml", max_depth=3)


    print "RDF Serializations:"

    # Serialize as XML
    print "--- start: rdf-xml ---"
    print store.serialize(format="pretty-xml")
    print "--- end: rdf-xml ---\n"

    # Serialize as NTriples
    print "--- start: ntriples ---"
    print store.serialize(format="nt")
    print "--- end: ntriples ---\n"


if __name__ == '__main__':
    main()