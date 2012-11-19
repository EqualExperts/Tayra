package com.ee.beaver.io;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.UnknownHostException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ee.beaver.domain.NotAReplicaSetNode;
import com.ee.beaver.domain.Oplog;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class CopierSpecs {
	private static Mongo replicaSet;
	private static final String HOST = "localhost";
	private static final int PORT = 27017;
	private DB local;
	private Copier copier;
	private static final CharSequence NEW_LINE = System
			.getProperty("line.separator");
	private final String document = "\"ts\"";
	@BeforeClass
	public static void connectToMongo() throws UnknownHostException,
			MongoException {
		replicaSet = new Mongo(HOST, PORT);
	}

	@AfterClass
	public static void closeConnectionToMongo() {
		replicaSet.close();
	}

	@Before
	public void givenCopierAndReplicasetLocalDB() {
		copier = new Copier();
		local = replicaSet.getDB("local");
		boolean oplogExists = local.collectionExists("oplog.rs");
		if (!oplogExists) {
			throw new NotAReplicaSetNode(
					"localhost is not a part of ReplicaSet");
		}
	}

	@Test
	public void writesOplogToDestination() throws Exception {
		// Given
		Writer writer = new StringWriter();
		OplogReader reader = new OplogReader(new Oplog(local), false);

		// When
		copier.copy(reader, writer);

		// Then
		assertThat(writer.toString(), containsString("ts"));
	}

	@Test
	public void replaysOplog() throws Exception {
		// Given
		OplogReplayer mockOplogReplayer = mock(OplogReplayer.class);
		BufferedReader from = new BufferedReader(new StringReader(document
				+ NEW_LINE));

		// When
		copier.copy(from, mockOplogReplayer);

		// Then
		verify(mockOplogReplayer).replayDocument(document);
	}

	@Test
	public void notifiesWhenReadingADocumentFromReaderIsSuccessful()
			throws Exception {
		// Given
		OplogReplayer mockOplogReplayer = mock(OplogReplayer.class);
		BufferedReader from = new BufferedReader(new StringReader(document
				+ NEW_LINE));

		CopyListener mockCopyListener = mock(CopyListener.class);

		// When
		copier.copy(from, mockOplogReplayer, mockCopyListener);

		// Then
		verify(mockCopyListener).onReadSuccess(document);
	}

	@Test
	public void notifiesWhenWritingADocumentToReplayerIsSuccessful()
			throws Exception {
		// Given
		OplogReplayer mockOplogReplayer = mock(OplogReplayer.class);
		BufferedReader from = new BufferedReader(new StringReader(document
				+ NEW_LINE));

		CopyListener mockCopyListener = mock(CopyListener.class);

		// When
		copier.copy(from, mockOplogReplayer, mockCopyListener);

		// Then
		verify(mockCopyListener).onWriteSuccess(document);
	}

	@Test
	public void notifiesWhenReplayerOperationFails() throws Exception {
		OplogReplayer mockOplogReplayer = mock(OplogReplayer.class);
		BufferedReader from = new BufferedReader(new StringReader(document
				+ NEW_LINE));

		CopyListener mockCopyListener = mock(CopyListener.class);
		final RuntimeException problem = new RuntimeException(
				"Document to update does not exist");
		doThrow(problem).when(mockOplogReplayer).replayDocument(document);

		// When
		copier.copy(from, mockOplogReplayer, mockCopyListener);

		// Then
		verify(mockCopyListener, never()).onReadFailure(document, problem);
		verify(mockCopyListener).onReadSuccess(document);
		verify(mockCopyListener).onWriteFailure(document, problem);
		verify(mockCopyListener, never()).onWriteSuccess(document);
	}

	@Test
	public void notifiesWhenReadingFromReaderFails() throws Exception {
		OplogReplayer mockOplogReplayer = mock(OplogReplayer.class);
		BufferedReader mockReader = mock(BufferedReader.class);
		final IOException problem = new IOException();
		given(mockReader.readLine()).willThrow(problem);

		CopyListener mockCopyListener = mock(CopyListener.class);

		// When
		copier.copy(mockReader, mockOplogReplayer, mockCopyListener);

		// Then
		verify(mockCopyListener).onReadFailure(null, problem);
		verify(mockCopyListener, never()).onReadSuccess(document);
		verify(mockCopyListener, never()).onWriteSuccess(document);
		verify(mockCopyListener, never()).onWriteFailure(document, problem);
	}

	@Test
	public void notifiesWhenReadingADocumentFromOplogIsSuccessful()
			throws Exception {
		// Given
		CollectionReader mockReader = mock(CollectionReader.class);
		given(mockReader.hasDocument()).willReturn(true, false);
		given(mockReader.readDocument()).willReturn(document);
		Writer mockWriter = mock(Writer.class);
		CopyListener mockCopyListener = mock(CopyListener.class);

		// When
		copier.copy(mockReader, mockWriter, mockCopyListener);

		// Then
		verify(mockCopyListener).onReadSuccess(document);
	}

	@Test
	public void notifiesWhenWritingADocumentToWriterIsSuccessful()
			throws Exception {
		// Given
		CollectionReader mockReader = mock(CollectionReader.class);
		given(mockReader.hasDocument()).willReturn(true, false);
		given(mockReader.readDocument()).willReturn(document);
		Writer mockWriter = mock(Writer.class);
		CopyListener mockCopyListener = mock(CopyListener.class);

		// When
		copier.copy(mockReader, mockWriter, mockCopyListener);

		// Then
		verify(mockCopyListener).onWriteSuccess(document);
	}

	@Test
	public void notifiesWhenWriterFailsToWrite() throws Exception {
		// Given
		CollectionReader mockReader = mock(CollectionReader.class);
		given(mockReader.hasDocument()).willReturn(true, false);
		given(mockReader.readDocument()).willReturn(document);
		Writer mockWriter = mock(Writer.class);
		CopyListener mockCopyListener = mock(CopyListener.class);

		final IOException problem = new IOException("Disk Full");
		doThrow(problem).when(mockWriter).append(document);

		// When
		copier.copy(mockReader, mockWriter, mockCopyListener);

		// Then
		verify(mockCopyListener, never()).onReadFailure(document, problem);
		verify(mockCopyListener).onReadSuccess(document);
		verify(mockCopyListener).onWriteFailure(document, problem);
		verify(mockCopyListener, never()).onWriteSuccess(document);
	}

	@Test
	public void notifiesWhenReadingDocumentFromOplogFails() throws Exception {
		CollectionReader mockReader = mock(CollectionReader.class);
		given(mockReader.hasDocument()).willReturn(true, false);
		given(mockReader.readDocument()).willReturn(document);
		Writer mockWriter = mock(Writer.class);
		CopyListener mockCopyListener = mock(CopyListener.class);
		RuntimeException problem = new MongoException("Connection Lost!");
		given(mockReader.readDocument()).willThrow(problem);

		// When
		copier.copy(mockReader, mockWriter, mockCopyListener);

		// Then
		verify(mockCopyListener).onReadFailure(null, problem);
		verify(mockCopyListener, never()).onReadSuccess(document);
		verify(mockCopyListener, never()).onWriteSuccess(document);
		verify(mockCopyListener, never()).onWriteFailure(document, problem);
	}

}
