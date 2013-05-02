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
import csv
import MySQLdb
import Connection
import networkx as nx
import pickle
from decimal import *
base_dir = "/Users/torniai/Dropbox/clinical_research_graphs/"

def extract_researcher_networks(model_files, researcher_nodes):
    for k, v in model_files.items():
    #filename= "filtered_mesh_20_icd_30_2007_2012"
        filename = model_files[k]
        input_file = base_dir + filename
        G = nx.read_gpickle(input_file)
        #G = nx.read_graphml(input_file)
        print "Evaluating model %s" % (k)
        # Below Is a test for researcher Nodes
        for current_node in researcher_nodes:
            print ("Evaluating Resrearcher %s") % (current_node)
            writenetwork(current_node, k, G)

def extract_practitioner_networks(model_files, practitioner_nodes):
    for k, v in model_files.items():
    #filename= "filtered_mesh_20_icd_30_2007_2012"
        filename = model_files[k]
        input_file = base_dir + filename
        G = nx.read_gpickle(input_file)
        #G = nx.read_graphml(input_file)
        print "Evaluating Model %s" % (k)
        # Below Is a test for researcher Nodes
        for current_node in practitioner_nodes:
            print ("Evaluating Practitioner %s") % (current_node)
            writenetworkpractitioner(current_node, k, G)


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

def writenetwork(current_node, model_name, G):
    excel_output = []
    out_base_dir ="/Users/torniai/Dropbox/clinical_research_graphs/rerun/"
    nodes = set([current_node])
    # Here check if the node is in the graph
    if current_node in G.nodes():
        print G.node[current_node]
        print G.neighbors(current_node)
        nodes.update(G.neighbors(current_node)) # extend with neighbors
        H=nx.Graph(G.subgraph(nodes).copy())
        print ("Nodes " + str(len(H.nodes())))
        print ("Edges " + str(len(H.edges())))
        center_node_first_name = H.node[int(current_node)]['First_Name']
        center_node_last_name = H.node[int(current_node)]['Last_Name']
        # Change the model number accordingly when I have the list of files
        excel_out_file = out_base_dir+center_node_last_name + "_" + center_node_first_name + "_Researcher_" + str(current_node) +"_"+ model_name +".csv"
        # Open the file
        resultFile = open(excel_out_file, 'a')
        wr = csv.writer(resultFile, dialect='excel')
        # Here add the properties to the nodes to be sorted out.
        add_nodes_properties(H)
        #Write the Excel
        # Here for each edges I will need to get form the node ID the
        for node in H.neighbors(current_node):
        # Get edge attrobutes
            #print node
            out_line = []
            term_count = 0
            # First look at curretn_node -> node
            if (H.edge[current_node][node]['weight'] != None):
                #print H.edge[current_node][node]
                weight = H.edge[current_node][node]['weight']
            else:
                weight = H.edge[node][current_node]['weight']
                # Get the key
            #print weight
            if (H.edge[current_node][node]['key'] != None):
                key = H.edge[current_node][node]['key']
            else:
                key = H.edge[node][current_node]['key']
            #print key
            # Get firstname and Lastname

            if (int(node) in H.nodes()):
                first_name = H.node[int(node)]['First_Name']
            else:
                first_name = ''
            #print first_name
            if (int(node) in H.nodes()):
                last_name = H.node[int(node)]['Last_Name']
            else:
                last_name = ''
            # Now I have an excel row to write node (npi) , Lat_name, first_name, weight, key

            # Here I would need to get the scival ID for the first name and last name for the guy
            # And if there is a match get to the
            db_conn = getDB("localhost", 3306, "root", "grisu#71", "scivaltest")
            sql = "SELECT `term_count` FROM `authoer_mesh_2007_2012` WHERE `expert_scival_id` = %d \
            AND mesh_descriptor LIKE '%s'"  %(int(current_node), key)

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
                term_count = row[0]
            adjusted_weight = Decimal(term_count)*Decimal(weight)
            out_line.append(node)
            out_line.append(last_name)
            out_line.append(first_name)
            out_line.append(weight)
            out_line.append(str(adjusted_weight))
            out_line.append(term_count)
            out_line.append(key)
            excel_output.append(out_line)
            db_conn.close()
        wr.writerows(excel_output)
        ## Write the graph down
        graph_out_name =  out_base_dir+center_node_last_name + "_" + center_node_first_name + "_Researcher_" + str(current_node) +"_"+ model_name
        nx.write_gpickle(H, graph_out_name+".pkl")
        nx.write_graphml(H, graph_out_name+".graphml")

    else:
        return


def writenetworkpractitioner(current_node, model_name, G):
    excel_output = []
    out_base_dir ="/Users/torniai/Dropbox/clinical_research_graphs/rerun/"
    current_node = str(current_node)
    nodes = set([current_node])
    # Here check if the node is in the graph
    if current_node in G.nodes():
        print G.node[int(current_node)]
        print G.neighbors(current_node)
        nodes.update(G.neighbors(current_node)) # extend with neighbors
        H=nx.Graph(G.subgraph(nodes).copy())
        print ("Nodes " + str(len(H.nodes())))
        print ("Edges " + str(len(H.edges())))
        print G.node[int(current_node)]['First_Name']
        center_node_first_name = G.node[int(current_node)]['First_Name']
        center_node_last_name = G.node[int(current_node)]['Last_Name']
        # Change the model number accordingly when I have the list of files
        excel_out_file = out_base_dir+center_node_last_name + "_" + center_node_first_name + "_Practitioner_" + str(current_node) +"_"+ model_name +".csv"
        # Open the file
        resultFile = open(excel_out_file, 'a')
        wr = csv.writer(resultFile, dialect='excel')
        # Here add the properties to the nodes to be sorted out.
        add_nodes_properties(H)
        for node in H.neighbors(current_node):
        # Get edge attrobutes
            #print node
            out_line = []
            term_count = 0
            # First look at curretn_node -> node
            if (H.edge[current_node][node]['weight'] != None):
                #print H.edge[current_node][node]
                weight = H.edge[current_node][node]['weight']
            else:
                weight = H.edge[node][current_node]['weight']
                # Get the key
            #print weight
            if (H.edge[current_node][node]['key'] != None):
                key = H.edge[current_node][node]['key']
            else:
                key = H.edge[node][current_node]['key']
            #print key
            # Get firstname and Lastname

            if (int(node) in H.nodes()):
                first_name = H.node[int(node)]['First_Name']
            else:
                first_name = ''
            #print first_name
            if (int(node) in H.nodes()):
                last_name = H.node[int(node)]['Last_Name']
            else:
                last_name = ''
            # Now I have an excel row to write node (npi) , Lat_name, first_name, weight, key

            # At this point i need to query for the mesh_term in the table
            # Get the occurences of Mesh Terms
            db_conn = getDB("localhost", 3306, "root", "grisu#71", "scivaltest")
            sql = "SELECT `term_count` FROM `authoer_mesh_2007_2012` WHERE `expert_scival_id` = %d \
            AND mesh_descriptor LIKE '%s'"  %(int(node), key)

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
                term_count = row[0]
            adjusted_weight = Decimal(term_count)*Decimal(weight)
            out_line.append(node)
            out_line.append(last_name)
            out_line.append(first_name)
            out_line.append(weight)
            out_line.append(str(adjusted_weight))
            out_line.append(term_count)
            out_line.append(key)
            excel_output.append(out_line)
            db_conn.close()
        wr.writerows(excel_output)
        ## Write the graph down
        graph_out_name =  out_base_dir+center_node_last_name + "_" + center_node_first_name + "_Practitioner_" + str(current_node) +"_"+ model_name
        nx.write_gpickle(H, graph_out_name+".pkl")
        nx.write_graphml(H, graph_out_name+".graphml")

    else:
        return



def add_nodes_properties(H):
    db_conn = getDB("localhost", 3306, "root", "grisu#71", "scivaltest")
    for node in H.nodes():
        # Get the distinct list of sciva_id_s in the connections_mesh_icd
        sql2= "SELECT provider_first_name, provider_last_name from practioner WHERE npi = %s" %(node)
        #print sql2
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
            #print ("Adding researcher " + str(clinician_element["name"])+ str(clinician_element["lastname"]))
            label = clinician_element["name"] + " " + clinician_element["lastname"]
            attributes["Label"] = label
            attributes["Type"] = "Clinician"
            attributes["First_Name"] = clinician_element["name"]
            attributes["Last_Name"] =  clinician_element["lastname"]
            H.add_node(int(clinician_element["npi"]), attributes)
    db_conn.close()


# Main



#
#
#    # get the weight and the key
#    weight = edge['weight']
#    key = edge["key"]
#    clinician_id  = edge[1]
#
#    # Get the first name
#    first_name = G.nodes(data=True)[clinician_id]["First_Name"]
def main():
    verbose = False
    safe= False


    # Important here we have the name of models are according tho the last enum in this google doc
    model_files = {'model_1':'unfiltered_2007_2012.pkl', 'model_2':'unfiltered_full_icd_codes_2007_2012.pkl', 'model_3':'filtered_mesh_20_icd_20_2007_2012.pkl', 'model_4':"filtered_mesh_20_full_icd_20_2007_2012.pkl"}
    researcher_nodes = [16, 24, 3014, 631, 632, 708, 837, 935, 1171, 1185, 1237, 1359, 1387, 1523, 1572, 1725, 1812, 1919, 188, 276, 471, 3090, 884, 887, 949, 1391, 1492, 1613]
    practitioner_nodes = [1104831619, 1679781959, 1346337391, 1164430690 ,1699783167, 1295741783, 1629086145, 1346256898, 1770598278, 1295743722 , 1174531644]


    #model_files = {'model_1':'unfiltered_2007_2012.pkl'}
    #researcher_nodes = [24]
    #extract_researcher_networks(model_files, researcher_nodes)
    #practitioner_nodes = [1104831619]

    extract_practitioner_networks(model_files, practitioner_nodes)
    # Uncomment for researcher
    extract_researcher_networks(model_files, researcher_nodes)


if __name__ == '__main__':
   main()
#    writeExpertiseReport('1508871005')
#    writeTagBag('1508871005')
#    writeNewExpertiseReport('1508871005')
#  checkICDTable()
#writeNewTagBag('1508871005')
