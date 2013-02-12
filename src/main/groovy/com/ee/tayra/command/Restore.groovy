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
package com.ee.tayra.command

import com.mongodb.DB
import com.mongodb.Mongo
import com.mongodb.ServerAddress
import com.ee.tayra.domain.*
import com.ee.tayra.domain.operation.Operations
import com.ee.tayra.io.*
import com.ee.tayra.io.criteria.CriteriaBuilder;

def cli = new CliBuilder(usage:'restore -d <MongoDB> [--port=number] -f <file> [-e exceptionFile] [--fAll] [--sDb=<dbName>] [--sUntil=<timestamp>] [--dry-run]')

boolean destinationOptionRequired = args.contains('--dry-run') ? false : true

cli.with  {
	d args:1, argName: 'MongoDB Host', longOpt:'dest', 'REQUIRED, Destination MongoDB IP/Host', required: destinationOptionRequired
	_ args:1, argName: 'port', longOpt:'port', 'OPTIONAL, Destination MongoDB Port, default is 27017', optionalArg:true
	f args:1, argName: 'file', longOpt:'file', 'REQUIRED, File To backup from', required: true
	_ args:0, argName:'fAll', longOpt: 'fAll', 'OPTIONAL,  Restore from All Files, Default Mode : Restore from Single File', optionalArg:true
	e args:1, argName: 'exceptionFile', longOpt:'exceptionFile', 'OPTIONAL, File containing documents that failed to restore, default writes to file "exception.documents" in the run directory', required: false
	u  args:1, argName: 'username', longOpt:'username', 'OPTIONAL, username for authentication, default is none', optionalArg:true
	p  args:1, argName: 'password', longOpt:'password', 'OPTIONAL, password for authentication, default is none', optionalArg:true
	_ args:1, argName:'sDb',longOpt:'sDb', 'OPTIONAL, Dbname for selective restore, default is none, Eg: --sDb=test', optionalArg:true
	_ args:1, argName:'sUntil',longOpt:'sUntil', 'OPTIONAL, timestamp for selective restore, default is none, \n Eg: ISO Format --sUntil=yyyy-MM-ddTHH:mm:ssZ or\n JSON Format \n --sUntil={"ts":{"$ts":1358408097,"$inc":10}} on windows (remove spaces)\n --sUntil=\'{ts:{$ts:1358408097,$inc:10}}\' on linux (remove space, double quotes and enclose in single quotes)' , optionalArg:true
	_ args:0, argName:'dry-run', longOpt: 'dry-run', 'OPTIONAL, To preview selected documents', optionalArg:true
}

def options = cli.parse(args)

if(!options) {
	return
}

destMongoDB = options.d ? options.d : null
restoreFromFile = options.f

int port = 27017
if(options.port) {
	port = Integer.parseInt(options.port)
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

exceptionFile = 'exception.documents'
if(options.e) {
	exceptionFile = options.e
}

isMultiple = false
if(options.fAll) {
	isMultiple = true
}

def criteria = new CriteriaBuilder().build {
	if(options.sDb) {
		usingDatabase options.sDb
	}
	if(options.sUntil) {
		usingUntil options.sUntil
	}
}

//mongo = null
try {
	def writer = null
	def listener = null
	def reporter = null
	def listeningReporter = null

	def resource = new ResourceBuilder().build(options.'dry-run', binding, destMongoDB, port, username, password, criteria, exceptionFile, console)

	writer = resource.getWriter()
	listener = resource.getListener()
	reporter = resource.getReporter()


/*
	if(!options.'dry-run'){

		ServerAddress server = new ServerAddress(destMongoDB, port);
		mongo = new Mongo(server)
		getAuthenticator(mongo).authenticate(username, password)

		writer = binding.hasVariable('writer') ? binding.getVariable('writer')
				: new SelectiveOplogReplayer(criteria, new OplogReplayer(new Operations(mongo)))

		listeningReporter = new RestoreProgressReporter(new FileWriter(exceptionFile), console)

		listener = binding.hasVariable('listener') ? binding.getVariable('listener')
				: listeningReporter

		reporter = binding.hasVariable('reporter') ? binding.getVariable('reporter')
				: listeningReporter
	} else {
		writer = binding.hasVariable('writer') ? binding.getVariable('writer')
				: new SelectiveOplogReplayer(criteria, new ConsoleReplayer(console))

		listeningReporter = new EmptyProgressReporter()

		listener = binding.hasVariable('listener') ? binding.getVariable('listener')
				: listeningReporter

		reporter = binding.hasVariable('reporter') ? binding.getVariable('reporter')
				: listeningReporter
	}
*/
	def files = new RotatingFileCollection(restoreFromFile, isMultiple)
	def copier = new Copier()

	reporter.writeStartTimeTo console

	files.withFile {
		def reader = binding.hasVariable('reader') ? binding.getVariable('reader') : new FileReader(it)
		copier.copy(reader, writer, listener)
	}

	reporter.summarizeTo console
} catch (Throwable problem) {
	console.println "Oops!! Could not perform restore...$problem.message"
	problem.printStackTrace(console)
//} finally {
//	if(mongo) {
//		mongo.close()
//	}
}

//def getAuthenticator(mongo) {
//	binding.hasVariable('authenticator') ?
//			binding.getVariable('authenticator') : new MongoAuthenticator(mongo)
//}
