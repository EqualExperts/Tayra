package com.ee.beaver.command

import java.net.UnknownHostException;

import spock.lang.Ignore
import spock.lang.Specification

import com.mongodb.Mongo
import com.mongodb.MongoException
import com.mongodb.MongoOptions
import com.mongodb.ServerAddress

class AuthenticatorSpecs extends Specification {

	private MongoAuthenticator authenticator
	static Mongo secured
	static final String HOST = "localhost"
	static final int PORT = 27020
	
	def setupSpec() throws UnknownHostException, MongoException {
		def options = new MongoOptions()
		options.safe = true
		ServerAddress server = new ServerAddress(HOST, PORT)
		secured = new Mongo(server, options);
	}
	public void setup() {
		authenticator = new MongoAuthenticator(secured)
	}

	def cleanupSpec() {
		secured.close();
	}
	
	def shoutsWhenNoUsernameIsGivenForSecuredReplicaSet() {
		given: 'only the password and not the username'
			def username = ''
			def password ='password'
			
		when: 'it authenticates with above args'
			authenticator.authenticate(username, password)
		
		then: 'error message should be thrown as'
			def problem = thrown(MongoException)
			problem.message == 'Username cannot be empty'
	}
	
	def shoutsWhenIncorrectCredentialsAreSupplied() {
		expect: 'it does not authenticate'
		    try {
				authenticator.authenticate(username, password)
		    } catch (MongoException problem) {
		    	problem.message == "Authentication Failed to $secured.address.host"
		    }
	
		where: 'username and password are given as'
			  username   |    password
			 'incorrect' |   'password'
			 'admin'     |   'incorrect'
	}
	
	def authenticatesAgainstSecureAndUnsecureDB() {
		given: 'a mongo DB'
			def authenticator 
		    if('Secure') {
		    	authenticator = new MongoAuthenticator(secured)
			} else {
				def unsecured = new Mongo(new ServerAddress(HOST, 27021))
				authenticator = new MongoAuthenticator(unsecured)
			}
			
		expect: 'it authenticates with above args'
			authenticated == authenticator.authenticate('admin', 'admin')
			
		where:
		       mode     | authenticated
			 'Secure'   | true
			 'unSecure' | false
	}
}
