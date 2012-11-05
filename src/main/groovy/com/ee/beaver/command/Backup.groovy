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
 
int port = 27017
if(options.p) {
	port = Integer.parseInt(options.p)
}

String sourceMongoDB = options.s
String recordToFile = options.f

Mongo mongo = null
try {
  ServerAddress server = new ServerAddress(sourceMongoDB, port);
  mongo = new Mongo(server);
  DB local = mongo.getDB("local");

  Copier copier = new Copier(local)
  FileWriter writer = new FileWriter(recordToFile)
  MongoCollection oplog = new Oplog(local)
  OplogReader reader = new OplogReader(oplog)
  copier.copy(reader, writer)
} catch (Throwable problem) {
	PrintWriter writer = new PrintWriter(System.out, true)
	writer.println "Oops!! Could not perform backup...$problem.message"
//	problem.printStackTrace(writer)
} finally {
	if(mongo) {
		mongo.close()
	}
}
