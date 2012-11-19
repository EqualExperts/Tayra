package com.ee.beaver.command

import com.mongodb.DB
import com.mongodb.Mongo
import com.mongodb.ServerAddress
import com.ee.beaver.domain.*
import com.ee.beaver.domain.operation.Operations
import com.ee.beaver.io.*

def cli = new CliBuilder(usage:'restore -d <MongoDB> [-p port] -f <file> [-e exceptionFile]')
cli.with {
  d args:1, argName: 'MongoDB Host', longOpt:'dest', 'REQUIRED, Destination MongoDB IP/Host', required: true
  p args:1, longOpt:'port', argName: 'port', 'OPTIONAL, Destination MongoDB Port, default is 27017', optionalArg:true
  f args:1, argName: 'file', longOpt:'file', 'REQUIRED, File To backup from', required: true
  e args:1, argName: 'exceptionFile', longOpt:'exceptionFile', 'OPTIONAL, File containing documents that failed to restore, default writes to file "exception.documents" in the run directory', required: false
}

def options = cli.parse(args)

if(!options) {
	return
}

destMongoDB = options.d
restoreFromFile = options.f

int port = 27017
if(options.p) {
  port = Integer.parseInt(options.p)
}

exceptionFile = 'exception.documents'
if(options.e) {
  exceptionFile = options.e
}

PrintWriter console = new PrintWriter(System.out, true)
mongo = null
try {
  ServerAddress server = new ServerAddress(destMongoDB, port);
  mongo = new Mongo(server)
  DB local = mongo.getDB("local")
  def reader = binding.hasVariable('reader') ? binding.getVariable('reader')
	: new BufferedReader(new FileReader(restoreFromFile))

  def writer = binding.hasVariable('writer') ? binding.getVariable('writer')
	  : new OplogReplayer(new Operations(mongo))

  def listener = binding.hasVariable('listener') ? binding.getVariable('listener')
	: new ProgressReporter(new FileWriter(exceptionFile), console)


  new Copier().copy(reader, writer, listener)
  
  printSummaryTo console, listener
  
} catch (Throwable problem) {
	console.println "Oops!! Could not perform restore...$problem.message"
	problem.printStackTrace(console)
} finally {
	if(mongo) {
		mongo.close()
	}
}

def printSummaryTo(console, listener) {
	console.printf '%s\r', ''.padRight(79, ' ')
	console.println ''
	console.println '---------------------------------'
	console.println '             Summary             '
	console.println '---------------------------------'
	console.println "Total Documents Read: $listener.documentsRead"
	console.println "Documents Restored: $listener.documentsWritten"
	console.println "Exception Documents: $listener.exceptionDocuments"
}
