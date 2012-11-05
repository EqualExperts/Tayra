package com.ee.beaver.io;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

import java.io.StringWriter;
import java.io.Writer;
import java.net.UnknownHostException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ee.beaver.domain.NotALocalDB;
import com.ee.beaver.domain.Oplog;
import com.ee.beaver.io.OplogReader;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class CopierSpecs {
	private static Mongo replicaSet;
	private static final String HOST = "localhost";
	private static final int PORT = 27017;
	private DB local;

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
	public void useLocalDB() {
		local = replicaSet.getDB("local");
		boolean oplogExists = local.collectionExists("oplog.rs");
	    if (!oplogExists) {
	      throw new NotAReplicaSetNode("localhost is not a part of ReplicaSet");
	    }
	}

	@Test
	public void connectsToANodeInReplicaSet() throws Exception {
		assertThat(new Copier(local), notNullValue());
	}

	@Test
	public void doesNotConnectToStandaloneMongoInstance() throws Exception {
		Mongo standalone = new Mongo(HOST, 27020);
		DB local = standalone.getDB("local");
		try {
			// When
			new Copier(local);
			fail("Should Not Connect to a standalone node");
		} catch (NotAReplicaSetNode e) {
			// Then
			assertThat(e.getMessage(),
					is("localhost is not a part of ReplicaSet"));
		}
	}

	@Test
	public void permitsReadingFromLocalDBOnlyForReplicaSet() {
		// Given
		DB nonLocalDB = replicaSet.getDB("nonLocal");

		// When
		try {
			new Copier(nonLocalDB);
			fail("DB used is not local, should raise an Exception");
		} catch (NotALocalDB naldb) {
			// Then
			assertThat(naldb.getMessage(), is("Not a local DB"));
		}
	}

	@Test
	public void writesOplogToDestination() throws Exception {
		// Given
		Copier copier = new Copier(local);
		Writer writer = new StringWriter();
		OplogReader reader = new OplogReader(new Oplog(local));

		// When
		copier.copy(reader, writer);

		// Then
		assertThat(writer.toString(), containsString("ts"));
	}
}
