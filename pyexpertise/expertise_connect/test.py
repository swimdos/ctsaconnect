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

## Test using Network X for relating clinitians to basic researchers
## Check the tutorial http://networkx.github.io/documentation/latest/tutorial/tutorial.html
## Now what I should get is the list of connected folks
## TO DO:
## How can I add a Label to the connections? I want to add a label which is the name of the Mesh Term

## QUat kind of queries I need to build in order to create a table that looks like

## id, npi, mesh_label, wieght
## From this

import networkx as nx
G=nx.Graph()

# List of dictionary contaning the mane and the type: CLinitians or Basic researcher
def logger(string_to_write):
    print(string_to_write)

#Researcher_list =  list()
#Clinitian_list = list()

Researcher_list =  [ {'id':1, 'name':"Carlo", 'lastname':"Torniai"},  {'id':2, 'name':"Melissa", 'lastname':"Haendel"}]
Clinitian_list =  [ {'npi':123, 'name':"Rob", 'lastname':"Schuff"} , {'npi':456, 'name':"Simone", 'lastname':"Walld"}]

Edges_list = [{'id':1, 'npi':123, 'mesh':"apostosis", 'weight':0.50},  {'id':2, 'npi':456, 'mesh':"appendix", 'weight':0.62}]
# I should assume to have research_id, cliniian_id, ICD / Mesh Label , strength
# Each distinct reseaarcher ID with First and LAstname + each linitian npi with attributes first and last name wil l be nodes
# The edges will be for each row adding an edg between npi and id with weight X

#Edges_list = [{'id':1, 'npi':123, 'mesh':"apostosis", 'weight':0.50},  {'id':2, 'npi':456, 'mesh':"appendix", 'weight':0.62}]

#G.add_edges_from([(1,2,{'color':'blue'}), (2,3,{'weight':8})])

for i in range (0, len(Researcher_list)):
    attributes = {}
    label = Researcher_list[i]["name"] + " " + Researcher_list[i]["lastname"]
    attributes["Label"] = label
    G.add_node(int(Researcher_list[i]["id"]), attributes)

for j in range (0, len(Clinitian_list)):
    attributes = {}
    label = Clinitian_list[j]["name"] + " " + Clinitian_list[j]["lastname"]
    attributes["Label"] = label
    G.add_node(int(Clinitian_list[j]["npi"]), attributes)

Edges_list = [{'id':1, 'npi':123, 'mesh':"apostosis", 'weight':0.50},  {'id':2, 'npi':456, 'mesh':"appendix", 'weight':0.62}]


for k in range (0, len(Edges_list)):
    G.add_edge(Edges_list[k]["id"], Edges_list[k]["npi"], weight=Edges_list[k]["weight"])
print G
print G.neighbors(123)
logger("Writing GraphML file")
nx.write_graphml(G, "./test.graphml")