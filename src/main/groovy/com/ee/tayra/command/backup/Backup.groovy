/*******************************************************************************
 * Copyright (c) 2013, Equal Experts Ltd
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation
 * are those of the authors and should not be interpreted as representing
 * official policies, either expressed or implied, of the Tayra Project.
 ******************************************************************************/
package com.ee.tayra.command.backup

import com.ee.tayra.*
import com.ee.tayra.connector.MongoAuthenticator;
import com.ee.tayra.connector.MongoReplSetConnection;
import com.ee.tayra.domain.*
import com.ee.tayra.io.*
import com.ee.tayra.io.criteria.CriteriaBuilder
import com.mongodb.Mongo
import com.mongodb.MongoException
import com.mongodb.ServerAddress

def cli = new CliBuilder(usage:'backup -s <MongoDB> [--port=number] -f <file> [--fSize=BackupFileSize] [--fMax=NumberOfRotatingLogs] [-t] [-u username] [-p password] [--sNs=<dbName>]')
cli.with {
	s  args:1, argName: 'MongoDB Host', longOpt:'source', 'OPTIONAL, Source MongoDB IP/Host, default is localhost', optionalArg:true
	_  args:1, argName: 'port', longOpt:'port', 'OPTIONAL, Source MongoDB Port, default is 27017', optionalArg:true
	f  args:1, argName: 'file', longOpt:'file', 'REQUIRED, File To Record Oplog To', required: true
	_  args:1, argName: 'fSize', longOpt:'fSize', 'OPTIONAL, Size of Backup File, Default is 512MB, Usage Eg: --fSize=4MB', optionalArg:true
	_ args:1, argName: 'fMax', longOpt:'fMax', 'OPTIONAL, Number of Backup Files to be generated, Default is 1, Usage Eg: --fMax=4', optionalArg:true
	t  args:0, argName: 'tailable', longOpt:'tailable', 'OPTIONAL, Default is Non-Tailable', optionalArg:true
	u  args:1, argName: 'username', longOpt:'username', 'OPTIONAL, username for authentication, default is none', optionalArg:true
	p  args:1, argName: 'password', longOpt:'password', 'OPTIONAL, password for authentication, default is none', optionalArg:true
	_ args:1, argName:'sNs',longOpt:'sNs', 'OPTIONAL, Namespace for selective backup, default is all namespaces, Eg: --sNs=test', optionalArg:true
}

def options = cli.parse(args)

if(!options) {
	return
}

config = new BackupCmdDefaults()

if(options.s) {
	config.mongo = options.s == true ? 'localhost' : options.s
}

recordToFile = options.f
timestampFileName = 'timestamp.out'
timestamp = null

logWriter = new RotatingFileWriter(recordToFile)

if(options.fSize) {
	logWriter.fileSize = options.fSize
}

if(options.fMax) {
	logWriter.fileMax = Integer.parseInt(options.fMax)
}

def getWriter() {
	binding.hasVariable('writer') ? binding.getVariable('writer')
			: logWriter
}

if(options.port) {
	config.port = Integer.parseInt(options.port)
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

if(options.u) {
	config.username = options.u
	config.password = options.p ?: readPassword(console)
}

writer = new TimestampRecorder(getWriter())

def listeningReporter = new ProgressReporter(console)

def listener = binding.hasVariable('listener') ? binding.getVariable('listener')
		: listeningReporter

def reporter = binding.hasVariable('reporter') ? binding.getVariable('reporter')
		: listeningReporter

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
def reader = null

def criteria = new CriteriaBuilder().build {
	if(options.sNs) {
		usingNamespace options.sNs
	}
}

boolean normalExecution = false

addShutdownHook {

	if(!normalExecution && reporter) {
		reporter.writeln (console,'==> User forced a Stop-Read...')
	}
	if(reader) {
		reader.close()
	}
	if (writer){
		writer.flush()
		if(writer.timestamp.length() > 0){
			new FileWriter(timestampFileName).append(writer.timestamp).close()
		}
	}
	if (reporter) {
		reporter.summarizeTo console
	}
}
errorLog = 'error.log'
def stderr = new PrintStream (new FileOutputStream(errorLog))
System.setErr(stderr)
try {
	reporter.writeStartTimeTo console
	new MongoReplSetConnection(config.mongo, config.port).using { mongo ->
		getAuthenticator(mongo).authenticate(config.username, config.password)
		def oplog = new Oplog(mongo)
		reader = new SelectiveOplogReader(new OplogReader(oplog, timestamp, isContinuous), criteria)
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
			if(writer.timestamp.length() > 0){
				new FileWriter(timestampFileName).append(writer.timestamp).close()
				timestamp = writer.timestamp
			}
		}
		console.println "Attempting to resume Backup On: ${new Date()}"
	}
} catch (Throwable problem) {
	console.println "Oops!! Could not perform backup...$problem.message"
}
normalExecution = true;

def getAuthenticator(mongo) {
	binding.hasVariable('authenticator') ?
			binding.getVariable('authenticator') : new MongoAuthenticator(mongo)
}
