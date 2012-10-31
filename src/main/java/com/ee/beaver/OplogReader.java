package com.ee.beaver;

import java.util.Iterator;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class OplogReader {

	private final OplogCollection collection;

	public OplogReader(OplogCollection collection) {
		this.collection = collection;
	}

	public OplogDocument readDocument() {
		Iterator<OplogDocument> iterator = collection.iterator();
		while(iterator.hasNext()) {
			OplogDocument oplogDocument = iterator.next();
			return oplogDocument;
		}

		return null;
	}

}
