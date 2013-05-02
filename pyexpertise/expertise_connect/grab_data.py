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
# This script get some data form the ctsaconnect dB
#
####
verbose = True
safe= False
import MySQLdb
import Connection
import csv

from collections import defaultdict

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

def get_icd_data():

    print "Extracting data"
    # Ok let's get the results form this query here
    db = getDB(Connection.host, Connection.port, Connection.user, Connection.password, Connection.database)

    # Trying to get the data from the freaking total codes:

    #sql = "SELECT weight_code.provider_id, weight_code.specific_icd_9_code, weight_code.high_level_icd_code, weight_code.code_weigth from icd_expertise"

    # Original SQL with just the top ten terms below:

    sql = "SELECT icd_expertise.provider_id,icd_expertise.icd_code,icd_expertise.code_weigth from icd_expertise"

    # Run query and get result
    cursor = db.cursor()
    try:
        cursor.execute(sql)
        result = cursor.fetchall()
    except Exception, e:
        print e
    db_conn = getDB("localhost", 3306, "root", "grisu#71", "scivaltest")
    for row in result:
        # Here get the proper variables in the results
        npi =  row[0]
        icd_code= row[1]
        weight = row[2]
        # Now inser this value in the local table

        cursor = db_conn.cursor()
        sql = "INSERT  INTO icd_data (npi, icd_code, weight) \
           VALUES ('%s', '%s', '%s')" % \
           (npi, icd_code, weight)
        if verbose:
            print sql.encode('ascii', 'ignore')
            # Insert the row
        if not safe:
            executeQuery(db_conn, sql)
        # Now do the caluclaiton I was doing before PLUS flatten the code
        #print provider_id, code, code_flatten, code_unique_patients, code_occurrences, total_patient, \
        #code_percentage_patients,  code_frequency , code_weight, version
    db_conn.close()

def get_total_icd_data():

    print "Extracting data"
    # Ok let's get the results form this query here
    db = getDB(Connection.host, Connection.port, Connection.user, Connection.password, Connection.database)

    # Trying to get the data from the freaking total codes:

    sql = "SELECT weight_code.provider_id, weight_code.specific_icd9_code, weight_code.high_level_icd_code, weight_code.code_weight from weight_code"

    # Original SQL with just the top ten terms below:

    #sql = "SELECT icd_expertise.provider_id,icd_expertise.icd_code,icd_expertise.code_weigth from icd_expertise"

    # Run query and get result
    cursor = db.cursor()
    try:
        cursor.execute(sql)
        result = cursor.fetchall()
    except Exception, e:
        print e
    db_conn = getDB("localhost", 3306, "root", "grisu#71", "scivaltest")
    for row in result:
        # Here get the proper variables in the results
        npi =  row[0]
        icd_code= row[1]
        icd_high_level_code = row[2]
        weight = row[3]
        # Now inser this value in the local table

        cursor = db_conn.cursor()
        sql = "INSERT  INTO icd_data_total (npi, icd_code, icd_high_level_code, weight) \
           VALUES ('%s', '%s', '%s', '%s')" % \
           (npi, icd_code, icd_high_level_code, weight)
        if verbose:
            print sql.encode('ascii', 'ignore')
            # Insert the row
        if not safe:
            executeQuery(db_conn, sql)
        # Now do the caluclaiton I was doing before PLUS flatten the code
        #print provider_id, code, code_flatten, code_unique_patients, code_occurrences, total_patient, \
        #code_percentage_patients,  code_frequency , code_weight, version
    db_conn.close()


def get_practitioner_data():
    db = getDB(Connection.host, Connection.port, Connection.user, Connection.password, Connection.database)
    print "Extracting practitioner data"
    # Ok let's get the results form this query here
    sql2 = "SELECT  `ohsu_npi`.`NPI`, `ohsu_npi`.`Provider_Last_Name_(Legal_Name)`, `ohsu_npi`.`Provider_First_Name` \
    from ohsu_npi"
    # Run query and get result
    cursor2 = db.cursor()
    try:
        cursor2.execute(sql2)
        result2 = cursor2.fetchall()
    except Exception, e:
        print e
    db_conn = getDB("localhost", 3306, "root", "grisu#71", "scivaltest")
    for row2 in result2:
        # Here get the proper variables in the results
        npi =  row2[0]
        provider_last_name= row2[1]
        provider_first_name = row2[2]
        # Now inser this value in the local table

        cursor3 = db_conn.cursor()
        sql3 = "INSERT  INTO practioner (npi, provider_last_name, provider_first_name) \
           VALUES ('%s', '%s', '%s')" % \
           (npi, provider_last_name, provider_first_name)
        if verbose:
            print sql3.encode('ascii', 'ignore')
            # Insert the row
        if not safe:
            executeQuery(db_conn, sql3)
        # Now do the caluclaiton I was doing before PLUS flatten the code
        #print provider_id, code, code_flatten, code_unique_patients, code_occurrences, total_patient, \
        #code_percentage_patients,  code_frequency , code_weight, version
    db_conn.close()
    db.close()

def main():
    # Query the data form the
    #get_icd_data()
    get_total_icd_data()
    #get_practitioner_data()
    # Close the Connection
    db.close()
    #write_icd_data()

if __name__ == '__main__':
    main()
