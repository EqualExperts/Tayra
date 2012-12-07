package com.ee.beaver.domain.operation

import com.mongodb.BasicDBObjectBuilder
import com.mongodb.Mongo
import org.bson.types.ObjectId

import spock.lang.*

class UserSpecs extends Specification {

	def HOST = 'localhost'
	def PORT = 27023
	def objId = new ObjectId()
	def adminDBName = 'admin'
	def userCollection = 'system.users'
	def authStandalone
	
	def setup() {
		authStandalone = new Mongo("localhost",PORT )
	}
	
	def cleanup() {
		authStandalone.getDB(adminDBName).dropDatabase()
		authStandalone.close();
	}
	
	def insertsAddUserDocument() {
		given: 'an insert document operation'
			def operation = new InsertDocument(authStandalone)
			
		and: 'an add user insert document oplog entry'
			def o = BasicDBObjectBuilder
					.start()
						.add('_id', objId)
						.add('user', 'test')
						.add('readOnly', false)
						.add('pwd', 'e78333b96cbdc20a67432095f4741222')
					.get()
			def document = MongoUtils.insertDocument(adminDBName, userCollection, o) as String

		when: 'the operation runs'
			operation.execute(document)
			
		and: 'a new connection to mongo is opened'
			def authStandaloneTwo = new Mongo("localhost",PORT )

		then: 'the user should be able to login'
			authStandaloneTwo.getDB(adminDBName).isAuthenticated() == false
			authStandaloneTwo.getDB(adminDBName).authenticate('test', "123".toCharArray())
			authStandaloneTwo.getDB(adminDBName).isAuthenticated()
		
		and: 'the document should exist'
			authStandaloneTwo.getDB(adminDBName).getCollection(userCollection).findOne(o) == o
			
		cleanup: 'close connections'
			authStandaloneTwo.getDB(adminDBName).dropDatabase()
			authStandaloneTwo.close();
	}
	
	def deletesAUser() {
		given: 'an delete document operation'
			def operation = new DeleteDocument(authStandalone)
			
		and: 'a delete document oplog entry for deleting user'
			def o = new BasicDBObjectBuilder()
						.start()
							.add('user', 'test')
						.get()
			def document = MongoUtils.deleteDocument(adminDBName, userCollection,o) as String
			
		and: 'a user already exists'
			def documentToBeDeleted = BasicDBObjectBuilder
										.start()
											.add('_id', objId)
											.add('user', 'test')
											.add('readOnly', false)
											.add('pwd', 'e78333b96cbdc20a67432095f4741222')
										.get()
			authStandalone.getDB(adminDBName).getCollection(userCollection).insert(documentToBeDeleted)
	
		when: 'user is authorised'
			authStandalone.getDB(adminDBName).authenticate('test', "123".toCharArray())
			
		and: 'the operation runs'
			operation.execute(document)
	
		then: 'user document should not be present'
			authStandalone.getDB(adminDBName).getCollection(userCollection).findOne(o) == null
	}
}

