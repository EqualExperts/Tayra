package com.ee.beaver.command

import com.ee.beaver.*
import com.ee.beaver.domain.*
import com.ee.beaver.io.*
import com.mongodb.Mongo
import com.mongodb.MongoException
import com.mongodb.ServerAddress

def cli = new CliBuilder(usage:'backup -s <MongoDB> [--port=number] -f <file> [--fSize=BackupFileSize] [--fMax=NumberOfRotatingLogs] [-t] [-u username] [-p password]')
cli.with {
	s  args:1, argName: 'MongoDB Host', longOpt:'source', 'REQUIRED, Source MongoDB IP/Host', required: true
	_  args:1, argName: 'port', longOpt:'port', 'OPTIONAL, Source MongoDB Port, default is 27017', optionalArg:true
	f  args:1, argName: 'file', longOpt:'file', 'REQUIRED, File To Record Oplog To', required: true
	fSize  args:1, argName: 'fileSize', longOpt:'fileSize', 'OPTIONAL, Default is 512MB', optionalArg:true
	fMax  args:1, argName: 'fileMax', longOpt:'fileMax', 'OPTIONAL, Default is 1', optionalArg:true
	t  args:1, argName: 'tailable', longOpt:'tailable', 'OPTIONAL, Default is Non-Tailable', optionalArg:true
	u  args:1, argName: 'username', longOpt:'username', 'OPTIONAL, username for authentication, default is none', optionalArg:true
	p  args:1, argName: 'password', longOpt:'password', 'OPTIONAL, password for authentication, default is none', optionalArg:true
}

def options = cli.parse(args)

if(!options) {
	return
}

sourceMongoDB = options.s
recordToFile = options.f
timestampFileName = 'timestamp.out'
timestamp = null

fSize = null
if(options.fSize) {
	fSize = options.fSize
}

fMax = 0
if(options.fMax) {
	fMax = Integer.parseInt(options.fMax)
}

def getWriter() {
	binding.hasVariable('writer') ? binding.getVariable('writer')
			: new RotatingFileWriter(recordToFile, fSize, fMax)
}

int port = 27017
if(options.port) {
	port = Integer.parseInt(options.port)
}

boolean isContinuous = false
if(options.t) {
	isContinuous = true
}

PrintWriter console = new PrintWriter(System.out, true)

def readPassword(output) {
	def input = System.console()
	if(!input) {
		output.println("Cannot Read Password Input, please use -p command line option")
		return ''
	}

	print "Enter password: "
	return new String(System.console().readPassword())
}


String username = ''
String password = ''
if(options.u) {
	username = options.u
	password = options.p ?: readPassword(console)
}

mongo = null
writer = new TimestampRecorder(getWriter())
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
	ServerAddress server = new ServerAddress(sourceMongoDB, port)
	mongo = new Mongo(server)
	getAuthenticator(mongo).authenticate(username, password)
	oplog = new Oplog(mongo)
	reader = new OplogReader(oplog, timestamp, isContinuous)
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

def getAuthenticator(mongo) {
	binding.hasVariable('authenticator') ?
	binding.getVariable('authenticator') : new MongoAuthenticator(mongo)
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
