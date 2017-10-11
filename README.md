# FundsXML-EMT-Converter

![Apache 2.0 License](https://img.shields.io/badge/LICENSE-Apache_2.0-yellow.svg)

## Purpose
The following tool convertes a EMT (CSV file) to an FundsXML4 file.

## How to use


### precompiled binary files
You can download a precompiled jar file [here](https://github.com/karlkauc/FundsXML-EMT-Converter/blob/master/documents/FundsXML-EMT-Converter-all-0.1.jar) !  

### build from source
To build the converter from source you need:
1. JDK (min. version 6)
2. Build tool [Gradle](https://gradle.org/)
3. (optional)  [GIT](https://git-scm.com/downloads) 

Install Gradle following the instructins on the gradle website.  
Clone the project with GIT - or download a .zip version from this site.  
Open a command line client and type "gradle fatJar"

The compiled converter can be started with "java -jar build\libs\FundsXML-EMT-Converter-all-0.1.jar" 

## start converting
java -jar FundsXML-EMT-Converter-all-0.1.jar 
   
  
### input
all csv files in current directory  

### output
Valid FundsXML 4.1.0 files in current directory

  


# License
Apache 2 license...
