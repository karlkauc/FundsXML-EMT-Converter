import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil
import java.util.logging.Logger

def originDir = new File(".")
def xmlDataSuppliere = "ESK"
def xmlContentDate = new Date().format('YYYY-MM-dd')

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
            if (count == 1)
                return

            def split = line.split(';')
            println "groesse: " + split.size()

            def uniqueDocumentId = xmlDataSuppliere + "-" + new Date().format('YYYY-MM-dd') + "-" + generator((('A'..'Z') + ('0'..'9')).join(), 9)
            def isin = split.getAt(0)

            // General Financial Instrument information
            def Financial_Instrument_Identifying_Data = split[0]
            def Type_Of_Identification_Code_For_The_Financial_Instrument = split[1]
            def Financial_Instrument_Name = split.getAt(2)
            def Financial_Instrument_Currency = split?.getAt(3)
            def Reporting_Date = split?.getAt(4)
            def Financial_Instrument_Legal_Structure = split?.getAt(5)
            def Financial_Instrument_Issuer_Name = split?.getAt(6)
            def Financial_Instrument_Guarantor_Name = split.getAt(7)
            def Product_Category_or_Nature = split.getAt(8)
            def Leveraged_Financial_Instrument_or_Contingent_Liability_Instrument = split.getAt(9)

            // Target markets - Investor Type
            def Investor_Type_Retail
            def Investor_Type_Professional
            def Investor_Type_Eligible_Counterparty

            // Target markets - Knowledge and / Or Experience
            def Basic_Investor
            def Informed_Investor
            def Advanced_Investor
            def Expert_Investor_Germany

            // Target markets - Ability To Bear Losses
            def No_Capital_Loss
            def Limited_Capital_Loss
            def Limited_Capital_Loss_Level
            def No_Capital_Guarantee
            def Loss_Beyond_Capital

            // Target markets - Risk Tolerance
            def Risk_Tolerance_PRIIPS_Methodology
            def Risk_Tolerance_UCITS_Metholodology
            def Risk_Tolerance_Internal_Methodology_For_Non_PRIIPS_and_Non_UCITS
            def Risk_Tolerance_For_Non_PRIIPS_and_Non_UCITS_Spain
            def Not_For_Investors_With_The_Lowest_Risk_Tolerance_Germany

            // Target markets - Client Objectives & Needs
            def Return_Profile_Preservation
            def Return_Profile_Growth
            def Return_Profile_Income
            def Return_Profile_Hedging
            def Option_or_Leveraged_Return_Profile
            def Return_Profile_Other
            def Return_Profile_Pension_Scheme_Germany
            def Time_Horizon
            def Maturity_Date
            def May_Be_Terminated_Early
            def Specific_Investment_Need

            // Distribution strategy
            def Execution_Only
            def Execution_With_Appropriateness_Test_Or_Non_Advised_Services
            def Investment_Advice
            def Portfolio_Management

            // Costs & Charges ex ante
            def Structured_Securities_Quotation
            def One_off_cost_Financial_Instrument_entry_cost
            def One_off_cost_Financial_Instrument_maximum_entry_cost_fixed_amount_Italy
            def One_off_cost_Financial_Instrument_maximum_entry_cost_acquired
            def One_off_costs_Financial_Instrument_maximum_exit_cost
            def One_off_costs_Financial_Instrument_maximum_exit_cost_fixed_amount_Italy
            def One_off_costs_Financial_Instrument_maximum_exit_cost_acquired
            def One_off_costs_Financial_Instrument_Typical_exit_cost
            def One_off_cost_Financial_Instrument_exit_cost_structured_securities_prior_RHP
            def Financial_Instrument_Ongoing_costs
            def Financial_Instrument_Management_fee
            def Financial_Instrument_Distribution_fee
            def Financial_Instrument_Transaction_costs_ex_ante
            def Financial_Instrument_Incidental_costs_ex_ante

            // Costs & Charges ex post
            def One_off_cost_Structured_Securities_entry_cost_ex_post
            def One_off_costs_Structured_Securities_exit_cost_ex_post
            def Financial_Instrument_Ongoing_costs_ex_post
            def Structured_Securities_Ongoing_costs_ex_post_accumulated
            def Financial_Instrument_Management_fee_ex_post
            def Financial_Instrument_Distribution_fee_ex_post
            def Financial_Instrument_Transaction_costs_ex_post
            def Financial_Instrument_Incidental_costs_ex_post
            def Beginning_Of_Calculation_Period
            def End_Of_Calculation_Period

            // WM additionals


            def writer = new StringWriter()
            def fXML = new MarkupBuilder(writer)

            fXML.FundsXML4(["xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance", "xsi:noNamespaceSchemaLocation": "https://fdp-service.oekb.at/FundsXML_4.0.1_AI.xsd"]) {
                ControlData {
                    UniqueDocumentID(uniqueDocumentId)
                    DocumentGenerated(new Date().format('YYYY-MM-dd'))
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
                                        ISIN(split[0])
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
    } else {
        log.info(file.toPath().toString() + " ignoring... not a CSV file.")
    }

}



