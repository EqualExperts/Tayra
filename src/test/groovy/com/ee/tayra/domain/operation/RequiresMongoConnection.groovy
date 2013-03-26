package com.ee.tayra.domain.operation

import com.ee.tayra.Environment

import com.mongodb.Mongo
import com.mongodb.MongoException
import com.mongodb.MongoOptions
import com.mongodb.ServerAddress
import spock.lang.*

abstract class RequiresMongoConnection extends Specification {
    static String HOST
    static int PORT
	static Mongo standalone
	String dbName = 'tayra'
	
	def setupSpec() throws UnknownHostException,
			MongoException {
		def options = new MongoOptions()
		options.safe = true
        def parameters = Environment.settings()
        HOST = parameters.get('{secureTgtNode}')
        PORT = Integer.parseInt(parameters.get('{secureTgtPort}'))
		ServerAddress server = new ServerAddress(HOST, PORT)
		standalone = new Mongo(server, options);
		standalone.getDB('admin').authenticate('admin', 'admin'.toCharArray())
	}

	def cleanupSpec() {
		standalone.close();
	}
}
