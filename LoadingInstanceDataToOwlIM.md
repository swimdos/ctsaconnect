# Introduction #

This guide will help you take the output of the ctsaconnect code which generates fake instance information from aggregated clinical encounters data and place it in a OWL-IM repository on a sesame server.


# Step 1.  Get OWL-IM #

  1. Go here and fill out the form: http://www.ontotext.com/owlim/.  "OWLIM-Lite" is the free version but only provides In-Memory Data (ie Tomcat stops the memory goes away).
  1. They'll send you a download link.
  1. Untar and grab the war files from the sesame-owlim directory.
```
tar -cvfz the-owlim-file.tar.gz
cd the-owlim-file
cd sesame_owlim
```
  1. Place the war files for Sesame and Workbench in your Tomcat Webapps Directory.
```
cp openrdf-sesame.war /var/lib/tomcat6/webapps
cp openrdf-workbench.war /var/lib/tomcat6/webapps
```
  1. Go to localhost:8080/openrdf-workbench
    1. If you are using Ubuntu or Debian you may have an issue with your Sesame Server.  "Unable to create logging directory ..." Simply add the named directory "/usr/share/tomcat6/.aduna" to your system and give ownership to the tomcat6 user and group
```
sudo mkdir /usr/share/tomcat6/.aduna
chown -R tomcat6:tomcat6 .aduna
```
  1. Select "New Repository"
    1. enter a new repository name
    1. OWL-IM selected as the repository type "Click Next"
    1. select OWL2-RL as the Ruleset
  1. You can now add/clear/query from the workbench, or directly from the endpoint, which will be http://localhost:8080/openrdf-sesame/repositories/repositoryshortname

![http://ctsaconnect.googlecode.com/files/Add-Repository_OWLIM.png](http://ctsaconnect.googlecode.com/files/Add-Repository_OWLIM.png)

# Step 2. Generate the Data #

Created for your leisure in the ctsaconnect repository is a tool for generating instance information from your existing aggregated clinical encounters data.  Right now in the branch known as "dataSourceFromCSV" is the complete tool with some tweaking for large datasets and additional tools for CSV loads.

## From CSV ##
You will need to use GIT to download the code for this tool.
```
git clone https://user@code.google.com/p/ctsaconnect/ 
```
> Simply load your file onto the system where the ctsaconnect code is and run the class GenerateInstanceDataForCSV.  You'll want to pass as your only parameter the location of your CSV File.
```
cd eproject/CTSAConnect/src/net/ctsaconnect/datasource
java GenerateInstanceDataForCSV.java ~/fakedata.txt
```
That file should have the following structure to it columns
    * PRV\_PRIMARY\_PROVIDER\_ID - id of the doctor ( expected data type: string)
    * PROCEDURE\_CODE - for CPT Codes ( expected data type: string)
    * DIAGNOSIS\_CODE - for ICD9 Codes ( expected data type: string)
    * UNIQUE\_PATIENTS - number of unique patients ( expected data type: integer)
    * QUANTITY - number of times the code has been used ( expected data type: integer)

# Step 3. Load the Data into Workbench #

There are two ways to load the data into Sesame, either through the web interface or programmatically through the API. Depending on your hardware and the size of your data set; loading data may take a few minutes or more. You will see the 'page loading' indication for those few minutes while your data is loading.

## Through the web interface ##

The web interface is simple enough.  First you'll need to add the ontologies schema to OWL-IM.  Once we've gone through the process of adding the ontology schema you'll follow the same process for the instance information.
  1. Open OpenRDF Workbench and select the repository that you would like to store your data in
  1. In the menu on the left select ADD from the Modify Group
  1. To add the ontology you'll need 5 files
    1. clinical\_module.owl
    1. icd9\_classes.owl
    1. icd9\_subaxioms.owl
    1. cpt\_classes.owl (if you have the license)
    1. cpt\_subclasses.owl (if you have the classes)
  1. To add your data go to the OWLFiles\_generated folder in your CTSAConnect Project.  In there you'll find that every 50 records in your CSV has been broken out into its own owl file.  Upload each of these to your OWLIM system.
```
cd /ctsaconnect/eproject/CTSAConnect/OWLFiles_generated
```

![http://ctsaconnect.googlecode.com/files/AddRDF_OWLIM.png](http://ctsaconnect.googlecode.com/files/AddRDF_OWLIM.png)

## Through the API ##
TODO Document this process

# Step 4. Query the Data #
![http://ctsaconnect.googlecode.com/files/Query_OWLIM.png](http://ctsaconnect.googlecode.com/files/Query_OWLIM.png)