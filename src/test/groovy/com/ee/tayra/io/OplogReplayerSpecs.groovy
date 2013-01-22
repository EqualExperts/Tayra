package com.ee.tayra.io

import org.bson.types.BSONTimestamp
import org.bson.types.ObjectId

import spock.lang.*

import com.ee.tayra.domain.operation.*
import com.ee.tayra.io.OplogReplayer;
import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject

public class OplogReplayerSpecs extends Specification {
	
	private OplogReplayer replayer
	private OperationsFactory mockOperations
	private Operation mockOperation
	
	def setup() {
		mockOperations = Mock(OperationsFactory)
		mockOperation = Mock(Operation)
		replayer = new OplogReplayer(mockOperations)
	}

	def replaysCreateCollectionOperation() throws Exception {
		given: 'an Oplog entry for Create Collection'
			def builder = new MongoUtils().createCollection('person', 'testCollection')
			def oplogDocString = builder as String
			
		and: 'operations factory gets a Create Collection Operation'
			mockOperations.get('c') >> mockOperation

		when: 'Replayer replays an Oplog Entry String'
			replayer.replay(oplogDocString)

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
			mockOperations.get('i') >> mockOperation

		when: 'Replayer replays an Oplog Entry String'
			replayer.replay(oplogDocString)

		then: 'Insert Operation executes the Oplog Entry'
			1 * mockOperation.execute(oplogDocString)
	}
	
	def extractsCorrectOpcode() {
		given: 'an Oplog entry for Create Collection with trailing spaces for opcode'
			def builder = new DocumentBuilder(
				ts: new BSONTimestamp(1352105652, 1),
				h :'3493050463814977392',
				op :'   c ',
				ns : 'person' + '.$cmd',
				o : new BasicDBObjectBuilder()
						.start()
							.add('create', 'testCollection')
							.add('capped', false)
							.add('size', null)
							.add('max', null)
						.get()
			)		
			def oplogDocString = builder as String
			
		when: 'Replayer replays an Oplog Entry String'
			replayer.replay(oplogDocString)
	
		then: 'Operation is called with correct opcode parameter'
			mockOperations.get('c') >> mockOperation
			0 * mockOperations.get('   c ')
			mockOperation.execute(oplogDocString)
	}
}