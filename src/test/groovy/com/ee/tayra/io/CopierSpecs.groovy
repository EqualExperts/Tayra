package com.ee.tayra.io

import com.ee.tayra.utils.StringDocumentWriter
import spock.lang.*

import com.ee.tayra.domain.NotAReplicaSetNode
import com.ee.tayra.domain.Oplog
import static com.ee.tayra.ConnectionFactory.*
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

	def writesOplogToDestination() throws Exception {
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


	def replaysOplog() throws Exception {
		given: 'a reader and an oplog replayer'
			mockOplogReplayer = Mock(OplogReplayer)
			BufferedReader from = new BufferedReader(new StringReader(document
					+ NEW_LINE))

		when: 'document is copied'
			copier.copy(from, mockOplogReplayer)

		then: 'oplog replayer should replay the document'
			1 * mockOplogReplayer.replay(document)
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
			mockReplayer = Stub(Replayer)
			BufferedReader from = new BufferedReader(new StringReader(document + NEW_LINE))

		and: 'a copy listener'
			mockCopyListener = Mock(CopyListener)

		and: 'the replayer replays the document'
			mockReplayer.replay (document) >> true

		when: ' document is copied'
			copier.copy(from, mockReplayer, mockCopyListener)

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
			mockOplogReplayer.replay(document) >> {throw problem}

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
			copier = new Copier() {
				@Override
				BufferedReader createBufferedReader(Reader reader) {
					mockReader
				}
			}

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


	def doesNotWriteEmptyDocuments() {
		given: 'a collection reader and a writer'
			CollectionReader mockReader = Mock(CollectionReader)
            DocumentWriter mockWriter = Mock(DocumentWriter)

		and: 'documents do not satisfy criteria'
			mockReader.hasDocument() >> true >> false
			mockReader.readDocument() >> ""

		when: 'the document is copied'
			copier.copy(mockReader, mockWriter)

		then: 'it notifies only read success'
			0 * mockWriter.notifyWriteSuccess(_)
	}

	def notifiesWriteStartWhenADocumentIsRead(){
		given: 'a collection reader and a writer'
			BufferedReader mockReader = Mock(BufferedReader)
			Replayer mockWriter = Mock(Replayer)
			copier = new Copier() {
				@Override
				BufferedReader createBufferedReader(Reader reader) {
					mockReader
				}
			}

		and: 'a copy listener'
			mockCopyListener = Mock(CopyListener.class)

		and: 'reader reads a document'
			mockReader.readLine() >> document >> null
			mockWriter.replay(document) >> false

		when: 'the document is copied'
			copier.copy(mockReader, mockWriter, mockCopyListener)

		then: 'it notifies a successful write'
			1 * mockCopyListener.onWriteStart(document)
	}
}
