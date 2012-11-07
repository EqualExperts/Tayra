package com.ee.beaver.command

import com.ee.beaver.*
import com.ee.beaver.io.*
import com.ee.beaver.domain.*
import com.mongodb.DB
import com.mongodb.Mongo
import com.mongodb.ServerAddress

def cli = new CliBuilder(usage:'backup -s <MongoDB> [-p port] -f <file>')
cli.with {
  s args:1, argName: 'MongoDB Host', longOpt:'source', 'REQUIRED, Source MongoDB IP/Host', required: true
  p args:1, longOpt:'port', argName: 'port', 'OPTIONAL, Source MongoDB Port, default is 27017', optionalArg:true
  f args:1, argName: 'file', longOpt:'recordTo', 'REQUIRED, File To Record Oplog To', required: true
}

def options = cli.parse(args)

if(!options) {
	return
}

sourceMongoDB = options.s
recordToFile = options.f

def getWriter() {
  binding.hasVariable('writer') ? binding.getVariable('writer')
	: new FileWriter(recordToFile)
}
 
int port = 27017
if(options.p) {
	port = Integer.parseInt(options.p)
}


mongo = null
try {
  ServerAddress server = new ServerAddress(sourceMongoDB, port);
  mongo = new Mongo(server);
  DB local = mongo.getDB("local");
  oplog = new Oplog(local)
  reader = new OplogReader(oplog)
  def writer = getWriter() 
  new Copier().copy(reader, writer)
} catch (Throwable problem) {
	PrintWriter writer = new PrintWriter(System.out, true)
	writer.println "Oops!! Could not perform backup...$problem.message"
} finally {
	if(mongo) {
		mongo.close()
	}
}
