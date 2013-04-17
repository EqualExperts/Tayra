package com.ee.tayra.domain.operation

import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject
import static com.ee.tayra.ConnectionFactory.*

class RenameCollectionSpecs extends RequiresMongoConnection {
	def collectionName = 'games'
	def changedCollectionName = 'sports'
	def targetCollectionName = 'hobby'
	def operation
	def setup() {
		DBObject obj = new BasicDBObjectBuilder().start().add('name', 'abc').get()
		standalone.getDB(dbName).createCollection(collectionName, null).insert(obj)
		standalone.getDB(dbName).createCollection(targetCollectionName, null).insert(obj)
		operation = new RenameCollection()
	}
	
	def cleanup() {
		standalone.getDB(dbName).getCollection(collectionName).drop()
		standalone.getDB(dbName).getCollection(changedCollectionName).drop()
		standalone.getDB(dbName).getCollection(targetCollectionName).drop()
	}
	
	def assertThatCollectionIsRenamed(){
		standalone.getDB(dbName).collectionExists(changedCollectionName) &&
		!standalone.getDB(dbName).collectionExists(collectionName)
	}
	
	def renamesACollection() {
		given:'A rename collection oplog payload entry'
			DBObject o = new BasicDBObjectBuilder()
							.start()
								.add('renameCollection', dbName + "." + collectionName)
								.add('to', dbName + "." + changedCollectionName)
								.add('dropTarget', null)
							.get()
							
		when:'an operation runs'
			operation.doExecute(standalone.getDB(dbName), o)
		
		then: 'the collection should be renamed'
			assertThatCollectionIsRenamed()
	}
	 
	def shoutsWhenSourceNamespaceDoesNotExist() {
		given:'A rename collection oplog payload entry'
			DBObject o = new BasicDBObjectBuilder()
							.start()
								.add('renameCollection', dbName + "." + changedCollectionName)
								.add('to', dbName + "." + targetCollectionName)
								.add('dropTarget', null)
							.get()
							
		when: 'an operation runs' 
			operation.doExecute(standalone.getDB(dbName), o)
			
		then: 'it complains with the proper error'
			def problem = thrown(RenameCollectionFailed)
			problem.message.contains("""{ "serverUsed" : "localhost/127.0.0.1:27020" , "errmsg" : "exception: source namespace does not exist" , "code" : 10026 , "ok" : 0.0}""")
	}
	
	def dropsTargetWhileRenaming() {
		given:'A rename collection oplog payload entry with drop target enabled'
			DBObject o = new BasicDBObjectBuilder()
							.start()
								.add('renameCollection', dbName + "." + collectionName)
								.add('to', dbName + "." + targetCollectionName)
								.push('dropTarget')
									.add('dropTarget', true)
								.pop()
							.get()
						
		when:'an operation runs'
			operation.doExecute(standalone.getDB(dbName), o)
						
		then: 'the collection should be renamed'
			standalone.getDB(dbName).collectionExists(targetCollectionName)
	}
	
	def shoutsWhenTargetNamespaceAlreadyExists() {
		given:'A rename collection oplog payload entry with drop target disabled'
			DBObject o = new BasicDBObjectBuilder()
							.start()
								.add('renameCollection', dbName + "." + collectionName)
								.add('to', dbName + "." + targetCollectionName)
								.push('dropTarget')
									.add('dropTarget',false)
								.pop()
							.get()
							
		when: 'an operation runs'
			operation.doExecute(standalone.getDB(dbName), o)
			
		then:
			def problem = thrown(RenameCollectionFailed)
			problem.message == """{ "serverUsed" : "localhost/127.0.0.1:27020" , "errmsg" : "exception: target namespace exists" , "code" : 10027 , "ok" : 0.0}"""
	}
}
