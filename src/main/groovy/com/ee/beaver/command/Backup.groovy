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
	_  args:1, argName: 'fSize', longOpt:'fSize', 'OPTIONAL, Size of Backup File, Default is 512MB, Usage Eg: --fSize=4MB', optionalArg:true
	_ args:1, argName: 'fMax', longOpt:'fMax', 'OPTIONAL, Number of Backup Files to be generated, Default is 1, Usage Eg: --fMax=4', optionalArg:true
	t  args:0, argName: 'tailable', longOpt:'tailable', 'OPTIONAL, Default is Non-Tailable', optionalArg:true
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

logWriter = new RotatingFileWriter(recordToFile)

if(options.fSize) {
	logWriter.setFileSize(options.fSize)
}

if(options.fMax) {
	logWriter.setFileMax(Integer.parseInt(options.fMax))
}

def getWriter() {
	binding.hasVariable('writer') ? binding.getVariable('writer')
			: logWriter
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

addShutdownHook {
	if (writer){
		new FileWriter(timestampFileName).append(writer.timestamp).flush()
	}
	if (listener) {
		printSummaryTo console, listener
	}
}

try {
	console.println "Backup Started On: ${new Date()}"
	new MongoReplSetConnection(sourceMongoDB, port).using { mongo ->
		getAuthenticator(mongo).authenticate(username, password)
		def oplog = new Oplog(mongo)
		def reader = new OplogReader(oplog, timestamp, isContinuous)
		new Copier().copy(reader, writer, listener, new CopyListener() {
					void onReadSuccess(String document){
					}
					void onWriteSuccess(String document){
					}
					void onWriteFailure(String document, Throwable problem){
					}
					void onReadFailure(String document, Throwable problem){
						if(problem instanceof MongoException)
							throw problem
					}
				})
	} {
		if (writer){
			new FileWriter(timestampFileName).append(writer.timestamp).flush()
			timestamp=writer.timestamp
		}
		console.println "Attempting to resume Backup On: ${new Date()}"
	}
} catch (Throwable problem) {
	console.println "Oops!! Could not perform backup...$problem.message"
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
