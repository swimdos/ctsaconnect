# Introduction #
Because the ISF is a modular ontology, you can load selected pieces as needed and leave out others. These instructions will focus on loading the ontology at VIVO startup.


# Details #
To begin with, you need a VIVO install and a copy of the ISF ontology. Instructions on setting up VIVO locally are beyong the scope of this document; instead, refer to http://vivoweb.org/support for information.

## Obtaining the ISF ##
You must check out the latest version of the ISF using Subversion. You need to have a google code account password to authenticate to the ISF repository, in addition to a regular Google account. You can obtain your Google Code password at https://code.google.com/hosting/settings. Subversion documentation and downloads are available at http://subversion.apache.org/.

After installing subversion locally, clone the ISF repo by running the following command:
```
svn checkout https://connect-isf.googlecode.com/svn/trunk/ connect-isf --username username@gmail.com
```

Enter your google code password when prompted. You will then have a connect-isf directory containing three subdirectories, `data`, doc, and src.



There are three locations you can load the ontology elements into:

/var/lib/tomcat6/webapps/vivo/WEB-INF/ontologies/user/tbox
is used for loading ontology defintions (terminology, or tbox elements).

/var/lib/tomcat6/webapps/vivo/WEB-INF/filegraph/abox
is used for loading instance data (assertions, or abox elements)

/var/lib/tomcat6/webapps/vivo/WEB-INF/filegraph/tbox
additional location used for loading tbox elements

For the clinical demonstrator work performed at UF, we copied the following elements:

expertise-module-instances.owl > /var/lib/tomcat6/webapps/vivo/WEB-INF/ontologies/user/tbox

icd9-procedure-vocabulary.owl > /var/lib/tomcat6/webapps/vivo/WEB-INF/filegraph/abox

clinical\_module.owl, arg.owl, bfo11.owl, bfo.owl, expertise-module-IAO-imports.owl, expertise-module-OBI-imports.owl, expertise-module.owl, external-byhand.owl, obi-imports.owl, ro\_bfo\_bridge11.owl > /var/lib/tomcat6/webapps/vivo/WEB-INF/filegraph/tbox

After a VIVO restart, browse to the Site Admin page and verify that the ontologies have been loaded. Additional ontology elements can be copied to these directories as needed and will be loaded on startup.