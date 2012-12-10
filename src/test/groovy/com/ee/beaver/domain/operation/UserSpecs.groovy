package com.ee.beaver.domain.operation

import com.mongodb.BasicDBObjectBuilder
import com.mongodb.Mongo
import org.bson.types.ObjectId

import spock.lang.*

class UserSpecs extends RequiresMongoConnection {

	def objId = new ObjectId()
	def adminDBName = 'admin'
	def userCollection = 'system.users'
	def username = 'test'
	def password = '123'
	
	def insertsAddUserDocument() {
		given: 'an insert document operation'
			def operation = new InsertDocument(standalone)
			
		and: 'an add user insert document oplog entry'
			def o = BasicDBObjectBuilder
					.start()
						.add('_id', objId)
						.add('user', username)
						.add('readOnly', false)
						.add('pwd', 'e78333b96cbdc20a67432095f4741222')
					.get()
			def document = MongoUtils.insertDocument(adminDBName, userCollection, o) as String

		when: 'the operation runs'
			operation.execute(document)
			
		and: 'a new connection to mongo is opened'
			def authStandaloneTwo = new Mongo(HOST,PORT )

		then: 'the user should be able to login'
			authStandaloneTwo.getDB(adminDBName).isAuthenticated() == false
			authStandaloneTwo.getDB(adminDBName).authenticate(username, password.toCharArray())
			authStandaloneTwo.getDB(adminDBName).isAuthenticated()
		
		and: 'the document should exist'
			authStandaloneTwo.getDB(adminDBName).getCollection(userCollection).findOne(o) == o
			
		cleanup: 'close connections'
			authStandaloneTwo.getDB(adminDBName).removeUser(username)
			authStandaloneTwo.close();
	}
	
	def deletesAUser() {
		given: 'an delete document operation'
			def operation = new DeleteDocument(standalone)
			
		and: 'a delete document oplog entry for deleting user'
			def o = new BasicDBObjectBuilder()
						.start()
							.add('user', username)
						.get()
			def document = MongoUtils.deleteDocument(adminDBName, userCollection,o) as String
			
		and: 'a user already exists'
			standalone.getDB(adminDBName).addUser(username,password.toCharArray())
	
		when: 'the operation runs'
			operation.execute(document)
	
		then: 'user document should not be present'
			standalone.getDB(adminDBName).getCollection(userCollection).findOne(o) == null
	}
}
