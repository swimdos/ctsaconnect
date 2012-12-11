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


#===============================================================================
#   This scripts uses the weight_code table to generate reports for each
#   practitioner based on the top 10 weighted ICD
#   There is a method to create the same data in a tabel too
#===============================================================================

import MySQLdb
import Connection
import csv

#Defines the max numbers of code we are considering
max_code_num=1000000000


createCVS = False
createTable= True
wrtieReports = False

def writeTagBag(provider):
#===============================================================================
#   Writes a CSV containing the top 10 ICD values for a provider
#===============================================================================
    print "Building  Tag_Bag for %s" %(provider)
    results = buildTagBag(provider)
    filenametag = "./results/%s_tag.xls" % (provider)
    csvfiletag = open(filenametag, 'wb')
    writeCSV(['ICD_CODE', 'TERM', 'CODE WEIGHT'], csvfiletag)
    for row in results:
        writeCSV(row, csvfiletag)

def writeNewTagBag(provider):
#===============================================================================
#   Writes a CSV containing the top ICD values for a provider.
#   It aggregates equal codes in only one entry adding the weight
#===============================================================================
    print "Building  Tag_Bag for %s" %(provider)
    results = buildTagBag(provider)
    filenametag = "./results/%s_tag_new.xls" % (provider)
    csvfiletag = open(filenametag, 'wb')
    # Write first row result
    print results
    new_results=[]
    skip = set()
    for i in range(0, len(results)):
        replaced=False
        new_weight=0
        if i not in skip:
            new_weight= new_weight+results[i][2]
            for p in range(i+1, len(results)):
                if p not in skip:
                    #print "Comparing " + results[i][0] + " with " + results[p+1][0]
                    if results[i][0]==results[p][0]:
                        #print "Same"
                        # Calculate the new weight and add the new element to the new_results
                        new_weight = new_weight+results[p][2]
                        new_entry= (results[i][0], results[i][1], new_weight)
                        replaced=True
                        skip.add(p)
            if not replaced:
                new_results.append(results[i])
            else:
                new_results.append(new_entry)

    #print skip
    if createCVS:
        writeCSV(['ICD_CODE', 'TERM', 'CODE WEIGHT'], csvfiletag)
        for row in new_results:
            writeCSV(row, csvfiletag)
        print new_results

    if createTable:
        for row in new_results:
        # Insert the vale in the ICD
            sql_insert = "INSERT into ctsadata.icd_expertise(provider_id, icd_code, term, code_weigth, version)  \
        VALUES (%s, %s, '%s', %f, %s)" % (provider, row[0], row[1], float(row[2]), "1.0")
            print sql_insert
            db = getDB(Connection.host, Connection.port, Connection.user, Connection.password, Connection.database)
            cursor = db.cursor()
            try:
                cursor.execute(sql_insert)
            except Exception, e:
                print e

        # Now insert the values here
            db.close()

def checkICDTable():
    # If crateDB is true drop and create a table icd_expertise
    if createTable:
        db = getDB(Connection.host, Connection.port, Connection.user, Connection.password, Connection.database)
        # First drop the tabel if exists
        sql_drop = "DROP TABLE IF EXISTS ctsadata.icd_expertise; "
        cursor_drop = db.cursor()
        try:
            cursor_drop.execute(sql_drop)
        except Exception, e:
            print e

        sql_create="CREATE TABLE ctsadata.icd_expertise (\
      provider_id varchar(64), \
      icd_code varchar(45), \
      term varchar(64), \
      code_weigth float(12), \
      version varchar(64) \
    );"

        cursor = db.cursor()
        try:
            cursor.execute(sql_create)
        except Exception, e:
            print e

        # Now insert the values here
        db.close()



def writeNewExpertiseReport(provider):
#===============================================================================
#   Writes a CSV containing all the info abotu ICD ordered by ICD weight
#===============================================================================
    print "Building  expertise report for %s" %(provider)
    results = buildExpertiseReport(provider)
    filename = "./results/%s_expertise_new.xls" % (provider)
    csvfile = open(filename, 'wb')
    # Write first row result
    print results
    new_results=[]
    skip = set()
    for i in range(0, len(results)):
        new_weight=0
        replaced=False
        if i not in skip:
            new_weight= new_weight+results[i][3]
            for p in range(i+1, len(results)):
                if p not in skip:
                    print "Comparing " + results[i][1] + " with " + results[p][1]
                    if results[i][1]==results[p][1]:
                        print "Same"
                        # Calculate the new weight and add the new element to the new_results
                        new_weight= new_weight+results[p][3]
                        new_entry= (results[i][0], results[i][1], results[i][2], new_weight, results[i][4], results[i][5], results[i][6], results[i][7], results[i][8], results[i][9], results[i][10])
                        replaced=True
                        skip.add(p)
            if not replaced:
            # Add the element
                new_results.append(results[i])
            else:
                new_results.append(new_entry)

    #print skip
    writeCSV(['PROVIDER_ID', 'ICD_CODE', 'CODE LABEL', 'CODE WEIGHT', 'ICD_SPECIFIC_CODE', 'HIGH_LEVEL_CODE', 'TOTAL_PATIENTS_PER_CODE', 'TOTAL_CODE_USE', 'TOTAL_PATIENTS', 'PERCENTAGE_PATIENTS', 'CODE_FREQUENCY'], csvfile)
    for row in new_results:
        writeCSV(row, csvfile)
    #print new_results

def writeExpertiseReport(provider):
#===============================================================================
#   Writes a CSV containing all the info abotu ICD ordered by ICD weight
#===============================================================================
    print "Building  expertise report for %s" %(provider)
    results = buildExpertiseReport(provider)
    filename = "./results/%s_expertise.xls" % (provider)
    csvfile = open(filename, 'wb')
    # Write first row result
    writeCSV(['PROVIDER_ID', 'ICD_CODE', 'CODE LABEL', 'CODE WEIGHT', 'ICD_SPECIFIC_CODE', 'HIGH_LEVEL_CODE', 'TOTAL_PATIENTS_PER_CODE', 'TOTAL_CODE_USE', 'TOTAL_PATiENTS', 'PERCENTAGE_PATIENTS', 'CODE_FREQUENCY'], csvfile)
    for row in results:
        writeCSV(row, csvfile)


def buildExpertiseReport(provider):
#===============================================================================
#   Queries the weight_code table to generate the expertise report file
#===============================================================================
    queryresults=[]
    db = getDB(Connection.host, Connection.port, Connection.user, Connection.password, Connection.database)

    sql = "SELECT weight_code.provider_id, icd9_code.code,  icd9_code.term, weight_code.code_weight, \
    weight_code.specific_icd9_code, weight_code.high_level_icd_code, weight_code.unique_patients, \
    weight_code.total_code_use, \
    weight_code.total_patients, weight_code.patients_percentage, weight_code.code_frequency \
    from icd9_code, weight_code \
    WHERE weight_code.provider_id = %s AND \
    icd9_code.code = weight_code.high_level_icd_code \
    ORDER BY weight_code.code_weight DESC \
    LIMIT %d " %(provider, max_code_num)

    cursor = db.cursor()
    try:
        cursor.execute(sql)
        result = cursor.fetchall()
    except Exception, e:
        print e
    # Loop through result
    for row in result:
        queryresults.append(row)

    return queryresults

def buildTagBag(provider):
#===============================================================================
#   Queries the weight_code table to generate the set of 10 most representative terms
#===============================================================================
    queryresults=[]
    db = getDB(Connection.host, Connection.port, Connection.user, Connection.password, Connection.database)
    sql= "SELECT icd9_code.code,  icd9_code.term, weight_code.code_weight from icd9_code, weight_code \
    WHERE weight_code.provider_id = %s AND \
    icd9_code.code = weight_code.high_level_icd_code \
    ORDER BY weight_code.code_weight DESC \
    LIMIT 10" %(provider)
    cursor = db.cursor()
    try:
        cursor.execute(sql)
        result = cursor.fetchall()
    except Exception, e:
        print e
    # Loop through result
    for row in result:
        queryresults.append(row)

    return queryresults

def getDB(host, port,  user, password, database):
#===============================================================================
#   Function that returns a DB connection
#
#===============================================================================
    db = MySQLdb.connect(host=host, port=port, user=user, passwd=password, db=database)
    return db


def writeCSV(entry, file):
#===============================================================================
#   Function that writes a csv in filename using the value returned by
#   executeGetICDCountSQL
#
#===============================================================================
    spamwriter = csv.writer(file, delimiter='\t', quotechar='|', quoting=csv.QUOTE_MINIMAL)
    spamwriter.writerow(entry)


def getPatientIdsTotalPatient(unique_patient_view):
#===============================================================================
#   It reads NPI number for a practitioner and the unique patient IDs
#   from the view unique_patient_view in the DB
#
#===============================================================================
    providerTotalPatients=dict()
    db = getDB(Connection.host, Connection.port, Connection.user, Connection.password, Connection.database)
    sql = "SELECT * from  %s;" %(unique_patient_view)
    #print sql
    # Run query and get result
    cursor = db.cursor()
    try:
        cursor.execute(sql)
        result = cursor.fetchall()
    except Exception, e:
        print e
    # Loop through result
    for row in result:
        # Get the queryresults.append(row)
        providerTotalPatients[str(row[0])]=row[1]

    # Close the Connection
    db.close()
    return providerTotalPatients

def main():

    providerTotalPatients = getPatientIdsTotalPatient('unique_patients_for_provider')

    if wrtieReports:
        for provider, total_patients in providerTotalPatients.items():
            #writeExpertiseReport(provider)
            writeNewExpertiseReport(provider)
            pass

    # Generate tag bags:
    if createTable:
        checkICDTable();

    for provider, total_patients in providerTotalPatients.items():
        #writeTagBag(provider)
        writeNewTagBag(provider)

if __name__ == '__main__':
   main()
#    writeExpertiseReport('1508871005')
#    writeTagBag('1508871005')
#    writeNewExpertiseReport('1508871005')
#  checkICDTable()
#writeNewTagBag('1508871005')