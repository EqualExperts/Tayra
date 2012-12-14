package com.ee.beaver.io

import spock.lang.*

import com.ee.beaver.domain.NotAReplicaSetNode
import com.ee.beaver.domain.Oplog
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DB
import com.mongodb.DBObject
import com.mongodb.Mongo
import com.mongodb.MongoException

public class CopierSpecs extends Specification {

	private static Mongo replicaSet
	private static final String HOST = "localhost"
	private static final int PORT = 27017
	private DB local
	private Copier copier
	private static final CharSequence NEW_LINE = System.getProperty("line.separator")
	private final String document = "\"ts\""
	private OplogReplayer mockOplogReplayer
	private CopyListener mockCopyListener

	def setupSpec() throws UnknownHostException, MongoException {
		replicaSet = new Mongo(HOST, PORT)
	}

	def cleanupSpec() {
		replicaSet.close()
	}

	def setup() {
		copier = new Copier()
		replicaSet.getDB("admin").authenticate("admin", "admin".toCharArray())
		local = replicaSet.getDB("local")
		boolean oplogExists = local.collectionExists("oplog.rs")
		if (!oplogExists) {
			throw new NotAReplicaSetNode(
					"localhost is not a part of ReplicaSet")
		}
	}

	def writesOplogToDestination() throws Exception {
		given:'a writer and an oplog reader'
			Writer writer = new StringWriter()
			DBObject dbObject = local.getCollection("oplog.rs").find().next();
			DBObject query = new BasicDBObjectBuilder()
							.start()
								.add("ts", dbObject.get("ts"))
							.get();
			OplogReader reader = new OplogReader(new Oplog(replicaSet), query.toString(), false)

		when: 'document is copied'
			copier.copy(reader, writer)

		then: 'destination writer should contatin proper document'
			writer.toString().contains("ts")
	}


	def replaysOplog() throws Exception {
		given: 'a reader and an oplog replayer'
			mockOplogReplayer = Mock(OplogReplayer)
			BufferedReader from = new BufferedReader(new StringReader(document
					+ NEW_LINE))

		when: 'document is copied'
			copier.copy(from, mockOplogReplayer)

		then: 'oplog replayer should replay the document'
			1 * mockOplogReplayer.replayDocument(document)
	}


	def notifiesWhenReadingADocumentFromReaderIsSuccessful()
			throws Exception {
		given: 'a reader and an oplog replayer'
			mockOplogReplayer = Mock(OplogReplayer)
			BufferedReader from = new BufferedReader(new StringReader(document + NEW_LINE))

		and: 'a copy listener'
			mockCopyListener = Mock(CopyListener)

		when: 'document is copied'
			copier.copy(from, mockOplogReplayer, mockCopyListener)

		then: 'a notification of successful read is given'
			1 * mockCopyListener.onReadSuccess(document)
	}


	def notifiesWhenWritingADocumentToReplayerIsSuccessful()
			throws Exception {
		given: 'a reader and an oplog replayer'
			mockOplogReplayer = Mock(OplogReplayer)
			BufferedReader from = new BufferedReader(new StringReader(document + NEW_LINE))

		and: 'a copy listener'
			mockCopyListener = Mock(CopyListener)

		when: ' document is copied'
			copier.copy(from, mockOplogReplayer, mockCopyListener)

		then: 'a notification of successful write is given'
			1 * mockCopyListener.onWriteSuccess(document)
	}


	def notifiesWhenReplayerOperationFails() throws Exception {
		given: 'a reader and an oplog replayer'
			mockOplogReplayer = Mock(OplogReplayer)
			BufferedReader from = new BufferedReader(new StringReader(document + NEW_LINE))

		and: 'a copy listener'
			mockCopyListener = Mock(CopyListener)

		and: 'a problem occurs when the replay fails'
			final RuntimeException problem = new RuntimeException(
					"Document to update does not exist")
			mockOplogReplayer.replayDocument(document) >> {throw problem}

		when: 'document is copied'
			copier.copy(from, mockOplogReplayer, mockCopyListener)

		then: 'notifies a successful read'
			0 * mockCopyListener.onReadFailure(document, problem)
			1 * mockCopyListener.onReadSuccess(document)

		and: 'a failed write'
			1 * mockCopyListener.onWriteFailure(document, problem)
			0 * mockCopyListener.onWriteSuccess(document)
	}


	def notifiesWhenReadingFromReaderFails() throws Exception {
		given: 'a reader and an oplog replayer'
			mockOplogReplayer = Mock(OplogReplayer)
			BufferedReader mockReader = Mock(BufferedReader)

		and: 'a copy listener'
			mockCopyListener = Mock(CopyListener)

		and: 'a problem occurs while reading'
			final IOException problem = new IOException()
			mockReader.readLine() >> {throw problem}

		when: 'the document is copied'
			copier.copy(mockReader, mockOplogReplayer, mockCopyListener)

		then: 'it notifies a failed read only'
			1 * mockCopyListener.onReadFailure(null, problem)
			0 * mockCopyListener.onReadSuccess(document)
			0 * mockCopyListener.onWriteSuccess(document)
			0 * mockCopyListener.onWriteFailure(document, problem)
	}


	def notifiesWhenReadingADocumentFromOplogIsSuccessful()
			throws Exception {
		given: 'a collection reader and a writer'
			CollectionReader mockReader = Mock(CollectionReader)
			Writer mockWriter = Mock(Writer)

		and: 'a copy listener'
			mockCopyListener = Mock(CopyListener)

		and: 'reader reads a document'
			mockReader.hasDocument() >>true >> false
			mockReader.readDocument() >> document


		when: 'the document is copied'
			copier.copy(mockReader, mockWriter, mockCopyListener)

		then: 'it notifies a successful read'
			1 * mockCopyListener.onReadSuccess(document)
	}


	def notifiesWhenWritingADocumentToWriterIsSuccessful()
			throws Exception {
		given: 'a collection reader and a writer'
			CollectionReader mockReader = Mock(CollectionReader)
			Writer mockWriter = Mock(Writer)

		and: 'a copy listener'
			mockCopyListener = Mock(CopyListener.class)

		and: 'reader reads a document'
			mockReader.hasDocument() >>true >> false
			mockReader.readDocument() >> document

		when: 'the document is copied'
			copier.copy(mockReader, mockWriter, mockCopyListener)

		then: 'it notifies a successful write'
			1 * mockCopyListener.onWriteSuccess(document)
	}


	def notifiesWhenWriterFailsToWrite() throws Exception {
		given: 'a collection reader and a writer'
			CollectionReader mockReader = Mock(CollectionReader)
			Writer mockWriter = Mock(Writer)

		and: 'a copy listener'
			mockCopyListener = Mock(CopyListener)

		and: 'reader reads a document'
			mockReader.hasDocument() >> true >> false
			mockReader.readDocument() >> document

		and: 'a problem occurs while writing'
			final IOException problem = new IOException("Disk Full")
			mockWriter.append(document) >> {throw problem}

		when: 'the document is copied'
			copier.copy(mockReader, mockWriter, mockCopyListener)

		then: 'it notifies a successful read'
			0 * mockCopyListener.onReadFailure(document, problem)
			1 * mockCopyListener.onReadSuccess(document)

		and: 'a failed write'
			1 * mockCopyListener.onWriteFailure(document, problem)
			0 * mockCopyListener.onWriteSuccess(document)
	}


	def notifiesWhenReadingDocumentFromOplogFails() throws Exception {
		given: 'a collection reader and a writer'
			CollectionReader mockReader = Mock(CollectionReader)
			Writer mockWriter = Mock(Writer)

		and: 'a copy listener'
			mockCopyListener = Mock(CopyListener)

		and: 'problem occurs while reading the document'
			mockReader.hasDocument() >> true
			RuntimeException problem = new MongoException("Connection Lost!")
			mockReader.readDocument() >> { throw problem }

		when: 'the document is copied'
			copier.copy(mockReader, mockWriter, mockCopyListener)

		then: 'it notifies a failed read only'
			1 * mockCopyListener.onReadFailure(null, problem)
			0 * mockCopyListener.onReadSuccess(document)
			0 * mockCopyListener.onWriteSuccess(document)
			0 * mockCopyListener.onWriteFailure(document, problem)
	}

}
