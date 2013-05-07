package com.ee.tayra.io

import static com.ee.tayra.ConnectionFactory.*
import spock.lang.*

import com.ee.tayra.domain.NotAReplicaSetNode
import com.ee.tayra.domain.Oplog
import com.ee.tayra.io.listener.CopyListener
import com.ee.tayra.io.reader.DocumentReader
import com.ee.tayra.io.reader.FileDocumentReader
import com.ee.tayra.io.reader.OplogReader
import com.ee.tayra.io.writer.DocumentWriter
import com.ee.tayra.io.writer.OplogReplayer
import com.ee.tayra.io.writer.Replayer
import com.ee.tayra.utils.StringDocumentWriter
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DB
import com.mongodb.DBObject
import com.mongodb.MongoClient
import com.mongodb.MongoException

public class CopierSpecs extends Specification {

	private static MongoClient replicaSet
	private static final String HOST = secureSrcNode
	private static final int PORT = secureSrcPort
	private DB local
	private Copier copier
	private static final CharSequence NEW_LINE = System.getProperty("line.separator")
	private final String document = "\"ts\""
	private OplogReplayer mockOplogReplayer
	private CopyListener mockCopyListener
	private Replayer mockReplayer

	def setupSpec() throws UnknownHostException, MongoException {
		replicaSet = new MongoClient(HOST, PORT)
	}

	def cleanupSpec() {
		replicaSet.close()
	}

	def setup() {
		copier = new Copier()
		replicaSet.getDB("admin").authenticate(username, password.toCharArray())
		local = replicaSet.getDB("local")
		boolean oplogExists = local.collectionExists("oplog.rs")
		if (!oplogExists) {
			throw new NotAReplicaSetNode(
					"localhost is not a part of ReplicaSet")
		}
	}

	def writesOplogToDestination() {
		given:'a writer and an oplog reader'
			DocumentWriter writer = new StringDocumentWriter()
			DBObject dbObject = local.getCollection("oplog.rs").find().next();
			DBObject query = new BasicDBObjectBuilder()
							.start()
								.add("ts", dbObject.get("ts"))
							.get();
			OplogReader reader = new OplogReader(new Oplog(replicaSet), query.toString(), false)

		when: 'document is copied'
			copier.copy(reader, writer)

		then: 'destination writer should contain proper document'
			writer.toString().contains("ts")
	}

	def replaysOplog() {
		given: 'a reader and an oplog replayer'
			mockOplogReplayer = Mock(OplogReplayer)
			def bufferedReader = new BufferedReader(new StringReader(document + NEW_LINE))
			DocumentReader from = new FileDocumentReader(bufferedReader)

		when: 'document is copied'
			copier.copy(from, mockOplogReplayer)

		then: 'oplog replayer should replay the document'
			1 * mockOplogReplayer.replay(document)
	}
}
