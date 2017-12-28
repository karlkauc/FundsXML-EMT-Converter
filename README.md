# FundsXML-EMT-Converter

![Apache 2.0 License](https://img.shields.io/badge/LICENSE-Apache_2.0-yellow.svg)

## Purpose
The following tool convertes a EMT (CSV file) to an FundsXML4 file.
 
<b>All shareclasses (ISINs)of a fund has to be inside ONE csv file (grouping of XML data is done based on this information).</b>



## How to use


### precompiled binary files
You can download a precompiled jar file [here](https://github.com/karlkauc/FundsXML-EMT-Converter/blob/master/build/libs/FundsXML-EMT-Converter-0.1-all.jar?raw=true)  
There is also a Windows compiled .exe file [here](https://github.com/karlkauc/FundsXML-EMT-Converter/blob/master/build/launch4j/FundsXML-EMT-Converter-all.exe?raw=true)

### build from source
To build the converter from source you need:
1. JDK (min. version 6)
2. (optional)  [GIT](https://git-scm.com/downloads) 

Clone the project with GIT - or download a .zip version from this site.  
Open a command line client and type "./gradlew build" (or "gradlew.bat build" on Windows)

The compiled converter can be started with "java -jar build\libs\FundsXML-EMT-Converter-all-0.1.jar" on all platforms (or "build\launch4j\FundsXML-EMT-Converter-all.exe" on Windows)

## start converting
java -jar FundsXML-EMT-Converter-all-0.1.jar 
   
  
### input parameter
```
usage: java -jar FundsXML-EMT-Converter-0.1-all.jar
 -d,--dirfile <file or directory>   Verzeichnis oder File zum Bearbeiten (default: .)
 -h,--help                          Help
 -ih,--includeHeaderLines <arg>     csv file include X header lines (default: auto)
 -s,--seperator <arg>               CSV seperator (default: auto)
 -sc,--systemCountry <arg>          System Country (default: AT)
 -xn,--supplierName <arg>           Data Supplier Long Name (default: XXXX)
 -xs,--supplierShort <arg>          Data Supplier Short Name (default: XXX)
 -xt,--supplierType <arg>           Data Supplier Type (default: KAG)

```


### output
Valid FundsXML 4.1.1 files in current directory.
One file per Fund is created.  
Filename: ```<<uniqueDocumentId>>_<<xmlDataSuppliereShort>>.xml```


### changelg
2017-12-07: 
* changed to FundsXML 4.1.1 (2 nodes are now optional)
* new schema location
* header lines auto detect
* supplierType - default changed to "KAG"
* all shareclasses in one Fund node
