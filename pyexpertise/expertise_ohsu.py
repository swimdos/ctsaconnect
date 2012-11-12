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

import csv
from collections import defaultdict

##############################################################################
# Summary:
# Purpose of this module is to calculate Expertise of a Provider by finding
# the weight of each diagnosis or procedure code. The weight is determined by
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
##############################################################################








##############################################################################
#   Function to count how many patients TOTAL for each PROVIDER_ID
##############################################################################


#def totalpatients(PROVIDER_ID):


#TODO: replace all this. this is just prototyping
#Sample Data ( Later to Come from Triple Store )
# TODO: Add code to query Triple store to create this array of data

#PROVIDERID,DX_CODE,UNIQUE_PATIENTS,UNIQUE_CODE_OCCUR
#94376254889,530.1,10,11
#94376254889,530.3,20,21



#Read in the data from the csv file.
#This csv file must be in the same directory as the program
#otherwise you need to specify full path. Clean this up later.

##############################################################################
# Function that writes a csv in filename using the value returned by
# executeGetICDCountSQL
##############################################################################

def writeCSV(entry, file):
    spamwriter = csv.writer(file, delimiter='\t', quotechar='|', quoting=csv.QUOTE_MINIMAL)
    spamwriter.writerow(entry)

def readPatientIdsTotalPatient(filename):
#===============================================================================
#   It reads tNPI number for a practitioner and the unique patient IDs
#   it returns a dictionary with providr_id:unique_patient
#
#===============================================================================
    providerTotalPatients=dict()
    f = open(filename, 'rU')
    dialect = csv.Sniffer().sniff(f.readline())
    f.seek(0)
    reader = csv.DictReader(f, dialect=dialect)
    for line in reader:
        providerTotalPatients[line['PROVIDERID']]=line['UNIQUE_PATIENTS']
    return providerTotalPatients




def percentofpatientsbycode(provider_id, total_patient):
#===============================================================================
#   function to calculate the % of patients w each code
#   For Each provider Calculate the Percentage of Patients with each Diagnosis
#   Formula: ((CODE_UNIQUE_PATIENTS/TOTAL_PATIENTS)*100) = CODE_PERCENT_PATIENTS
#===============================================================================

    # read the Excel_fiel for the provider ID
    expertise_file_name='./results/'+provider_id+'_expertise.xls'
    percentage_code_file_name = './results/'+provider_id+'_percentage_codes.xls'
    percentage_code_file = open(percentage_code_file_name, 'wb')
    expertise_file = open(expertise_file_name, 'rU')
    # Intialize the ooutput file
    writeCSV(['DX_CODE','CODE_UNIQUE_PTIENTS','TOTAL_PATIENTS', 'CODE_PERC_PAT'], percentage_code_file)
    dialect = csv.Sniffer().sniff(expertise_file.readline())
    expertise_file.seek(0)
    reader = csv.DictReader(expertise_file, dialect=dialect)
    for line in reader:
        code = line['DX_CODE']
        code_unique_patients = int ( line ['UNIQUE_PATIENTS'])
        # write this in the  percentage_code_file
        code_percentage_patients =  ((code_unique_patients / float(total_patient)) * 100)
        output_row = [code, code_unique_patients, total_patient, code_percentage_patients]
        #print output_row
        # write the percentage of codes excel for each patient
        writeCSV(output_row, percentage_code_file)
    percentage_code_file.close()
    expertise_file.close()
##############################################################################
#Function to calculate the frequency of the code occurrence
# Formula: (UNIQUE_CODE_OCCUR/CODE_UNIQUE_PATIENTS) = CODE_OCCURRENCE_FREQUENCY
##############################################################################

# def freqcodeoccur(PROVIDER_ID):


##############################################################################
#Function to calculate the code weight for this provider
# Formula: (CODE_PERCENT_PATIENTS * CODE_OCCURRENCE_FREQUENCY)
##############################################################################

# def providercodeweight(PROVIDER_ID):

    # p = PROVIDER_ID
    # tp = totalpatients(p)
    # ppbc = percentofpatientsbycode(p)
    # fco = freqcodeoccur(p)

    # pcw = (ppbc * fco)

    # return pcw


# main() calls the above functions with inputs,
def main():

    # get the list of providers and their total patients
    providerTotalPatients = readPatientIdsTotalPatient('./unique_patients_per_provider.csv')

    # For each provider calculate the percentage of patients by code
    for provider, total_patients in providerTotalPatients.items():
        print provider, total_patients
        percentofpatientsbycode (provider, total_patients)


if __name__ == '__main__':
    main()
