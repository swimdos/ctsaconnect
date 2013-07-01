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

from rdflib.namespace import Namespace, RDF, RDFS, XSD

base_instance_uri="http://ohsu.dev.eagle-i.net/i/"
expertise_base_uri = base_instance_uri+"exp/"
icd_base_uri = "http://purl.obolibrary.org/obo/arg/icdcode/"
#
# Constant used for generating instances
#
BFO = Namespace("http://www.ifomis.org/bfo/1.1/snap#")
FOAF = Namespace("http://xmlns.com/foaf/0.1/")
VIVO = Namespace("http://vivoweb.org/ontology/core#")
OBO = Namespace("http://purl.obolibrary.org/obo/")

#Define properties and Classes Used

#Class URI for the type DateTimeValue in VIVO
DateTimeValue_Class = VIVO["DateTimeValue"]

#Property connecting an isntance of DateTimeValue to the XSD:dateTime value
dateTime_Prop = VIVO["dateTime"]

#CLass expert measurement process
exp_measurment_process_Class = OBO["ARG_2000011"]

#Annotation property for Description in the VIVO namesapce
description_AnnProp = VIVO["description"]

#POBJect property dateTimeValue connecting to an instance of
dateTimeValue_Obj_Prop = VIVO["dateTimeValue"]

#BFO QUality Class
quality_Class = BFO["Quality"]

#BFO Object Property has Quality at some time connecting a Continuant to a quality
has_quality_Obj_Prop = OBO["BFO_0000086"]

#Health Care Provider CLass
health_care_provider_Class = OBO["ARG_0000130"]

# Expertise mesurement Class
expertise_measurment = OBO["ARG_2000009"]

# HAs_measurement_value IAO dataProperty
has_measurement_value_Data_Prop = OBO["IAO_0000004"]

#Measurement label data Property
has_measurement_label_Data_Prop = OBO["ARG_2000012"]

#is_quality_measurement_of IAO Object Property connecting expertise measurement to expertise
is_quality_measurement_of_Obj_Prop = OBO["IAO_0000221"]

# is_quality_measured_as IAO OBJ Property between expertise to Expertise emasurement
is_quality_measured_as_Obj_Prop = OBO["IAO_0000417"]

#is_specified_output Ibject property form OBI connecting a measurememtn to a measurement process
is_specified_output_Obj_Prop= OBO["OBI_0000312"]

#has_measurement-Unit_label Object property
has_measurement_unit_label_Obj_Prop = OBO["IAO_0000039"]