package com.ee.beaver.runner

def cli = new CliBuilder(usage:'backup -s <MongoDB> [-p port] -f <file>')
cli.with {
  h longOpt:'help', 'Show usage information'
  s args:1, argName: 'MongoDB Host', longOpt:'source', 'REQUIRED, Source MongoDB IP/Host', required: true
  p args:1, longOpt:'port', argName: 'port', 'OPTIONAL, Source MongoDB Port, default is 27017', optionalArg:true
  f args:1, argName: 'file', longOpt:'recordTo', 'REQUIRED, File To Record Oplog To', required: true
}

def options = cli.parse(args)

if(!options) {
	return
}
 
if(options.h) {
	cli.usage()
	return
}

int port = 27017
if(options.p) {
	port = Integer.parseInt(options.p)
}

String sourceMongoDB = options.s
String recordToFile = options.f


println "source is $sourceMongoDB"
println "port is $port"
println "recordTo is $recordToFile"

def backupRunner = new BackupRunner(sourceMongoDB, port)
def writer = new FileWriter(recordToFile)
backupRunner.backup(writer)
backupRunner.close()
