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

##
## Loading a graph from a graphML and getting subnetwork for researxhers and practicioners in a list
## And saving them in excel (as well as in the graph itself)

## From this

import MySQLdb
import Connection
import networkx as nx
import pickle
verbose = True
safe= True
base_dir = "/Users/torniai/Dropbox/clinical_research_graphs/"

filename= "filtered_mesh_20_icd_30_2007_2012"
input_file = base_dir+ filename+"pkl"

def executeQuery(db, query):
    # prepare a cursor object using cursor() method
    cursor = db.cursor()
    # Prepare SQL query to INSERT a record into the database.
    try:
       # Execute the SQL command
       cursor.execute(query)
       # Commit your changes in the database
       db.commit()
    except:
        print "Error"
       # Rollback in case there is any error
        db.rollback()
    # disconnect from server
    # db.close()


def getDB(host, port,  user, password, database):
#===============================================================================
#   Function that returns a DB connection
#
#===============================================================================
    db = MySQLdb.connect(host=host, port=port, user=user, passwd=password, db=database)
    return db

G = nx.read_gpickle(input_file)
for node in G.nodes(data=True):
    print node
print ("Nodes " + str(len(G.nodes())))
print ("Edges " + str(len(G.edges())))
distinct_key=set()

for edge in G.edges(data=True):
    #print edge
    distinct_key.add(edge[2]["key"])
print ("DIstinct Mesh terms used " + str(len(distinct_key)))

# Get the neighborso of sue
sue_nodes =  G.neighbors(16)


H = nx.subgraph(G,16)
print ("Nodes " + str(len(H.nodes())))
print ("Edges " + str(len(H.edges())))







# I think the best way is to use neghbor
# Get the neighborso of sue
# This at least gives me te name of folks but not why they are connected
connected_experts =  G.neighbors(16)
subgraph_experts = nx.Graph(G.subgraph(connected_experts))
print ("Nodes " + str(len(subgraph_experts.nodes())))
print ("Edges " + str(len(subgraph_experts.edges())))

print("Ego Graph")
connected_experts = nx.Graph(nx.ego_graph(G, 16, radius=1, center=True, undirected=True, distance=None))
print ("Nodes " + str(len(connected_experts.nodes())))
print ("Edges " + str(len(connected_experts.edges())))
nx.write_graphml(connected_experts, base_dir+"ego_test.graphml")

def add_nodes_roperties(H):
    db_conn = getDB("localhost", 3306, "root", "grisu#71", "scivaltest")
    for node in H.nodes():
        # Get the distinct list of sciva_id_s in the connections_mesh_icd
        sql2= "SELECT provider_first_name, provider_last_name from practioner WHERE npi = %s" %(node)
        print sql2
        cursor2 = db_conn.cursor()
        try:
             cursor2.execute(sql2)
             result2 = cursor2.fetchall()
        except Exception, e:
            print e
        for row2 in result2:
            clinician_element={}
            attributes = {}
            clinician_element["npi"]= node
            clinician_element["name"]= row2[0]
            clinician_element["lastname"] = row2[1]
            print ("Adding researcher " + str(clinician_element["name"])+ str(clinician_element["lastname"]))
            label = clinician_element["name"] + " " + clinician_element["lastname"]
            attributes["Label"] = label
            attributes["Type"] = "Clinician"
            attributes["First_Name"] = clinician_element["name"]
            attributes["Last_Name"] =  clinician_element["lastname"]
            H.add_node(int(clinician_element["npi"]), attributes)
    db_conn.close()


# Let's try to add my own version of ego_graph same as above...
nodes=set([16])
nodes.update(G.neighbors(16)) # extend with neighbors

# Now I want to add all the attributes to these nodes
# By looping through them and
for node in nodes:
    print G.node[node]

H=nx.Graph(G.subgraph(nodes).copy())
print ("Nodes " + str(len(H.nodes())))
print ("Edges " + str(len(H.edges())))

# Here add the properties
add_nodes_roperties(H)
nx.write_graphml(H, base_dir+"modified_ego_test.graphml")

# CHeck data = true
# There is no freaking way to do that therefore I need to :
# http://stackoverflow.com/questions/10158833/iterate-neighbors-along-with-attributes
