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
## Creates a tabel expertise_data_set_sample with first_name, last_name, npi and scival_id for the 37 people in pur dataset for the experiments
## Out form an excel containing lastname and firstname
##
import csv
import MySQLdb
import Connection
verbose = True
safe = True
def writeCSV(entry, file):
##############################################################################
# Function that writes a csv in filename using the value returned by
# executeGetICDCountSQL
##############################################################################
    spamwriter = csv.writer(file, delimiter='\t', quotechar='|', quoting=csv.QUOTE_MINIMAL)
    spamwriter.writerow(entry)

def  writeall(hierarchy_list, filename):
#===============================================================================
#  writes hierarchies to a spreadsheet
#===============================================================================
    csvfile =  open(filename, 'wb')
    for hierarchy in hierarchy_list:
        writeCSV(hierarchy, csvfile)

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


def query_expert(lname):
    # Return a structure with first_name, last_name and the scival_id_experts for all the
    db_conn = getDB("localhost", 3306, "root", "grisu#71", "scivaltest")

    expert_list=[]
    sql = "SELECT  `expert_scival_id`, `first_name`, `last_name` FROM `expert` WHERE `last_name` LIKE '%s'" %(MySQLdb.escape_string(lname))
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
        expert_element={}
        expert_element["expert_scival_id"]= row[0]
        expert_element["first_name"]= row[1]
        expert_element["last_name"] = row[2]
        expert_list.append(expert_element)
    db_conn.close()
    return expert_list

def query_clinicians(lname):
    # Return a structure with first_name, last_name and the scival_id_experts for all the
    db_conn = getDB("localhost", 3306, "root", "grisu#71", "scivaltest")

    expert_list=[]
    sql = "SELECT `npi`, `provider_last_name`, `provider_first_name` FROM `practioner` WHERE `provider_last_name` LIKE '%s'" %(MySQLdb.escape_string(lname))
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
        expert_element={}
        expert_element["npi"]= row[0]
        expert_element["first_name"]= row[1]
        expert_element["last_name"] = row[2]
        # Add the element to the Researcher List
        expert_list.append(expert_element)
    db_conn.close()
    return expert_list

last_names=['Aicher', 'Alkayed' ,'Bahjat','Bourdette','Cetas', 'Ellison', 'Grigsby', 'Grompe','Hamilton','Hayes','Janowsky','Jensen','Johnson','Kaul','Kaye','Klein','Koeller','McEvoy','Meshul','Messamore','Mitchell','Orloff','O\'Rourke','McCarty','Pavel','Roberts','Rotwein','Scanlan','Sears','Sheppard','Stouffer','Pejovic','Tsukamoto','Vandenbark','Wiesen','Hoffman','Wright']
output_record=[]
out_line = []
# For each row in the file read the last name
expert_filename= "./expert_set_experiment.csv"
clinician_filename= "./clinician_set_expertiment.csv"
clinician_list=[]
expert_list=[]
resultFile = open(expert_filename,'a')
wr = csv.writer(resultFile, dialect='excel')

clinicianFile = open(clinician_filename, 'a')
wrc = csv.writer(clinicianFile, dialect='excel')
excel_output = []
for lname in last_names:
    expert_entries = query_expert(lname)
    clinician_entries = query_clinicians(lname)
    expert_list.append(expert_entries)
    #print expert_entries
   #for expert in expert_entries:
    #    expert_list.append(expert)
    #for clinician in clinician_entries:
     #   clinician_list.append(clinician)

#    for expert in expert_entries:
#            excel_output = []
#            out_line =[]
#            out_line.append(expert["expert_scival_id"])
#            out_line.append(expert["first_name"])
#            out_line.append(expert["last_name"])
#            excel_output.append(out_line)
#    print (excel_output)
#    wr.writerows(excel_output)

    for clinican in clinician_entries:
            excel_output = []
            out_line =[]
            out_line.append(clinican["npi"])
            out_line.append(clinican["first_name"])
            out_line.append(clinican["last_name"])
            excel_output.append(out_line)
    print (excel_output)
    wrc.writerows(excel_output)
    #Write a CSV with these entries, it and then create the table by reading it

# Check if we have last name in the expert_table if so add  for EACH result last_name, first_name and scival_expert_id




#Check if we have last name in the practioner table and if so add first_name, last_name