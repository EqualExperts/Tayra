package com.ee.beaver.command

import com.mongodb.DB
import com.mongodb.Mongo
import com.mongodb.ServerAddress
import com.ee.beaver.domain.*
import com.ee.beaver.io.*

def cli = new CliBuilder(usage:'restore -d <MongoDB> [-p port] -f <file>')
cli.with {
  d args:1, argName: 'MongoDB Host', longOpt:'dest', 'REQUIRED, Destination MongoDB IP/Host', required: true
  p args:1, longOpt:'port', argName: 'port', 'OPTIONAL, Destination MongoDB Port, default is 27017', optionalArg:true
  f args:1, argName: 'file', longOpt:'file', 'REQUIRED, File To backup from', required: true
}

def options = cli.parse(args)

if(!options) {
	return
}

destMongoDB = options.d
restoreFromFile = options.f

def getReader() {
  binding.hasVariable('reader') ? binding.getVariable('reader')
	: new FileReader(restoreFromFile)
}
 
int port = 27017
if(options.p) {
	port = Integer.parseInt(options.p)
}


mongo = null
try {
  ServerAddress server = new ServerAddress(destMongoDB, port);
  mongo = new Mongo(server);
  DB local = mongo.getDB("local");
  oplog = new Oplog(local)
  writer = new OplogWriter(oplog)
  def reader = getReader()
  new Copier().copy(writer, reader)
} catch (Throwable problem) {
	PrintWriter writer = new PrintWriter(System.out, true)
	writer.println "Oops!! Could not perform restore...$problem.message"
} finally {
	if(mongo) {
		mongo.close()
	}
}
