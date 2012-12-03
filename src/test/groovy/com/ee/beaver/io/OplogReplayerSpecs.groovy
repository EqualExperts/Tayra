package com.ee.beaver.io

import org.bson.types.ObjectId

import spock.lang.*

import com.ee.beaver.domain.operation.*
import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject

public class OplogReplayerSpecs extends Specification{

	private OplogReplayer replayer
	private OperationsFactory operations
	private Operation mockOperation
	
	def setup() {
		operations = Stub(OperationsFactory)
		mockOperation = Mock (Operation)
		replayer = new OplogReplayer(operations)
	}

	def replaysCreateCollectionOperation() throws Exception {
		given: 'an Oplog entry for Create Collection'
			def builder = new MongoUtils().createCollection('person', 'testCollection')
			def oplogDocString = builder as String
			
		and: 'operations factory gets a Create Collection Operation'
			operations.get('c') >> mockOperation

		when: 'Replayer replays an Oplog Entry String'
			replayer.replayDocument(oplogDocString)

		then: 'Create Collection Operation executes the Oplog entry'
			1 * mockOperation.execute(oplogDocString)
	}

	def replaysInsertDocumentOperation() throws Exception {
		given: 'an Oplog entry for Insert Operation'
			def o = new BasicDBObjectBuilder()
						.start()
							.add( '_id' , new BasicDBObject('$oid', new ObjectId()))
							.add( 'name' , '[Test Name]')
						.get()
			def builder = new MongoUtils().insertDocument('person', 'things', o)
			def oplogDocString = builder as String

		and: 'operations factory gets a Insert Operation Operation'
			operations.get('i') >> mockOperation

		when: 'Replayer replays an Oplog Entry String'
			replayer.replayDocument(oplogDocString)

		then: 'Insert Operation executes the Oplog Entry'
			1 * mockOperation.execute(oplogDocString)
	}
}