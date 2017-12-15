import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.xml.bind.DatatypeConverter

final Logger log = LogManager.getLogger(getClass().getName())

def cli = new CliBuilder(usage: 'java -jar FundsXML-EMT-Converter-all-0.1.jar')
cli.with {
    h longOpt: 'help', 'Help', args: 0, required: false
    d longOpt: 'dirfile', argName: 'file or directory', 'Verzeichnis oder File zum Bearbeiten', args: 1, required: false
    s longOpt: 'seperator', 'CSV seperator', args: 1, required: false
    xs longOpt: 'supplierShort', 'Data Supplier Short Name', args: 1, required: false
    xn longOpt: 'supplierName', 'Data Supplier Long Name', args: 1, required: false
    xt longOpt: 'supplierType', 'Data Supplier Type', args: 1, required: false
    sc longOpt: 'systemCountry', 'System Country', args: 1, required: false
    ih longOpt: 'includeHeaderLines', 'csv file include X header lines', args: 1, required: false
}

def opt = cli.parse(args)
if (opt.h) {
    cli.usage()
    System.exit(0)
}

log.debug "Starting..."

File originDir
if (opt.d && opt.d != 'auto') {
    originDir = new File(opt.d)
    log.info "setting auto file/directory to: " + originDir.name
} else {
    originDir = new File(".")
    log.info "setting user definied file/directory to: " + originDir.name
}

def splitter = opt.s

def xmlSystemCountry
if (!opt.sc) {
    xmlSystemCountry = 'AT'
} else {
    xmlSystemCountry = opt.sc
}
log.info "setting xmlSystemCountry to: " + xmlSystemCountry

def xmlDataSuppliereShort
if (!opt.xs) {
    xmlDataSuppliereShort = 'XXX'
} else {
    xmlDataSuppliereShort = opt.xs
    xmlDataSuppliereShort = removeQuotes(xmlDataSuppliereShort)

}
log.info "setting xmlDataSuppliereShort to: " + xmlDataSuppliereShort


def xmlDataSuppliereName
if (!opt.xn) {
    xmlDataSuppliereName = 'XXXX'
} else {
    xmlDataSuppliereName = opt.xn
    xmlDataSuppliereName = removeQuotes(xmlDataSuppliereName)
}
log.info "setting xmlDataSuppliereName to: " + xmlDataSuppliereName


def xmlDataSuppliereType
if (!opt.xt) {
    xmlDataSuppliereType = 'KAG'
} else {
    xmlDataSuppliereType = opt.xt
    xmlDataSuppliereType = removeQuotes(xmlDataSuppliereType)
}
log.info "setting xmlDataSuppliereType to: " + xmlDataSuppliereType

def skipHeaderLines
if (opt.ih && opt.ih != 'auto') {
    skipHeaderLines = 1
} else {
    skipHeaderLines = 0
}
// log.info "skipping [" + skipHeaderLines + "] header lines."

Calendar today = Calendar.getInstance()
def lineStart = 1

// generate random String for uniqueness
def generator = { String alphabet, int n ->
    new Random().with {
        (1..n).collect { alphabet[nextInt(alphabet.length())] }.join()
    }
}

// detecting split character
def static getSplitter(String s) {
    def map = [:]
    def possibleSplitter = [';', ',', '|']

    s.toCharArray().each { c ->
        if (map.containsKey(c)) {
            map.put(c, map[c] + 1)
        } else {
            map.put(c, 1)
        }
    }

    return map.findAll { k, v -> possibleSplitter.contains(k.toString()) }.sort { e1, e2 ->
        e2.value <=> e1.value
    }.keySet().toArray()[0].toString()
}

def timeToProd =  Date.parse("yyyy-MM-dd","2017-12-21")
def schemaLocation = "https://fdp-service.oekb.at/FundsXML_4.1.1_AI.xsd"
if (new Date() <= timeToProd ) {
    schemaLocation = "https://fdp-qas-service.oekb.at/FundsXML_4.1.1_AI.xsd"
    log.info "writing QAS schema location"
}

// Liste mit Files zum Bearbeiten
List<File> originFileList = []
if (originDir.isDirectory()) {
    originDir.eachFile { file -> if (file.name.toLowerCase().endsWith('.csv')) originFileList.add(file) }
} else {
    if (originDir.isFile()) {
        originFileList.add(originDir)
    } else {
        log.info("NOT A FILE: [" + originDir + "]")
    }
}
log.info("Processing file list: " + originFileList.join(';'))
if (originFileList.size() == 0) {
    log.error("KEINE FILES ZUM BEARBEITEN GEFUNDEN!")
    log.error("EVTL. PROGRAMM MIT OPTION -d <<Verzeichnis oder File>> STARTEN.")
}

originFileList.each { file ->
    log.info("start converting file: " + file.toPath())
    if (splitter == null || splitter == "" || splitter == 'auto' || !splitter.asBoolean()) {
        splitter = getSplitter(file.text.toString())
        log.info "auto detect splitter: " + splitter
        if (splitter == "|")
            splitter = '\\|'
    } else {
        log.info "using user defined splitter: " + splitter
    }

    file.eachLine { line, count ->
        def split = line.split(splitter.toString())

        // check if header exists
        if ((count == 1 && split[0].size() != 12) || count <= skipHeaderLines)   {
            log.info "skipping automatic header line"
            return
        }
        else {
            log.info "Processing ISIN [" + split[0] + "]"
        }

        def includingAdditional = false
        if (split.size() == 77) {
            includingAdditional = true
            log.info "csv includes EMT Additional data"
        } else {
            log.info "csv does not include EMT additional data"
        }

        def uniqueDocumentId = xmlDataSuppliereShort + "-" + new Date().format('YYYY-MM-dd') + "-" + generator((('A'..'Z') + ('0'..'9')).join(), 9)
        def reportingDate = split[4]
        def outputFileName = split[0] + "_" + reportingDate + "_" + xmlDataSuppliereShort + ".xml"

        log.debug("uniqueDocumentId: " + uniqueDocumentId)
        log.debug("reportingDate: " + reportingDate)
        log.debug("outputFileName: " + outputFileName)

        def writer = new StringWriter()
        def fXML = new MarkupBuilder(writer)

        fXML.FundsXML4(["xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance", "xsi:noNamespaceSchemaLocation": schemaLocation]) {
            ControlData {
                UniqueDocumentID(uniqueDocumentId)
                DocumentGenerated(DatatypeConverter.printDateTime(today))
                Version("4.1.1")
                ContentDate(reportingDate)
                DataSupplier {
                    SystemCountry(xmlSystemCountry)
                    Short(xmlDataSuppliereShort)
                    Name(xmlDataSuppliereName)
                    Type(xmlDataSuppliereType)
                }
                CountrySpecificData {
                    AT {
                        FundDataPortalContent('REG')
                    }
                }
            }
            RegulatoryReportings {
                DirectReporting {
                    EMT {
                        FinancialInstrument {
                            FundOrShareClassIdentifiers {
                                ISINs {
                                    ISIN(split[0])
                                }
                            }
                            DataSupplier {
                                SystemCountry(xmlSystemCountry)
                                Short(xmlDataSuppliereShort)
                                Name(xmlDataSuppliereName)
                                Type(xmlDataSuppliereType)
                            }
                            GeneralInformation {
                                Code(split[0])
                                CodificationSystem(split[1])
                                InstrumentName(split[2])
                                InstrumentCurrency(split[3])
                                ReportingDate(split[4])
                                LegalStructure(split[5])
                                IssuerName(split[6])
                                !split[7] ?: GuarantorName(split[7])
                                !split[8] ?: ProductCategoryNature(split[8])
                                !split[9] ?: LeveragedOrContingentLiability(split[9])
                            }
                            InvestorType {
                                Retail(split[10])
                                Professional(split[11])
                                EligibleCounterparty(split[12])
                            }
                            KnowledgeAndExperience {
                                BasicInvestor(split[13])
                                InformedInvestor(split[14])
                                AdvancedInvestor(split[15])
                                !split[16] ?: ExpertInvestorGermany(split[16])
                            }
                            AbilityToBearLosses {
                                NoCapitalLoss(split[17])
                                !split[18] ?: LimitedCapitalLoss(split[18])
                                !split[19] ?: LimitedCapitalLossLevel(split[19])
                                NoCapitalGuarantee(split[20])
                                LossBeyondCapital(split[21])
                            }
                            RiskTolerance {
                                !split[22] ?: PRIIPSMethodology(split[22])
                                !split[23] ?: UCITSMethodology(split[23])
                                !split[24] ?: InternalMethodology(split[24])
                                !split[25] ?: MethodologySpain(split[25])
                                !split[26] ?: NotForInvestorsWithLowestRiskToleranceGermany(split[26])
                            }
                            ClientObjectives {
                                ReturnProfile {
                                    Preservation(split[27])
                                    Growth(split[28])
                                    Income(split[29])
                                    Hedging(split[30])
                                    OptionOrLeveraged(split[31])
                                    Other(split[32])
                                    !split[33] ?: PensionSchemeGermany(split[33])
                                }
                                TimeHorizon {
                                    def temp = split[34]

                                    if (temp.isDouble()) {
                                        Years(temp)
                                    } else {
                                        Category(temp)
                                    }
                                }
                                !split[35] ?: MaturityDate(split[35])
                                !split[36] ?: MayBeTerminatedEarly(split[36])
                                !split[37] ?: SpecificInvestmentNeed(split[37])
                            }
                            DistributionStrategy {
                                !split[38] ?: ExecutionOnly(split[38])
                                !split[39] ?: ExecutionWithCheckOrNonAdvisedServices(split[39])
                                !split[40] ?: InvestmentAdvice(split[40])
                                !split[41] ?: PortfolioManagement(split[41])
                            }
                            CostsAndChargesExAnte {
                                if (split[43] != null && split[43] != "") {
                                    // entweder fonds - oder structured security
                                    Fund {
                                        EntryCost(split[43].trim())
                                        !split[45] ?: MaxEntryCostItaly(split[45].trim())
                                        !split[46] ?: MaxEntryCostAcquired(split[46].trim())
                                        MaxExitCost(split[47].trim())
                                        !split[48] ?: MaxExitCostItaly(split[48].trim())
                                        !split[49] ?: MaxExitCostAcquired(split[49].trim())
                                        !split[50] ?: TypicalExitCost(split[50].trim())
                                        OngoingCosts(split[53].trim())
                                        ManagementFee(split[55].trim())
                                        !split[57] ?: DistributionFee(split[57].trim())
                                        TransactionCosts(split[58].trim())
                                        split[59].trim() ? IncidentalCosts(split[59].trim()) : IncidentalCosts(0)
                                    }
                                } else {
                                    StructuredSecurity {
                                        Quotation(split[42].trim())
                                        OneOfEntryCost(split[44].trim())
                                        !split[51] ?: TypicalExitCost(split[51].trim())
                                        !split[52] ?: ExitCostPriorRHP(split[52].trim())
                                        OngoingCosts(split[54].trim())
                                        !split[56] ?: ManagementFee(split[56].trim())
                                    }
                                }
                            }
                            CostsAndChargesExPost {
                                if (split[60] == null || split[60] == "") {
                                    Fund {
                                        OngoingCosts(split[62].trim())
                                        ManagementFee(split[65].trim())
                                        !split[67] ?: DistributionFee(split[67].trim())
                                        TransactionCosts(split[68].trim())
                                        split[69].trim() ? IncidentialCosts(split[69].trim()) : IncidentialCosts(0)
                                        if (split[70] != null && split[71] != null) {
                                            CalculationPeriod {
                                                !split[70] ?: Start(split[70].trim())
                                                !split[71] ?: End(split[71].trim())
                                            }
                                        }
                                    }
                                } else {
                                    StructuredSecurity {
                                        !split[60] ?: EntryCost(split[60])
                                        !split[61] ?: ExitCost(split[61])
                                        OngoingCosts(split[63])
                                        !split[64] ?: OngoingCostsAccumulated(split[64])
                                        !split[66] ?: ManagementFee(split[66])
                                    }
                                }
                            }

                            if (includingAdditional) {
                                CountrySpecificData {
                                    DE {
                                        EMT_Additional {
                                            TargetMarketInformation {
                                                ModusApproval(split[72])
                                                !split[73] ?: SpecialRequirementsDescription(split[73])
                                                NatureProductCategory(split[74])
                                                ApprovalProcess(split[75])
                                            }
                                            CostTransparency {
                                                SwingPricing(split[76])
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        log.debug XmlUtil.serialize(writer.toString())

        new File(outputFileName).exists() ?: new File(outputFileName).delete()
        new File(outputFileName).write(XmlUtil.serialize(writer.toString()))
        log.info "written: " + new File(outputFileName).size() + " bytes"
    }
}

static String removeQuotes(String s) {
    s.replace("'", "").replace("\"", "")
}
