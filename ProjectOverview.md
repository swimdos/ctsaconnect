

## Introduction ##

This Google Code project is for any code related to the [CTSAconnect project](http://ctsaconnect.org/).  The code in this project is not tool related, rather, it is code related to processing and generating the data needed to support the current use-cases for the CTSAconnect project.

The current code is in one Eclipse project that is checked-in to the default Git repository.  The next section describes how to start working with the Eclipse project.

## Setup of the Eclipse project ##

Install Eclipse with Git support (EGit). The [Eclipse IDE for Java Developers](http://www.eclipse.org/downloads/) includes EGit or EGit can be added from the "Install new software" in the help menu.

Start Eclipse and open a "Git Repositories" view by going to Window -> Show View -> Other... and selected the view in the Git folder. In the opened view, select the "Clone a Git ..." button and enter  "https://code.google.com/p/ctsaconnect/" without the quotes in the URI field and click next. The next window will show the current Git branches and you can select which ones to clone if needed, otherwise, keep the default selections. The next window will show where the repository will be cloned to on your local machine but this can be changed as needed. Keep the default choices and click Finish.

You should now see "ctsaconnect" in the the "Git Repositories" view. Right-click and select "Import Projects..." then Next and Finish. The project should be imported into the "Package Explorer" and ready to be used. There are few additional configuration steps described next.

You will notice that there are compile errors in two of the packages. This is because the "DBInfo" class is missing. This class contains few constants for database connection parameters and few methods for getting database connections. There is an "DBInfoExample" class that should be copied and named "DBInfo". The compile errors should disappear one this is done.  The DBInfo" class still needs to be edited to change the JDBC URLs, usernames, and passwords as needed. The customized "DBInfo" is ignored by Git and it will not be committed to the repository (it will remain private). The code currently uses a standard installation of the UMLS in MySQL and the parameters need to point to that database. The other databases (CTSADATA, RXNORM, SNOMED, etc.) are not needed for running the main  code.

The next configuration brings in the ISF SVN checkout into the Eclipse workspace. Notice the "isf\_svn" folder in the project. It is a link to the trunk checkout of the ISF. However, for this to work, it needs a variable setup in Eclipse that points to the SVN checkout. Go to Window -> Preferences -> General -> Workspace -> Linked Resources and click "New...".  Enter "ISF\_SVN" and then click the "Folder.." button and navigate to the ISF checkout folder that represents a checkout of the whole trunk. Click OK and OK. Now right click on the project and Refresh. You should now see the ISF checkout under the "isf\_svn". This could be useful if you want to work with some of the ISF files inside Eclipse.

The next configuration is related to the previous one. Plain Java code is not aware of the Eclipse-specific variable that was setup above. Java code needs a variable (not a linked resource) that points to the same location. There is at least one Java script that needs an "ISF\_SVN" variable to run correctly. There are various ways for doing this but the current code depends on a Java system property named "ISF\_SVN" that has the location of the ISF checkout.  Go to Window -> Preferences -> Java -> Installed JREs. There should be at least one entry in the list of installed JREs. Select the one that is checked and click Edit. In the "Default VM Arguments" field, enter "-DISF\_SVN=" without the quotes followed by the path to your SVN checkout. It should look something like "-DISF\_SVN=c:/svns/ctsaconnect". Now this "ISF\_SVN" variable value is available to the code that needs it.



## Code details ##

The imported project will show few Java packages. The "net.ctsaconnect.common" package contains a "Const" class that contains the various string literals that are used in the code (URL prefixes, few high-level class URLs, etc.). The "OWLUtil" and "Util" classes contain reusable utility methods.

The "net.ctsaconnect.classes" package contains scripts that generate the OWL CPT and ICD9 classes needed for the ontology. These scripts can be ran by right-clicking and selecting "run as" -> "java application".

The "net.ctsaconnnect.data" package contains a script that generates an OWL file for sample data included in the code as described below. This can be ran as described above.

The "net.ctsaconnect.datasource" package contains classes needed for using data from various sources. The "SimpleDataObject" class is a common view of a data element from a datasource.  The original data from various sources will likely differ in their raw format and will need to be transformed to a common data structure. The "SimpleDataObject" represents a draft version of the common data structure. The "DataSource" class represent an interface to a datasource that is able to return instances of "SimpleDataObject".  There is an example of an actual datasource named "DataSourceSimple" that shows how to implement the 3 methods needed to read data from a datasource. This example has hard-coded data but real implementations will likely read from a file or a database.

The "net.ctsaconnect.hold" package is a holding place for new or old code that is not usable but should still be made available for some time.

There are few text files in the project that are intended to keep track of changes, issues, setup instructions, etc. but they are not up-to-date at this time. This wiki page will likely be the more current description of the project.

The code at this point is only a draft version that is mainly used for testing. Changes will be made as we obtain real data and as the ontology evolves. The main next changes will be to refine the data structures based on the available data in the various institutions, and to write a repository/triplestore writer to write data directly to the repository instead of generating large data files.