package com.ee.beaver.domain.operation

import java.net.UnknownHostException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress

abstract class RequiresMongoConnection {
	static Mongo standalone;
	static final String HOST = "localhost"
	static final int PORT = 27020
	String db = 'beaver'
	
	@BeforeClass
	public static void connectToMongo() throws UnknownHostException,
			MongoException {
		def options = new MongoOptions()
		options.safe = true
		ServerAddress server = new ServerAddress(HOST, PORT)
		standalone = new Mongo(server, options);
	}

	@AfterClass
	public static void closeConnectionToMongo() {
		standalone.close();
	}
}
