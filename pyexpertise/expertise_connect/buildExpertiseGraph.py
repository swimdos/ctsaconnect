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

## Test using Network X for relating clinicians to basic researchers
## Check the tutorial http://networkx.github.io/documentation/latest/tutorial/tutorial.html
## Now what I should get is the list of connected folks
## TO DO:
## How can I add a Label to the connections? I want to add a label which is the name of the Mesh Term

## QUat kind of queries I need to build in order to create a table that looks like

## id, npi, mesh_label, wieght
## From this

import MySQLdb
import Connection
import networkx as nx

verbose = True
safe= True

G=nx.Graph()

# List of dictionary containing the mane and the type: CLinitians or Basic researcher
def logger(string_to_write):
    print(string_to_write)

#Researcher_list =  list()
#Clinitian_list = list()

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

def build_edge_list():

    Edges_list = []
# Create the list of the edges form the DB
    db_conn = getDB("localhost", 3306, "root", "grisu#71", "scivaltest")
    # Here I filter MeSH_terms that occur more than 10 times
    sql = "SELECT expert_scival_id, npi, S_Label, weight \
    from connections_mesh_icd  WHERE expert_scival_id in \
    (SELECT expert_scival_id from authors_mesh_weights where term_count >10)"

    if verbose:
            print sql.encode('ascii', 'ignore')
            # Insert the row
    cursor = db_conn.cursor()
    try:
        cursor.execute(sql)
        result = cursor.fetchall()
    except Exception, e:
        print e
    # Now build the edge list
    for row in result:
        edge_element={}
        edge_element["id"]= row[0]
        edge_element["npi"]= row[1]
        edge_element["label"] = row[2]
        edge_element["weight"] = row[3]
        # Add the element to the Researcher List
        Edges_list.append(edge_element)
    db_conn.close()
    return Edges_list

def build_researcher_list():
    Researcher_list = []
    # Get the distinct list of sciva_id_s in the connections_mesh_icd

    db_conn = getDB("localhost", 3306, "root", "grisu#71", "scivaltest")
    sql = "SELECT distinct expert_scival_id \
           from connections_mesh_icd"
    if verbose:
            print sql.encode('ascii', 'ignore')
            # Insert the row
    cursor = db_conn.cursor()
    try:
        cursor.execute(sql)
        result = cursor.fetchall()
    except Exception, e:
        print e
    # Now build the edge list
    for row in result:
        # Get Lastname and Firstname form the expert_table
        sql2= "SELECT first_name, last_name from expert WHERE expert_scival_id = %d" %(int(row[0]))
        cursor2 = db_conn.cursor()
        try:
             cursor2.execute(sql2)
             result2 = cursor2.fetchall()
        except Exception, e:
            print e
        for row2 in result2:
            researcher_element={}
            researcher_element["id"]= row[0]
            researcher_element["name"]= row2[0]
            researcher_element["lastname"] = row2[1]
            # Add the element to the Researcher List
            Researcher_list.append(researcher_element)
    db_conn.close()
    return Researcher_list


def build_clinitian_list():
    Clinitian_list = []
    # Get the distinct list of sciva_id_s in the connections_mesh_icd

    db_conn = getDB("localhost", 3306, "root", "grisu#71", "scivaltest")
    sql = "SELECT distinct npi\
           from connections_mesh_icd"
    if verbose:
            print sql.encode('ascii', 'ignore')
            # Insert the row
    cursor = db_conn.cursor()
    try:
        cursor.execute(sql)
        result = cursor.fetchall()
    except Exception, e:
        print e
    # Now build the edge list
    for row in result:
        # Get Lastname and Firstname form the expert_table
        sql2= "SELECT provider_first_name, provider_last_name from practioner WHERE npi = %d" %(int(row[0]))
        cursor2 = db_conn.cursor()
        try:
             cursor2.execute(sql2)
             result2 = cursor2.fetchall()
        except Exception, e:
            print e
        for row2 in result2:
            clinician_element={}
            clinician_element["npi"]= row[0]
            clinician_element["name"]= row2[0]
            clinician_element["lastname"] = row2[1]
            # Add the element to the Researcher List
            Clinitian_list.append(clinician_element)
    db_conn.close()
    return Clinitian_list


#Researcher_list =  [ {'id':1, 'name':"Carlo", 'lastname':"Torniai"},  {'id':2, 'name':"Melissa", 'lastname':"Haendel"}]
#Clinitian_list =  [ {'npi':123, 'name':"Rob", 'lastname':"Schuff"} , {'npi':456, 'name':"Simone", 'lastname':"Walld"}]

#Edges_list = [{'id':1, 'npi':123, 'label':"apostosis", 'weight':0.50},  {'id':2, 'npi':456, 'label':"appendix", 'weight':0.62}]
# I should assume to have research_id, cliniian_id, ICD / Mesh Label , strength
# Each distinct reseaarcher ID with First and LAstname + each linitian npi with attributes first and last name wil l be nodes
# The edges will be for each row adding an edg between npi and id with weight X

# SO the edge list can come up directly form the tabele
# For each row
# Build the Edge list

#Researcher_list = {}
#Clinitian_list = {}
Edges_list = build_edge_list()
Researcher_list = build_researcher_list()
Clinitian_list = build_clinitian_list()


for i in range (0, len(Researcher_list)):
    attributes = {}
    print ("Adding researcher" + str(Researcher_list[i]["name"])+ str(Researcher_list[i]["lastname"]))
    label = Researcher_list[i]["name"] + " " + Researcher_list[i]["lastname"]
    attributes["Label"] = label
    attributes["Type"] = "Researcher"
    G.add_node(int(Researcher_list[i]["id"]), attributes)

for j in range (0, len(Clinitian_list)):
    attributes = {}
    label = Clinitian_list[j]["name"] + " " + Clinitian_list[j]["lastname"]
    attributes["Label"] = label
    attributes["Type"] = "Clinician"
    G.add_node(int(Clinitian_list[j]["npi"]), attributes)

# add edges
for k in range (0, len(Edges_list)):
    print"Adding edge " + str(Edges_list[k]["id"])+ " " +  str(Edges_list[k]["npi"]) + " " + str(Edges_list[k]["weight"])
    G.add_edge(Edges_list[k]["id"], Edges_list[k]["npi"], weight=Edges_list[k]["weight"])
print G
print G.neighbors(397)
logger("Writing GraphML file")
nx.write_graphml(G, "./test_new_limit.graphml")