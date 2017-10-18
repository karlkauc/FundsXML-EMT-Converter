import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil
import groovyjarjarcommonscli.Option

import javax.xml.bind.DatatypeConverter
import java.util.logging.Logger

def cli = new CliBuilder(usage: 'java -jar FundsXML-EMT-Converter-all-0.1.jar')

cli.with {
    h(longOpt: 'help', 'Help', args: 0, required: false)
    d(longOpt: 'directories', 'Two arguments, separated by a comma', args: Option.UNLIMITED_VALUES, valueSeparator: ',', required: false)
    s(longOpt: 'strings', 'Strings (class names) to search for in JARs', args: Option.UNLIMITED_VALUES, valueSeparator: ',', required: false)
}

def opt = cli.parse(args)
if (opt.h)  {
    cli.usage()
}
if (opt.d) {
    println "directory: " + opt.d
}


def originDir = new File(".")

def xmlSystemCountry = 'AT'
def xmlDataSuppliereShort = "ESK"
def xmlDataSuppliereName = "ESPA"
def xmlDataSuppliereType = 'IC'
Calendar today = Calendar.getInstance();
Logger log = Logger.getLogger("")

def lineStart = 1
def splitter = ';'

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

originDir.eachFile { file ->
    if (file.name.toLowerCase().endsWith(".csv")) {
        log.info("processing file: " + file.toPath())
        splitter = getSplitter(file.text.toString())

        file.eachLine { line, count ->
            if (count == lineStart)
                return

            def split = line.split(splitter)


            def includingAdditional = false
            if (split.size() == 77) {
                includingAdditional = true
            }

            def uniqueDocumentId = xmlDataSuppliereShort + "-" + new Date().format('YYYY-MM-dd') + "-" + generator((('A'..'Z') + ('0'..'9')).join(), 9)
            def reportingDate = split[4]
            def outputFileName = split[0] + "_" + reportingDate + "_" + xmlDataSuppliereShort + ".xml"

            def writer = new StringWriter()
            def fXML = new MarkupBuilder(writer)

            fXML.FundsXML4(["xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance", "xsi:noNamespaceSchemaLocation": "C:\\Data\\src\\FundsXML\\4.1.0\\FundsXML_4.1.0.xsd"]) {
                // https://fdp-service.oekb.at/FundsXML_4.0.1_AI.xsd
                ControlData {
                    UniqueDocumentID(uniqueDocumentId)
                    DocumentGenerated(DatatypeConverter.printDateTime(today))
                    Version("4.1.0")
                    ContentDate(reportingDate)
                    DataSupplier {
                        SystemCountry(xmlSystemCountry)
                        Short(xmlDataSuppliereShort)
                        Name(xmlDataSuppliereName)
                        Type(xmlDataSuppliereType)
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
                                    GuarantorName(split[7])
                                    ProductCategoryNature(split[8])
                                    LeveragedOrContingentLiability(split[9])
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
                                    ExpertInvestorGermany(split[16])
                                }
                                AbilityToBearLosses {
                                    NoCapitalLoss(split[17])
                                    LimitedCapitalLoss(split[18])
                                    LimitedCapitalLossLevel(split[19])
                                    NoCapitalGuarantee(split[20])
                                    LossBeyondCapital(split[21])
                                }
                                RiskTolerance {
                                    PRIIPSMethodology(split[22])
                                    UCITSMethodology(split[23])
                                    InternalMethodology(split[24])
                                    MethodologySpain(split[25])
                                    NotForInvestorsWithLowestRiskToleranceGermany(split[26])
                                }
                                ClientObjectives {
                                    ReturnProfile {
                                        Preservation(split[27])
                                        Growth(split[28])
                                        Income(split[29])
                                        Hedging(split[30])
                                        OptionOrLeveraged(split[31])
                                        Other(split[32])
                                        PensionSchemeGermany(split[33])
                                    }
                                    TimeHorizon {
                                        def temp = split[34]

                                        if (temp.isDouble()) {
                                            Years(temp)
                                        } else {
                                            Category(temp)
                                        }
                                    }
                                    MaturityDate(split[35])
                                    MayBeTerminatedEarly(split[36])
                                    SpecificInvestmentNeed(split[37])
                                }
                                DistributionStrategy {
                                    ExecutionOnly(split[38])
                                    ExecutionWithCheckOrNonAdvisedServices(split[39])
                                    InvestmentAdvice(split[40])
                                    PortfolioManagement(split[41])
                                }
                                CostsAndChargesExAnte {
                                    if (split[43] != null && split[43] != "") {
                                        // entweder fonds - oder structured security
                                        Fund {
                                            EntryCost(split[43])
                                            MaxEntryCostItaly(split[45])
                                            MaxEntryCostAcquired(split[46])
                                            MaxExitCost(split[47])
                                            MaxExitCostItaly(split[48])
                                            MaxExitCostAcquired(split[49])
                                            TypicalExitCost(split[50])
                                            OngoingCosts(split[53])
                                            ManagementFee(split[55])
                                            DistributionFee(split[57])
                                            TransactionCosts(split[58])
                                            IncidentalCosts(split[59])
                                        }
                                    } else {
                                        StructuredSecurity {
                                            Quotation(split[42])
                                            OneOfEntryCost(split[44])
                                            TypicalExitCost(split[51])
                                            ExitCostPriorRHP(split[52])
                                            OngoingCosts(split[54])
                                            ManagementFee(split[56])
                                        }
                                    }
                                }
                                CostsAndChargesExPost {
                                    if (split[60] == null || split[60] == "") {
                                        Fund {
                                            OngoingCosts(split[62])
                                            ManagementFee(split[65])
                                            DistributionFee(split[67])
                                            TransactionCosts(split[68])
                                            IncidentialCosts(split[69])
                                            CalculationPeriod {
                                                Start(split[70])
                                                End(split[71])
                                            }
                                        }
                                    } else {
                                        StructuredSecurity {
                                            EntryCost(split[60])
                                            ExitCost(split[61])
                                            OngoingCosts(split[63])
                                            OngoingCostsAccumulated(split[64])
                                            ManagementFee(split[66])
                                        }
                                    }
                                }

                                if (includingAdditional) {
                                    CountrySpecificData {
                                        DE {
                                            EMT_Additional {
                                                TargetMarketInformation {
                                                    ModusApproval(split[72])
                                                    SpecialRequirementsDescription(split[73])
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
            println "LINE:  " + line
            println XmlUtil.serialize(writer.toString())

            if (new File(outputFileName).exists()) {
                new File(outputFileName).delete()
            }
            new File(outputFileName).write(XmlUtil.serialize(writer.toString()))

        }
    } else {
        log.info(file.toPath().toString() + " ignoring... not a CSV file.")
    }

}
