package com.ee.tayra.domain.operation

import java.net.UnknownHostException

import com.mongodb.Mongo
import com.mongodb.MongoException
import com.mongodb.MongoOptions
import com.mongodb.ServerAddress
import spock.lang.*

abstract class RequiresMongoConnection extends Specification {
	
	static Mongo standalone;
	static final String HOST = "localhost"
	static final int PORT = 27020
	String dbName = 'beaver'
	
	def setupSpec() throws UnknownHostException,
			MongoException {
		def options = new MongoOptions()
		options.safe = true
		ServerAddress server = new ServerAddress(HOST, PORT)
		standalone = new Mongo(server, options);
		standalone.getDB('admin').authenticate('admin', 'admin'.toCharArray())
	}

	def cleanupSpec() {
		standalone.close();
	}
}
