package com.ee.beaver.command

import spock.lang.*;

import com.mongodb.MongoClient
import com.mongodb.MongoException
import com.mongodb.ServerAddress

public class MongoReplSetConnectionSpecs extends Specification {

	private String source
	private int port
	private MongoReplSetConnection mongoReplsetConnection

	def setup() {
		source = 'localhost'
		port = 27017
		mongoReplsetConnection = new MongoReplSetConnection(source, port)
	}

	def cleanup() {
		mongoReplsetConnection = null
	}

	def loansTheMongoMasterConnectionOnceAvailable() {
		given: 'the closure which needs the mongo connection'
			def actual = null
			def execute = { mongo ->
				actual = mongo
			}

		when: 'using the connection'
			mongoReplsetConnection.using(execute)

		then: 'ensure mongo connection is available'
			actual instanceof MongoClient
	}

	def allowsUserOperationBetweenMasterCrashAndReelectionAttempt() {
		given: 'master crashes'
			def called = false
			def execute = {
				if(!called) {
					throw new MongoException.Network('Master Crashed', new IOException())
				}
			}

		and: 'a retry closure'
			def retry = {
				called = true
			}

		when: 'using the connection'
			mongoReplsetConnection.using(execute, retry)

		then: 'ensure retry was invoked'
			called
	}

	def doesNotReactToAnyFailureOtherThanMasterCrash() {
		given: 'a problem other than master crash occurs'
			def notCalled = true
			def execute = {
				if(notCalled) {
					throw new MongoException('Non Master-Crash Exception')				}
			}

		and: 'a retry closure'
			def retry = {
				notCalled = false
			}

		when: 'using the connection'
			mongoReplsetConnection.using(execute, retry)

		then: 'ensure retry was not invoked'
			notCalled
			thrown(MongoException)
	}
}
