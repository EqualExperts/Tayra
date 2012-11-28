package com.ee.beaver.command

import com.ee.beaver.*
import com.ee.beaver.io.*
import com.ee.beaver.domain.*
import com.mongodb.DB
import com.mongodb.Mongo
import com.mongodb.ServerAddress

def cli = new CliBuilder(usage:'backup -s <MongoDB> [-p port] -f <file> [-t]')
cli.with {
  s args:1, argName: 'MongoDB Host', longOpt:'source', 'REQUIRED, Source MongoDB IP/Host', required: true
  p args:1, argName: 'port', longOpt:'port', 'OPTIONAL, Source MongoDB Port, default is 27017', optionalArg:true
  f args:1, argName: 'file', longOpt:'file', 'REQUIRED, File To Record Oplog To', required: true
  t args:1, argName: 'tailable', longOpt:'tailable', 'OPTIONAL, Default is Non-Tailable', optionalArg:true
}

def options = cli.parse(args)

if(!options) {
	return
}

sourceMongoDB = options.s
recordToFile = options.f
timestampFile = 'timestamp.out'
fromTimestamp = null

def getWriter() {
  binding.hasVariable('writer') ? binding.getVariable('writer')
	: new FileWriter(recordToFile)
}

int port = 27017
if(options.p) {
	port = Integer.parseInt(options.p)
}

boolean isContinuous = false
if(options.t) {
	isContinuous = true
}

mongo = null
PrintWriter console = new PrintWriter(System.out, true)
writer = new TimestampWriter(getWriter())
listener = binding.hasVariable('listener') ? binding.getVariable('listener')
		: new ProgressReporter(null, console)
try {
  ServerAddress server = new ServerAddress(sourceMongoDB, port);
  mongo = new Mongo(server)
  DB local = mongo.getDB("local")
  oplog = new Oplog(local)
  reader = new OplogReader(oplog, fromTimestamp, isContinuous)

  console.println "Backup Started On: ${new Date()}"
  new Copier().copy(reader, writer, listener)

} catch (Throwable problem) {
	console.println "Oops!! Could not perform backup...$problem.message"
} finally {
	if (writer){
		new FileWriter(timestampFile).append(writer.timestamp).flush()
	}
	if(mongo) {
		mongo.close()
	}
	if (listener) {
		printSummaryTo console, listener
	}
}

def printSummaryTo(console, listener) {
	console.printf '%s\r', ''.padRight(79, ' ')
	console.println ''
	console.println '---------------------------------'
	console.println '             Summary             '
	console.println '---------------------------------'
	console.println "Total Documents Read: $listener.documentsRead"
	console.println "Documents Written: $listener.documentsWritten"
}
