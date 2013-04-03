package com.ee.tayra.connector

import spock.lang.Specification

import static com.ee.tayra.ConnectionFactory.*
import com.mongodb.MongoClient
import com.mongodb.MongoException
import com.mongodb.ServerAddress

class AuthenticatorSpecs extends Specification {

	private MongoAuthenticator authenticator
	static MongoClient secured

	def setupSpec() throws UnknownHostException, MongoException {
		ServerAddress server = new ServerAddress(secureTgtNode, secureTgtPort)
		secured = new MongoClient(server);
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
				authenticator.authenticate(user, passwd)
		    } catch (MongoException problem) {
		    	problem.message == "Authentication Failed to $secured.address.host"
		    }
	
		where: 'username and password are given as'
		      user   |    passwd
         'incorrect' |   'password'
		 'admin'     |   'incorrect'
	}
	
	def authenticatesAgainstSecureAndUnsecureDB() {
		given: 'a mongo DB'
			def authenticator 
		    if('Secure') {
		    	authenticator = new MongoAuthenticator(secured)
			} else {
				def unsecured = new MongoClient(new ServerAddress(unsecureSrcNode, unsecureSrcPort))
				authenticator = new MongoAuthenticator(unsecured)
			}
			
		expect: 'it authenticates with above args'
			authenticated == authenticator.authenticate('admin', 'admin')
			
		where:
		       mode     | authenticated
			 'Secure'   | true
			 'unSecure' | false
	}
	
	def shoutsWhenInvalidPortIsGiven() {
		given: 'Invalid port is given '
			ServerAddress server = new ServerAddress('localhost', 34567)
			def mongo = new MongoClient(server)
			def username = ''
			def password =''
			
		when: 'makes an authenticator with the above mongo'
			authenticator = new MongoAuthenticator(mongo)
			
		and: 'authenticates with above args'
			authenticator.authenticate(username, password)
		
		then: 'error message should be thrown as'
			def problem = thrown(MongoException.Network)
			problem.message == 'can\'t call something : localhost/127.0.0.1:34567/admin'
	}
}
