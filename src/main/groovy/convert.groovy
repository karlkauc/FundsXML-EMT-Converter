import com.xlson.groovycsv.CsvParser
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil
import java.util.logging.Logger

def originDir = new File("C:\\Data\\src\\FundsXML\\documents\\2017-09-19_EMT_Converter\\emt")
def xmlDataSuppliere = "ESK"
def xmlContentDate = new Date().format('YYYY-MM-dd')

Logger log = Logger.getLogger("")


originDir.eachFile { file ->
    if (file.name.toLowerCase().endsWith(".csv")) {
        log.info("processing file: " + file.toPath())
        println xmlContentDate

        def csv = new CsvParser().parse(file.text, separator: ';', quoteChar: "'")

        csv.each { line ->
            def writer = new StringWriter()
            def fXML = new MarkupBuilder(writer)

            fXML.FundsXML4(["xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance", "xsi:noNamespaceSchemaLocation": "https://fdp-service.oekb.at/FundsXML_4.0.1_AI.xsd"]) {
                ControlData {
                    UniqueDocumentID(new Date())
                    DocumentGenerated(new Date())
                    Version("4.1.0")
                    ContentDate(xmlContentDate)
                    DataSupplier {
                        SystemCountry('AT')
                        Short(xmlDataSuppliere)
                        Name(xmlDataSuppliere)
                        Type('company')
                    }
                }
                RegulatoryReportings {
                    DirectReporting {
                        EMT {
                            FinancialInstrument {
                                FundOrShareClassIdentifiers {
                                    ISINs {
                                        ISIN(line.ISIN)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            println "LINE:  " + line
            println XmlUtil.serialize(writer.toString())
        }
    }
    else {
        log.info(file.toPath().toString() + " ignoring... not a CSV file.")
    }

}
