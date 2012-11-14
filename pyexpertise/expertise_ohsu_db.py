#!/usr/bin/python

#Insert BSD License (http://opensource.org/licenses/BSD-2-Clause)

##############################################################################
# Copyright (c) 2012, Christopher P. Barnes ( senrabc at gmail.com )
# Carlo Torniai
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

from collections import defaultdict

##############################################################################
# Summary:
# This modules uses the OHSU databases in order to give a first weighted
# expertise.
# It assumes that there is an existent view (or table) containing the unique patients
# numbers for each provider.
#
# We then evaluate he weight of each diagnosis or procedure code  by
# first calculating the percentage of each patient with each diagnosis/proc
#
# Formula: ((CODE_UNIQUE_PATIENTS/TOTAL_PATIENTS)*100) = CODE_PERCENT_PATIENTS
#
# then calculating the frequency of the code occurrence
#
# Formula: (UNIQUE_CODE_OCCUR/CODE_UNIQUE_PATIENTS) = CODE_OCCURRENCE_FREQUENCY
#
# then determining the code weight by multiplying the CODE_PERCENT_PATIENTS
# by the CODE_OCCURRENCE_FREQUENCY
#
# Formula: (CODE_PERCENT_PATIENTS * CODE_OCCURRENCE_FREQUENCY)
#
# TO DO:
# Have all the calculation happen in memory with sets of dict
##############################################################################

version = '1.0'

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



def insertrowinweigths (provider_id, code, code_flatten, code_unique_patients, code_occurrences, total_patient, \
        code_percentage_patients,  code_frequency , code_weight, version):
#===============================================================================
#   Inserts a row into the weight_codes table
#===============================================================================
    db = getDB(Connection.host, Connection.port, Connection.user, Connection.password, Connection.database)
    sql = "INSERT INTO  weight_code (provider_id, specific_icd9_code, high_level_icd_code, code_unique_patient, unique_code_occurence, total_patients, \
        code_percentage_patients,  code_frequency , code_weight, version) VALUES ('%s', '%s', \
         '%s', %d, %d, %d, %f, %f, %f, '%s')"  \
    % (provider_id, code, code_flatten, code_unique_patients, code_occurrences, total_patient, \
        code_percentage_patients,  code_frequency , code_weight, version)
    #print sql
    cursor = db.cursor()
    cursor.execute(sql)
    db.commit()
    db.close()


def providercodeweight(provider_id, total_patient):
#===============================================================================
#   Function to calculate the code weight for this provider
#   Formula: (CODE_PERCENT_PATIENTS * CODE_OCCURRENCE_FREQUENCY)
#   Currently writes a new file using the subsequent files I've created  for
#   debug / checking purposes
#===============================================================================

    print "Doing it form the database data"
    # Ok let's get the results form this query here
    db = getDB(Connection.host, Connection.port, Connection.user, Connection.password, Connection.database)
    sql = "SELECT  ctsadata.icd_simple.npi AS PROVIDERID, ctsadata.icd_simple.icd AS DX_CODE,  \
    COUNT(distinct ctsadata.icd_simple.patient_id) AS UNIQUE_PATIENTS,  \
    COUNT( ctsadata.icd_simple.icd) AS UNIQUE_CODE_OCCUR \
    FROM ctsadata.icd_simple WHERE ctsadata.icd_simple.npi ='%s' \
    GROUP BY ctsadata.icd_simple.icd \
    ORDER BY UNIQUE_PATIENTS DESC" % (provider_id)
    # Run query and get result
    cursor = db.cursor()
    try:
        cursor.execute(sql)
        result = cursor.fetchall()
    except Exception, e:
        print e
    # Loop through result
    for row in result:
        # Here get the proper variables in the results
        provider_id =  row[0]
        code= row[1]
        code_occurrences = row[2]
        code_unique_patients=int(row[3])
        code_percentage_patients =  ((code_unique_patients / float(total_patient)) * 100)
        code_frequency = (code_occurrences / float (code_unique_patients))
        code_weight= code_percentage_patients * code_frequency
        # Just get the stiring before
        if '.' in code:
            code_flatten= code[0: code.index('.')]
        else:
            code_flatten=code
        # Now do the caluclaiton I was doing before PLUS flatten the code
        #print provider_id, code, code_flatten, code_unique_patients, code_occurrences, total_patient, \
        code_percentage_patients,  code_frequency , code_weight, version

        insertrowinweigths (provider_id, code, code_flatten, code_unique_patients, code_occurrences, int(total_patient), \
        code_percentage_patients,  code_frequency , code_weight, version)
    # Close the Connection
    db.close()


def main():

    # get the list of providers and their total patients form the view in the DB
    providerTotalPatients = getPatientIdsTotalPatient('unique_patients_for_provider')

    for provider, total_patients in providerTotalPatients.items():
        # Dubug that works
        print provider, total_patients
        providercodeweight (provider, total_patients)

if __name__ == '__main__':
    main()
    # Here just try the function with this data :1043456361 154
    #providercodeweight ('1043456361',  '154')