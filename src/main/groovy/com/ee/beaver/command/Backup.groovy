package com.ee.beaver.command

import com.ee.beaver.*
import com.ee.beaver.domain.*
import com.ee.beaver.io.*
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
timestampFileName = 'timestamp.out'
timestamp = null

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


timestampFile = new File(timestampFileName)
if(timestampFile.isDirectory()) {
	console.println("Expecting $timestampFile.name to be a File, but found Directory")
	System.exit(1)
}
if(timestampFile.exists()) {
	if(timestampFile.canRead() && timestampFile.length() > 0) {
		timestamp = timestampFile.text
	} else {
		console.println("Unable to read $timestampFile.name")
	}
}

try {
	ServerAddress server = new ServerAddress(sourceMongoDB, port);
	mongo = new Mongo(server)
	oplog = new Oplog(mongo)
	reader = new OplogReader(oplog, null, isContinuous)
	console.println "Backup Started On: ${new Date()}"
	new Copier().copy(reader, writer, listener)
} catch (Throwable problem) {
	console.println "Oops!! Could not perform backup...$problem.message"
} finally {
	if (writer){
		new FileWriter(timestampFileName).append(writer.timestamp).flush()
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
