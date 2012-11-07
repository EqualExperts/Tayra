package com.ee.beaver.io;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
	public void writesOplogToDestination() throws Exception {
		// Given
		Copier copier = new Copier();
		Writer writer = new StringWriter();
		OplogReader reader = new OplogReader(new Oplog(local));

		// When
		copier.copy(reader, writer);

		// Then
		assertThat(writer.toString(), containsString("ts"));
	}
}
