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

import MySQLdb
import Connection
import csv


#===============================================================================
#   This scripts uses the weight_code table to generate reports for each
#   practitioner based on the top 10 weighted ICD
#===============================================================================
#Defines the max numbers of code we are considering



def writeTagBag(provider):
    print "Building  Tag_Bag"
    results = buildTagBag(provider)
    filenametag = "./results/%s_tag.xls" % (provider)
    csvfiletag = open(filenametag, 'wb')
    # Write first row result
    writeCSV(['ICD_CODE', 'TERM', 'CODE WEIGHT'], csvfiletag)
    for row in results:
        writeCSV(row, csvfiletag)

def writeExpertiseReport(provider):
    print "Building  expertise report"
    results = buildExpertiseReport(provider)
    filename = "./results/%s_expertise.xls" % (provider)
    csvfile = open(filename, 'wb')
    # Write first row result
    writeCSV(['PROVIDER_ID', 'ICD_CODE', 'CODE LABEL', 'CODE WEIGHT', 'ICD_SPECIFIC_CODE', 'HIGH_LEVEL_CODE', 'TOTAL_PATIENTS_PER_CODE', 'TOTAL_CODE_USE', 'TOTAL_PATiENTS', 'PERCENTAGE_PATIENTS', 'CODE_FREQUENCY'], csvfile)
    for row in results:
        writeCSV(row, csvfile)

max_code_num=2000000


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

    for provider, total_patients in providerTotalPatients.items():
        writeExpertiseReport(provider)

    # Generate tag bags:
    for provider, total_patients in providerTotalPatients.items():
        writeTagBag(provider)

if __name__ == '__main__':
    main()
   #writeTagBag('1508871005')