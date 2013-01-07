package com.ee.beaver.command

import com.mongodb.DB
import com.mongodb.Mongo
import com.mongodb.ServerAddress
import com.ee.beaver.domain.*
import com.ee.beaver.domain.operation.Operations
import com.ee.beaver.io.*

def cli = new CliBuilder(usage:'restore -d <MongoDB> [--port=number] -f <file> [-e exceptionFile] [-fAll]')
cli.with {
  d args:1, argName: 'MongoDB Host', longOpt:'dest', 'REQUIRED, Destination MongoDB IP/Host', required: true
  _ args:1, longOpt:'port', argName: 'port', 'OPTIONAL, Destination MongoDB Port, default is 27017', optionalArg:true
  f args:1, argName: 'file', longOpt:'file', 'REQUIRED, File To backup from', required: true
  fAll args:1, argName:'fAll', longOpt: 'fAll', 'OPTIONAL,  Restore from All Files, Default Mode : Restore from Single File', optionalArg:true
  e args:1, argName: 'exceptionFile', longOpt:'exceptionFile', 'OPTIONAL, File containing documents that failed to restore, default writes to file "exception.documents" in the run directory', required: false
  u  args:1, argName: 'username', longOpt:'username', 'OPTIONAL, username for authentication, default is none', optionalArg:true
  p  args:1, argName: 'password', longOpt:'password', 'OPTIONAL, password for authentication, default is none', optionalArg:true
  sDb args:1, argName:'DbFilter',longOpt:'sDb', 'OPTIONAL, Dbname for selective restore, default is none', optionalArg:true
}

def options = cli.parse(args)

if(!options) {
  return
}

destMongoDB = options.d
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
def filter = null
def toFilter = false
if(options.sDb) {
 toFilter = true
 filter = '-sDb=' + options.sDb
}

mongo = null
try {
  ServerAddress server = new ServerAddress(destMongoDB, port);
  mongo = new Mongo(server)
  getAuthenticator(mongo).authenticate(username, password)
  def files = new RotatingFileCollection(restoreFromFile, isMultiple)

  def writer = binding.hasVariable('writer') ? binding.getVariable('writer')
      : new OplogReplayer(new Operations(mongo))

  def listener = binding.hasVariable('listener') ? binding.getVariable('listener')
      : new ProgressReporter(new FileWriter(exceptionFile), console)

  def selectiveWriter = binding.hasVariable('selectiveWriter') ? binding.getVariable('selectiveWriter')
      : new SelectiveOplogReplayer(new Criteria(filter), writer)
  
  def copier = new Copier()

  def startTime = new Date().time
  console.println "Restore Started On: ${new Date(startTime)}"

  files.withFile {
    def reader = binding.hasVariable('reader') ? binding.getVariable('reader') : new FileReader(it)
    toFilter ? copier.copy(reader, selectiveWriter, listener) : copier.copy(reader, writer, listener) 
  }

  def endTime = new Date().time
  console.println "Completed in ${(endTime - startTime)/1000} secs"
  printSummaryTo console, listener
} catch (Throwable problem) {
  console.println "Oops!! Could not perform restore...$problem.message"
  problem.printStackTrace(console)
} finally {
  if(mongo) {
    mongo.close()
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
  console.println "Documents Restored: $listener.documentsWritten"
  console.println "Exception Documents: $listener.exceptionDocuments"
}
