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

import com.ee.tayra.connector.MongoAuthenticator
import com.ee.tayra.connector.MongoReplSetConnection
import com.ee.tayra.domain.*
import com.ee.tayra.io.*

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
	_ args:0, argName:'sExclude',longOpt:'sExclude', 'OPTIONAL, Excludes the following criteria, default is include all given criteria', optionalArg:true
}

def options = cli.parse(args)

if(!options) {
	return
}
PrintWriter console = new PrintWriter(System.out,true)

if(options.arguments()){
	console.println "Cannot Understand ${options.arguments()}"
	cli.usage()
	return
}

config = new BackupCmdDefaults()

if(options.s) {
	config.source = options.s == true ? 'localhost' : options.s
}

config.recordToFile = options.f

def isString(value) {
	if(value.getClass() == String) {
		if (value.toString().length() > 0) {
			return true
		}
	}
}

if (isString(options.fSize)) {
	config.fileSize = options.fSize
}

if (isString(options.fMax)) {
	config.fileMax = Integer.parseInt(options.fMax)
}

if(options.port) {
	config.port = Integer.parseInt(options.port)
}

if(options.t) {
	config.isContinuous = true
}

if(options.sExclude) {
	config.sExclude = true
}

if(options.sNs){
	config.sNs = options.sNs
}

if(options.u) {
	config.username = options.u
	config.password = options.p ?: readPassword(console)
}

private def readPassword(output) {
	def input = System.console()
	if(!input) {
		output.println("Cannot Read Password Input, please use -p command line option")
		return ''
	}
	print "Enter password: "
	return new String(System.console().readPassword())
}

def factory = new BackupFactory(config, console)

def progressListener = binding.hasVariable('listener') ? binding.getVariable('listener')
		: factory.createListener()

def progressReporter = binding.hasVariable('reporter') ? binding.getVariable('reporter')
		: factory.createReporter()

def writer =  binding.hasVariable('writer') ? binding.getVariable('writer')
		: factory.createDocumentWriter()

def timestamp = factory.timestamp

def reader = null
boolean normalExecution = false
addShutdownHook {
	if(!normalExecution) {
		progressReporter?.writeln (console,'==> User forced a Stop-Read...')
	}
	try {
		reader?.close()
	} catch (RuntimeException e) {
	}
	if(writer.class == TimestampRecorder) {
		if(writer && writer.timestamp.length() > 0) {
			factory.createTimestampFile().withWriter { it.write writer.timestamp }
		}
	}
	progressReporter?.summarizeTo console
}

errorLog = 'error.log'
def stderr = new PrintStream (new FileOutputStream(errorLog))
System.setErr(stderr)

try {
	progressReporter.writeStartTimeTo console
	new MongoReplSetConnection(config.source, config.port).using { mongo ->
		getAuthenticator(mongo).authenticate(config.username, config.password)
		def oplog = new Oplog(mongo)
		reader = factory.createReader(oplog)
//		def exceptionBubbler = factory.createMongoExceptionBubbler()
		new Copier().copy(reader, writer)
	} {
		if(writer && writer.timestamp.length() > 0){
			factory.createTimestampFile().append(writer.timestamp)?.close()
			factory.timestamp = writer.timestamp
		}
		console.println "Attempting to resume Backup On: ${new Date()}"
	}
} catch (Throwable problem) {
	console.println "Oops!! Could not perform backup...$problem.message"
} finally {
	reader?.close()
	if(writer.class == TimestampRecorder) {
		if(writer && writer.timestamp.length() > 0) {
			factory.createTimestampFile().withWriter { it.write writer.timestamp }
		}
	}
}

normalExecution = true

def getAuthenticator(mongo) {
	binding.hasVariable('authenticator') ?
			binding.getVariable('authenticator') : new MongoAuthenticator(mongo)
}
