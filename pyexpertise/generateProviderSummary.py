#!/usr/bin/python

#Insert BSD License (http://opensource.org/licenses/BSD-2-Clause)

##############################################################################
# Copyright (c) 2012, Carlo Torniai ( carlotonriai at gmail.com )
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

##############################################################################
# Summary:
# This module executes a stored procedure to generate  csv files to be used
# by expertise.py and other scripts.
# It requires that a COnnection.py exists (See Connection.sample.py for
#
# it requires to have installed the MySQLdb module -> easy_install MySQL-python
# it currently uses the database schema for OHSU ICD9 data
# but the queries can be adapted to other schemas
##############################################################################



##############################################################################
# Function that writes a csv in filename using the value returned by
# executeGetICDCountSQL
##############################################################################
def writeCSV(entry, filename):
    csvfile = open(filename, 'wb')
    spamwriter = csv.writer(csvfile, delimiter='\t', quotechar='|', quoting=csv.QUOTE_MINIMAL)
    spamwriter.writerow(entry)

##############################################################################
# function that returns a DB connection
##############################################################################
def getDB(host, port,  user, password, database):
    db = MySQLdb.connect(host=host, port=port, user=user, passwd=password, db=database)
    return db


##############################################################################
# Execute the query to get data about a provider as follows:
# icd_code    icd_code_lable    icd_code_occurrrences
##############################################################################
def executeGetICDCountSQL(npi):
    queryresults = []
    db = getDB(Connection.host, Connection.port, Connection.user, Connection.password, Connection.database)
    sql = "SELECT  ctsadata.icd_simple.icd, icd9_code.term, COUNT(ctsadata.icd_simple.icd) AS Occurrence \
FROM ctsadata.icd_simple , icd9_code \
WHERE ctsadata.icd_simple.npi='%s' AND icd9_code.code = ctsadata.icd_simple.icd  \
GROUP BY ctsadata.icd_simple.icd \
ORDER BY Occurrence DESC" % (npi)
# Run query and get result
    cursor = db.cursor()
    try:
        cursor.execute(sql)
        result = cursor.fetchall()
    except Exception, e:
        print e
    # Loop through result
    for row in result:
        queryresults.append(row)

    # Close the Connection
    db.close()
    return queryresults


##############################################################################
# Execute the stored Procedure getICDCount that return all the unique ID
# for providers the source for the query can be found in getUniqueProviders.sql
# under ./queries
##############################################################################
def getUniqueProviders():
    provider_npi=[]
    db = getDB(Connection.host, Connection.port, Connection.user, Connection.password, Connection.database)
    sql = "call getUniqueProviders()"

    # Run query and get result
    cursor = db.cursor()
    try:
        print 'Calling getICDCount Stored Procedure'
        cursor.execute(sql)
        result = cursor.fetchall()
    except Exception, e:
        print e
    # Loop through result
    for row in result:
        provider_npi.append(row)
    return provider_npi


def main():
    print 'Starting execution'
    providerList = getUniqueProviders()
    for provider in providerList:
        print 'Getting data for provider %s' % (provider)
        icd_results = executeGetICDCountSQL(provider)
        filename="./results/%s.xls" % (provider)
        for icd_entry in icd_results:
            writeCSV(provider, filename)
    print 'Done'

if __name__ == '__main__':
    main()
