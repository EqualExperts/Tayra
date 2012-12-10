package com.ee.beaver.domain.operation

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
		standalone.getDB('admin').addUser('admin', 'admin'.toCharArray())
		standalone.getDB('admin').authenticateCommand('admin', 'admin'.toCharArray())
	}

	def cleanupSpec() {
		standalone.getDB('admin').removeUser('admin')
		standalone.getDB('admin').dropDatabase()
		standalone.close();
	}
}
