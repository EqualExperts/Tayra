package com.ee.beaver.domain.operation

import org.bson.types.BSONTimestamp
import com.mongodb.BasicDBObjectBuilder

class MongoUtils {
	
	def createCollection(db, collectionName, isCapped = false, size = null, max = null) {
		new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'c',
			ns : db + '.$cmd',
			o : new BasicDBObjectBuilder()
					.start()
						.add('create', collectionName)
						.add('capped', isCapped)
						.add('size', size)
						.add('max', max)
					.get()
		)
	}
	
	def dropCollection(db, collectionName) {
		new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'c',
			ns : db + '.$cmd',
			o : new BasicDBObjectBuilder()
					.start()
			        	.add('drop', collectionName)
					.get()
		    )
	}
	
	def dropDatabase(db) {
		new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'c',
			ns : db + '.$cmd',
			o : new BasicDBObjectBuilder()
					.start()
						.add('dropDatabase', 1)
			        .get()
			    )
	}
	
	def deleteDocument(db, collectionName, o) {
		new DeleteDocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'d',
			ns : "$db.$collectionName",
			b : true,
			o : o
		)
	}
	
	def insertDocument(db, collectionName, o) {
	    new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'i',
			ns : "$db.$collectionName",
			o : o
		)
	}	
	
	def updateDocument(db, collectionName, o2, o) {
		new UpdateDocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'u',
			ns : "$db.$collectionName",
			o2 : o2,
			o : o
		)
	}
}
