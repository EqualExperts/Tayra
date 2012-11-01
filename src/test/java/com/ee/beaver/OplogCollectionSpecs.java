package com.ee.beaver;

import java.net.UnknownHostException;
import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

import com.google.gson.Gson;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class OplogCollectionSpecs {

	private static Mongo mongo;
	private static OplogCollection oplog;

	@BeforeClass
	public static void connectToMongo() throws UnknownHostException, MongoException {
		String host = "localhost";
		int port = 27017;
		mongo = new Mongo(host, port);
		oplog = new PrimaryOplogCollection(mongo);
		Iterator<OplogDocument> iterator = oplog.find();
		if(!iterator.hasNext()) {
			fail("DID NOT find oplog on PRIMARY, please check whether the node is a part of ReplicaSet");
		}
	}

	@AfterClass
	public static void closeConnectionToMongo() {
		mongo.close();
	}

	@Test
	public void itReadsFirstDocument() throws UnknownHostException, MongoException {
		//Given
		Iterator<OplogDocument> iterator = oplog.find();

		//When
		OplogDocument oplogDocument = iterator.next();

		//Then
		assertThat(oplogDocument.o, notNullValue());
		assertThat(oplogDocument.ts, notNullValue());
		assertThat(oplogDocument.h, notNullValue());
		assertThat(oplogDocument.op, notNullValue());
		assertThat(oplogDocument.ns, notNullValue());

	}

	@Test
	public void doesNotAllowDocumentRemoval() {
		//Given
		Iterator<OplogDocument> iterator = oplog.find();

		//When
		try {
			iterator.remove();
			fail("Removing Document is not supported, Should have raised Exception");
		} catch (UnsupportedOperationException uoe) {
			//Then
			assertThat(uoe.getMessage(), is("remove document on oplog is not supported"));
		}
	}
}
