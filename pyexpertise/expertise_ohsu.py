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
# This modules uses the OHSU csv files in order to calculate Expertise for Provider.
# Currently at each steps are generate excel files for debugging checking purposes
#
# First of all we find t
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
# TO DO:
# Have all the calculation happen in memory with sets of dict
##############################################################################



def writeCSV(entry, file):
#===============================================================================
#   Function that writes a csv in filename using the value returned by
#   executeGetICDCountSQL
#
#===============================================================================
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
    writeCSV(['DX_CODE','CODE_UNIQUE_PATIENTS','TOTAL_PATIENTS', 'CODE_PERC_PAT'], percentage_code_file)
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
        # write the percentage for each code in an excel for each practitioner
        writeCSV(output_row, percentage_code_file)
    percentage_code_file.close()
    expertise_file.close()



def freqcodeoccur(provider_id, total_patient):
#===============================================================================
#   Function to calculate the frequency of the code occurrence
#   Formula: (UNIQUE_CODE_OCCUR/CODE_UNIQUE_PATIENTS) = CODE_OCCURRENCE_FREQUENCY
#   Currently writes a new file using the subsequent files I've created again for
#   debug / checking purposes
#===============================================================================

# read the Excel_fiel for the provider ID
    expertise_file_name='./results/'+provider_id+'_expertise.xls'
    frequency_code_file_name = './results/'+provider_id+'_frequency_codes.xls'
    frequency_code_file = open(frequency_code_file_name, 'wb')
    expertise_file = open(expertise_file_name, 'rU')
    # Intialize the ooutput file
    writeCSV(['PROVIDER_ID', 'DX_CODE','CODE_UNIQUE_PATIENTS', 'UNIQUE_CODE_OCCUR','TOTAL_PATIENTS', 'CODE_PERC_PAT', 'CODE_FREQ'], frequency_code_file)
    dialect = csv.Sniffer().sniff(expertise_file.readline())
    expertise_file.seek(0)
    reader = csv.DictReader(expertise_file, dialect=dialect)
    for line in reader:
        code = line['DX_CODE']
        code_occurrences= int(line ['UNIQUE_CODE_OCCUR'])
        code_unique_patients = int ( line ['UNIQUE_PATIENTS'])
        # write this in the  percentage_code_file
        code_percentage_patients =  ((code_unique_patients / float(total_patient)) * 100)
        code_frequency = (code_occurrences / float (code_unique_patients))
        output_row = [provider_id, code, code_unique_patients, code_occurrences, total_patient, code_percentage_patients, code_frequency]
        #print output_row
        # write the percentage for each code in an excel for each practitioner
        writeCSV(output_row, frequency_code_file)
    frequency_code_file.close()
    expertise_file.close()





def providercodeweight(provider_id, total_patient):
#===============================================================================
#   Function to calculate the code weight for this provider
#   Formula: (CODE_PERCENT_PATIENTS * CODE_OCCURRENCE_FREQUENCY)
#   Currently writes a new file using the subsequent files I've created  for
#   debug / checking purposes
#===============================================================================
    # p = PROVIDER_ID
    # tp = totalpatients(p)
    # ppbc = percentofpatientsbycode(p)
    # fco = freqcodeoccur(p)

    # pcw = (ppbc * fco)

    # return pcw

    expertise_file_name='./results/'+provider_id+'_expertise.xls'
    expertise_file = open(expertise_file_name, 'rU')
    code_weight_file_name = './results/'+provider_id+'_weight_code.xls'
    code_weight_file= open(code_weight_file_name, 'wb')

    # Intialize the ooutput file
    writeCSV(['PROVIDER_ID', 'DX_CODE','CODE_UNIQUE_PATIENTS', 'UNIQUE_CODE_OCCUR','TOTAL_PATIENTS', 'CODE_PERC_PAT', 'CODE_FREQ', 'CODE_WEIGHT'], code_weight_file)
    dialect = csv.Sniffer().sniff(expertise_file.readline(), )
    expertise_file.seek(0)
    reader = csv.DictReader(expertise_file, dialect=dialect)
    for line in reader:
        code = line['DX_CODE']
        code_occurrences= int(line ['UNIQUE_CODE_OCCUR'])
        code_unique_patients = int ( line ['UNIQUE_PATIENTS'])
        # write this in the  percentage_code_file
        code_percentage_patients =  ((code_unique_patients / float(total_patient)) * 100)
        code_frequency = (code_occurrences / float (code_unique_patients))
        code_weight= code_percentage_patients * code_frequency
        output_row = [provider_id, code, code_unique_patients, code_occurrences, total_patient, code_percentage_patients, code_frequency, code_weight]
        #print output_row
        # write the percentage for each code in an excel for each practitioner
        writeCSV(output_row, code_weight_file)
    code_weight_file.close()
    expertise_file.close()


def main():

    # get the list of providers and their total patients
    providerTotalPatients = readPatientIdsTotalPatient('./unique_patients_per_provider.csv')

    # For each provider calculate the percentage of patients by code
    for provider, total_patients in providerTotalPatients.items():
        print provider, total_patients

        # Calculate the percentage of patients by code
        #percentofpatientsbycode (provider, total_patients)

        # Write the combined file with code percentages and codes frequency
        #freqcodeoccur(provider, total_patients)

        # Generatet the comulative file for each patient
        providercodeweight(provider, total_patients)

if __name__ == '__main__':
    main()
