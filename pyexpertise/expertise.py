#!/usr/bin/python

#Insert BSD License (http://opensource.org/licenses/BSD-2-Clause)

##############################################################################
# Copyright (c) 2012, Christopher P. Barnes ( senrabc at gmail.com )
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



data = defaultdict(list)
for i, row in enumerate(csv.reader(open('icdtestdata','rb'), delimiter=',')):
    if not i or not row:
        continue
    PROVIDERID, DX_CODE, UNIQUE_PATIENTS, UNIQUE_CODE_OCCUR = row
    data[PROVIDERID].append(float(UNIQUE_PATIENTS))

  
    
for PROVIDERID, UNIQUE_PATIENTS in data.iteritems():
    print 'For PROVIDERID:', PROVIDERID, 'TOTAL_PATIENTS:', sum(UNIQUE_PATIENTS)
    

    




##############################################################################
# function to calc. the % of patients w each code
# For Each provider Calculate the Percentage of Patients with each Diagnosis
# Formula: ((CODE_UNIQUE_PATIENTS/TOTAL_PATIENTS)*100) = CODE_PERCENT_PATIENTS
##############################################################################

# def percentofpatientsbycode(PROVIDER_ID):



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
    print 'main'
# providercodeweight(PROVIDER_ID)

if __name__ == '__main__':
    main()
