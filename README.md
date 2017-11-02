# FundsXML-EMT-Converter

![Apache 2.0 License](https://img.shields.io/badge/LICENSE-Apache_2.0-yellow.svg)

## Purpose
The following tool convertes a EMT (CSV file) to an FundsXML4 file.

## How to use


### precompiled binary files
You can download a precompiled jar file [here](https://github.com/karlkauc/FundsXML-EMT-Converter/blob/master/build/libs/FundsXML-EMT-Converter-0.1-all.jar?raw=true)  
There is also a Windows compiled .exe file [here](https://github.com/karlkauc/FundsXML-EMT-Converter/blob/master/build/launch4j/FundsXML-EMT-Converter-all.exe?raw=true)

### build from source
To build the converter from source you need:
1. JDK (min. version 6)
2. (optional)  [GIT](https://git-scm.com/downloads) 

Clone the project with GIT - or download a .zip version from this site.  
Open a command line client and type "./gradle build" (or "gradlew.bat build" on Windows)

The compiled converter can be started with "java -jar build\libs\FundsXML-EMT-Converter-all-0.1.jar" on all platforms (or "build\launch4j\FundsXML-EMT-Converter-all.exe" on Windows)

## start converting
java -jar FundsXML-EMT-Converter-all-0.1.jar 
   
  
### input parameter
```
 usage: java -jar FundsXML-EMT-Converter-all-0.1.jar
  -d,--dirfile <file or directory>   Verzeichnis oder File zum Bearbeiten
  -h,--help                          Help
  -s,--seperator <arg>               CSV seperator
  -sc,--systemCountry <arg>          System Country
  -xn,--supplierName <arg>           Data Supplier Long Name
  -xs,--supplierShort <arg>          Data Supplier Short Name
  -xt,--supplierType <arg>           Data Supplier Type
```

### output
Valid FundsXML 4.1.0 files in current directory.
One file per ISIN is created.  
Filename: ```<ISIN>_<reportingDate>_<xmlDataSuppliereShort>.xml```

