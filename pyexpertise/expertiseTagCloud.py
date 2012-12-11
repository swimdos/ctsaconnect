from tagCloud import *
import MySQLdb
import Connection
import csv

# Creates aTagcloud for a user a user
verbose = True

def getProvidersfromCSV(filename):
    providers=[]
    f = open(filename, 'rU')
    dialect = csv.Sniffer().sniff(f.readline())
    f.seek(0)
    reader = csv.DictReader(f, dialect=dialect)
    for line in reader:
        print line
        providers.append(line['npi'])
    return providers

def getProviders():
    providers=[]
    # Ok let's get the results form this query here
    db = getDB(Connection.host, Connection.port, Connection.user, Connection.password, Connection.database)
    sql = "SELECT distinct npi from icd_simple"
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
        # Build the first last name thing
        providers.append(row[0])
    db.close()
    return providers


def getDB(host, port,  user, password, database):
#===============================================================================
#   Function that returns a DB connection
#
#===============================================================================
    db = MySQLdb.connect(host=host, port=port, user=user, passwd=password, db=database)
    return db

def getProviderName(provider_id):
    name=""
    # Ok let's get the results form this query here
    db = getDB(Connection.host, Connection.port, Connection.user, Connection.password, Connection.database)
    sql = "SELECT `Provider_First_Name`, \
`Provider_Middle_Name`, \
`Provider_Last_Name_(Legal_Name)` from ohsu_npi_labels where NPI=%s" % (provider_id)
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
        # Build the first last name thing
        #print row
        name+=row[0]+" "
        name+=row[1]+" "
        name+=row[2]
    db.close()
    return name

def getProviderPatiens(provider_id):
    patients=""
    # Ok let's get the results form this query here
    db = getDB(Connection.host, Connection.port, Connection.user, Connection.password, Connection.database)
    sql = "SELECT  COUNT(distinct ctsadata.icd_simple.patient_id) AS UNIQUE_PATIENTS  \
 FROM ctsadata.icd_simple WHERE ctsadata.icd_simple.npi =%s" %(provider_id)
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
        #print row
        # Build the first last name thing
        patients = row[0]
    db.close()
    return patients

def getProviderCredential(provider_id):
    credential=""
    # Ok let's get the results form this query here
    db = getDB(Connection.host, Connection.port, Connection.user, Connection.password, Connection.database)
    sql = "SELECT`Healthcare_Provider_Taxonomy_Code_1` , \
`Healthcare_Provider_Taxonomy_Code_2` , \
`Healthcare_Provider_Taxonomy_Code_3` , \
`Healthcare_Provider_Taxonomy_Code_4` , \
`Healthcare_Provider_Taxonomy_Code_5` , \
`Healthcare_Provider_Taxonomy_Code_6` , \
`Healthcare_Provider_Taxonomy_Code_7` , \
`Healthcare_Provider_Taxonomy_Code_8` , \
`Healthcare_Provider_Taxonomy_Code_9` , \
`Healthcare_Provider_Taxonomy_Code_10` , \
`Healthcare_Provider_Taxonomy_Code_11` , \
`Healthcare_Provider_Taxonomy_Code_12` ,  \
`Healthcare_Provider_Taxonomy_Code_13` , \
`Healthcare_Provider_Taxonomy_Code_14` , \
`Healthcare_Provider_Taxonomy_Code_15`  \
FROM ohsu_npi_labels where  NPI=%s" % (provider_id)
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
        # Build the first last name thing
        #print row
        credential+=row[0]+","
        credential+=row[1]+","
        credential+=row[2]+","
        credential+=row[3]+","
        credential+=row[4]+","
        credential+=row[5]+","
        credential+=row[6]+","
        credential+=row[7]+","
        credential+=row[8]+","
        credential+=row[9]+","
        credential+=row[10]+","
        credential+=row[11]+","
        credential+=row[12]+","
        credential+=row[13]+","
        credential+=row[14]

    db.close()
    return credential


def renderTagcloud(tagCloud, outputfile):
        # generate an html pages using a tagcloud instance and saves it to an output file

        # defintion of stiles sheets
        outputf = open(outputfile, 'w')
        header = "<style type=\"text/css\"><!-- #htmltagcloud{ \
        /****************************************** \
         * CUSTOMIZE CLOUD CSS BELOW (optional) \
         */ \
            font-size: 100%; \
            width: 80%;        /* auto or fixed width, e.g. 500px   */ \
            font-family:'lucida grande','trebuchet ms',arial,helvetica,sans-serif; \
            background-color:#fff; \
           /* margin:1em 1em 0 1em; */ \
            border:2px dotted #ddd; \
            padding:2em; \
        /****************************************** \
         * END CUSTOMIZE \
         */ \
        } \
        #htmltagcloud \
        {line-height:2.4em; \
         word-spacing:normal; \
         letter-spacing:normal; \
         text-transform:none; \
         text-align:justify; \
         text-indent:0} \
        #htmltagcloud a: \
        link{text-decoration:none} \
                                    \
        #htmltagcloud a            \
        :visited{text-decoration:none} \
        \
        #htmltagcloud \
        a:hover{color:white; \
                background-color:#05f} \
         \
        #htmltagcloud \
        a:active{color:white; \
                 background-color:#03d} \
        .wrd{padding:0;position:relative} \
        .wrd a{text-decoration:none} \
        .tagcloud0{font-size:1.0em; \
                   color:#ACC1F3;z-index:10} \
        .tagcloud0 a{color:#ACC1F3} \
        .tagcloud1{font-size:1.4em;color:#ACC1F3;z-index:9} \
                   .tagcloud1 a{color:#ACC1F3}.tagcloud2{font-size:1.8em;color:#86A0DC;z-index:8}.tagcloud2 a{color:#86A0DC}.tagcloud3{font-size:2.2em;color:#86A0DC;z-index:7}.tagcloud3 a{color:#86A0DC}.tagcloud4{font-size:2.6em;color:#607EC5;z-index:6}.tagcloud4 a{color:#607EC5}.tagcloud5{font-size:3.0em;color:#607EC5;z-index:5}.tagcloud5 a{color:#607EC5}.tagcloud6{font-size:3.3em;color:#4C6DB9;z-index:4}.tagcloud6 a{color:#4C6DB9}.tagcloud7{font-size:3.6em;color:#395CAE;z-index:3}.tagcloud7 a{color:#395CAE}.tagcloud8{font-size:3.9em;color:#264CA2;z-index:2}.tagcloud8 a{color:#264CA2}.tagcloud9{font-size:4.2em;color:#133B97;z-index:1}.tagcloud9 a{color:#133B97}.tagcloud10{font-size:4.5em;color:#002A8B;z-index:0}.tagcloud10 a{color:#002A8B}.freq{font-size:10pt !important;color:#bbb}#credit{text-align:center;color:#333;margin-bottom:0.6em;font:0.7em 'lucida grande',trebuchet,'trebuchet ms',verdana,arial,helvetica,sans-serif}#credit a:link{color:#777;text-decoration:none}#credit a:visited{color:#777;text-decoration:none}#credit a:hover{color:white;background-color:#05f}#credit a:active{text-decoration:underline}// -->\
\
</style> \n"
        outputf.write(header)
        #print "building tagCloud"

        # Here writing additional info for our use case
        provider_id= tagCloud.tagcloudName
        provider_name = getProviderName(provider_id)
        total_patients = getProviderPatiens(provider_id)
        #print str(total_patients)
       # print provider_name
        provider_credentials=getProviderCredential(provider_id)
       # print provider_credentials
        #Write the Provider name
        outputf.write("Name: "+provider_name+"</br>")
        outputf.write("Credentials: "+provider_credentials+"</br>")
        outputf.write("Patients: "+str(total_patients)+"</br>")
        # Write the sets of credentialling

        outputf.write("<div id=\"htmltagcloud\"> ")
        for tag in tagCloud.tagSet:
            rangeIndex = 0
            tag_index=0
            tag_name_link = tag.name.replace(' ', '+')
            for range in tagCloud.ranges:
              url = "http://www.google.com/search?q=" + tag.name.replace(' ', '+') + "+site%3Asujitpal.blogspot.com"
              if (tag.weight >= range[0] and tag.weight <= range[1]):
                outputf.write("<span id=\""+str(tag_index)+"\" class=\"wrd tagcloud"+str(rangeIndex)+"\"> \n <a href=\"http://www.ncbi.nlm.nih.gov/mesh?term='"+tag_name_link+"'\" target=\"_blank\">"+ tag.name + "</a></span>\n")
                break
              rangeIndex = rangeIndex + 1
            tag_index=tag_index + 1
        outputf.write("</div> ")
        outputf.close()


def buildTagClooudforProvider(provider_id):
    myTagCloud = None
    tagset=[]
    expertise_file_name = "./results/"+provider_id+"_tag_new.xls"
    expertise_file = open(expertise_file_name, 'rU')
    # Here read the file
    dialect = csv.Sniffer().sniff(expertise_file.readline())
    expertise_file.seek(0)
    reader = csv.DictReader(expertise_file, dialect=dialect)
    for line in reader:
        tagname = line['TERM']
        weight= line['CODE WEIGHT']
        new_tag = tag(tagname, weight)
        if len(tagset)<=10:
           tagset.append(tag(tagname, float(weight)))

   #print tagset
   #CreateTheTagcloud
    myTagCloud= tagCloud(tagset, str(provider_id))

    return myTagCloud


def main():

    # get the list of providers id
    providers = getProvidersfromCSV('./unique_patients.csv')
    for provider in providers:
        # Dubug that works
        print "Processing provider %s" %(provider)
        mytagCloud = buildTagClooudforProvider(provider)
        renderTagcloud(mytagCloud, "./tags/tags_"+provider+".html")


if __name__ == '__main__':
    main()
    #mytagCloud = buildTagClooudforProvider('1174733596')
    #renderTagcloud(mytagCloud, "tags_1174733596.html")


