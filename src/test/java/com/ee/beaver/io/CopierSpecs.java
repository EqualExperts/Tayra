package com.ee.beaver.io;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
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
import com.ee.beaver.io.OplogReader;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;


public class CopierSpecs {
	private static Mongo replicaSet;
	private static final String HOST = "localhost";
	private static final int PORT = 27017;
	private DB local;
	private Copier copier;
	private static final CharSequence NEW_LINE = System.getProperty("line.separator");

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
	      throw new NotAReplicaSetNode("localhost is not a part of ReplicaSet");
	    }
	}

	@Test
	public void copiesOplogToDestination() throws Exception {
		// Given
		Writer writer = new StringWriter();
		OplogReader reader = new OplogReader(new Oplog(local));

		// When
		copier.copy(reader, writer);

		// Then
		assertThat(writer.toString(), containsString("ts"));
	}
	
	@Test
	public void replaysOplog() throws Exception {
		//Given
		OplogReplayer mockOplogReplayer = mock(OplogReplayer.class);
		BufferedReader from = new BufferedReader(new StringReader("\"ts\"" + NEW_LINE));
		
		//When
		copier.copy(from, mockOplogReplayer);
		
		//Then   
		verify(mockOplogReplayer).replayDocument("\"ts\"");
	}
	
	@Test
	public void notifiesWhenReadingADocumentIsDone() throws Exception {
		//Given
		OplogReplayer mockOplogReplayer = mock(OplogReplayer.class);
		BufferedReader from = new BufferedReader(new StringReader("\"ts\"" + NEW_LINE));
		
		CopyListener mockCopyListener = mock(CopyListener.class);
		
		//When
		copier.copy(from, mockOplogReplayer, new CopyListener[] {mockCopyListener});
		
		//Then   
		verify(mockCopyListener).onReadSuccess("\"ts\"");
	}
	
	@Test
	public void notifiesWhenWritingADocumentIsDone() throws Exception {
		//Given
		OplogReplayer mockOplogReplayer = mock(OplogReplayer.class);
		BufferedReader from = new BufferedReader(new StringReader("\"ts\"" + NEW_LINE));
		
		CopyListener mockCopyListener = mock(CopyListener.class);
		
		//When
		copier.copy(from, mockOplogReplayer, new CopyListener[] {mockCopyListener});
		
		//Then   
		verify(mockCopyListener).onWriteSuccess("\"ts\"");
	}
	
	@Test
	public void notifiesWhenRestoreOperationFails() throws Exception {
		OplogReplayer mockOplogReplayer = mock(OplogReplayer.class);
		final String document = "\"ts\"";
		BufferedReader from = new BufferedReader(new StringReader("\"ts\"" + NEW_LINE));
		
		CopyListener mockCopyListener = mock(CopyListener.class);
		final RuntimeException problem = new RuntimeException("Document to update does not exist");
		doThrow(problem)
			.when(mockOplogReplayer)
			.replayDocument(document);
		
		//When
		copier.copy(from, mockOplogReplayer, new CopyListener[] {mockCopyListener});
		
		//Then   
		verify(mockCopyListener, never()).onReadFailure(document, problem);
		verify(mockCopyListener).onReadSuccess(document);
		verify(mockCopyListener).onWriteFailure(document, problem);
		verify(mockCopyListener, never()).onWriteSuccess(document);
	}
	
	@Test
	public void notifiesWhenReadingFromSourceFails() throws Exception {
		OplogReplayer mockOplogReplayer = mock(OplogReplayer.class);
		final String document = "\"ts\"";
		BufferedReader mockReader = mock(BufferedReader.class);
		final IOException problem = new IOException();
		given(mockReader.readLine()).willThrow(problem);
		
		CopyListener mockCopyListener = mock(CopyListener.class);
		
		//When
		copier.copy(mockReader, mockOplogReplayer, new CopyListener[] {mockCopyListener});
		
		//Then   
		verify(mockCopyListener).onReadFailure(null, problem);
		verify(mockCopyListener, never()).onReadSuccess(document);
		verify(mockCopyListener, never()).onWriteSuccess(document);
		verify(mockCopyListener, never()).onWriteFailure(document, problem);
	}
}
