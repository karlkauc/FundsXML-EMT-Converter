import static com.xlson.groovycsv.CsvParser.parseCsv

println "hallo welt 2222!!!"

def csv = '''Name,Lastname
Mark,Andersson
Pete,Hansen'''

def data = parseCsv(csv)
for (line in data) {
    println "$line.Name $line.Lastname"
}

def cli = new CliBuilder(usage: 'java -jar FundsXML-EMT-Converter-all-1.0.jar [options]')
cli.with {
    h longOpt: 'help',              'Show this screen'
    i longOpt: 'InputFileOrDir',    args: 1, argName: 'InputFileOrDir', 'CSV file or directory with CSV files'

    c longOpt: 'concurrency',       args: 1, argName: 'concurrency', 'number of processes sending messages'
    n longOpt: 'messages',          args: 1, argName: 'messages', 'number of messages to be send by each process'
    s longOpt: 'ip:port',           args: 2, argName: 'ip:port', 'server IP address:server port', valueSeparator: ':'
}

def options = cli.parse(args)
println options.t
println options.c
println options.n
println options.ss

// convert
// -i --InputFileOrDir - CSV file or directory of CSV files to convert (default current dir)
// -o --OutputDir - directory for converted FundsXML files (default current directory)
// -s --seperator CSV Delimiter. (default ',')
// -p --DataSupplier  - FundsXML Datasupplier (default 'XXX')
// -c --ContentDate ContentDate (default current date)
// -d --debug print debug information (default off)




