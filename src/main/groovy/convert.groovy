import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

import javax.xml.bind.DatatypeConverter
import java.util.logging.Logger

def originDir = new File(".")

def xmlSystemCountry = 'AT'
def xmlDataSuppliereShort = "ESK"
def xmlDataSuppliereName = "ESPA"
def xmlDataSuppliereType = 'IC'
Calendar today = Calendar.getInstance();

Logger log = Logger.getLogger("")

def generator = { String alphabet, int n ->
    new Random().with {
        (1..n).collect { alphabet[nextInt(alphabet.length())] }.join()
    }
}

originDir.eachFile { file ->
    if (file.name.toLowerCase().endsWith(".csv")) {
        log.info("processing file: " + file.toPath())

        file.eachLine { line, count ->
            if (count == 1) // überprüfen auf Überschriftzeile
                return

            def split = line.split(';')
            println "groesse: " + split.size() // raus finden ob mit oder ohne wm additionals
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
                                        split[34] ? Years(split[34]) : Category(split[35])
                                    }
                                    MaturityDate(split[36])
                                    MayBeTerminatedEarly(split[37])
                                    SpecificInvestmentNeed(split[38])
                                }
                                DistributionStrategy {
                                    ExecutionOnly(split[39])
                                    ExecutionWithCheckOrNonAdvisedServices(split[40])
                                    InvestmentAdvice(split[41])
                                    PortfolioManagement(split[42])
                                }
                                CostsAndChargesExAnte {
                                    if (split[44] != null && split[44] != "") {
                                        // entweder fonds - oder structured security
                                        Fund {
                                            EntryCost(split[44])
                                            MaxEntryCostItaly(split[46])
                                            MaxEntryCostAcquired(split[47])
                                            MaxExitCost(split[48])
                                            MaxExitCostItaly(split[49])
                                            MaxExitCostAcquired(split[50])
                                            TypicalExitCost(split[51])
                                            OngoingCosts(split[54])
                                            ManagementFee(split[56])
                                            DistributionFee(split[58])
                                            TransactionCosts(split[59])
                                            IncidentalCosts(split[60])
                                        }
                                    } else {
                                        StructuredSecurity {
                                            Quotation(split[43])
                                            OneOfEntryCost(split[45])
                                            TypicalExitCost(split[52])
                                            ExitCostPriorRHP(split[53])
                                            OngoingCosts(split[55])
                                            ManagementFee(split[57])
                                        }
                                    }
                                }
                                CostsAndChargesExPost {
                                    if (split[61] == null || split[61] == "") {
                                        Fund {
                                            OngoingCosts(split[63])
                                            ManagementFee(split[66])
                                            DistributionFee(split[68])
                                            TransactionCosts(split[69])
                                            IncidentialCosts(split[70])
                                            CalculationPeriod {
                                                Start(split[71])
                                                End(split[72])
                                            }
                                        }
                                    }
                                    else {
                                        StructuredSecurity {
                                            EntryCost(split[61])
                                            ExitCost(split[62])
                                            OngoingCosts(split[64])
                                            OngoingCostsAccumulated(split[65])
                                            ManagementFee(split[67])
                                        }
                                    }
                                }
                                CountrySpecificData {
                                    DE {
                                        EMT_Additional {
                                            TargetMarketInformation {
                                                ModusApproval(split[73])
                                                SpecialRequirementsDescription(split[74])
                                                NatureProductCategory(split[75])
                                                ApprovalProcess(split[76])
                                            }
                                            CostTransparency {
                                                SwingPricing(split[77])
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
