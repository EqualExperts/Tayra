package com.ee.beaver.io;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ee.beaver.domain.MongoCollection;
import com.ee.beaver.domain.MongoCollectionIterator;
import com.ee.beaver.domain.ReaderAlreadyClosed;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@RunWith(MockitoJUnitRunner.class)
public class OplogReaderSpecs {

	@Mock
	private MongoCollection mockOplogCollection;

	@Mock
	private MongoCollectionIterator<DBObject> mockOplogCollectionIterator;

	private CollectionReader reader;

	@Before
	public void setupOplogReader() {
		given(mockOplogCollection.find(false)).willReturn(
				mockOplogCollectionIterator);
		reader = new OplogReader(mockOplogCollection, false);
	}

	@Test
	public void readsACreateCollectionOperationDocument() {
		// Given
		given(mockOplogCollectionIterator.hasNext()).willReturn(true);
		DBObject entry = makeCreateCollectionDBObject();
		given(mockOplogCollectionIterator.next()).willReturn(entry);

		// When
		String oplogDocumentString = reader.readDocument();

		// Then
		assertEquals(
				oplogDocumentString,
				("{ \"ts\" : { \"$ts\" : 1351755677000 , \"$inc\" : 1} , \"h\" : 8548170154004386733 , \"op\" : \"c\" , \"ns\" : \"person.$cmd\" , \"o\" : { \"create\" : \"people\"}}"));
	}

	private DBObject makeCreateCollectionDBObject() {
		DBObject entry = new BasicDBObject();

		Map<String, Long> ts = new HashMap<String, Long>();
		ts.put("$ts", 1351755677000L);
		ts.put("$inc", (long) 1);
		entry.put("ts", ts);

		entry.put("h", 8548170154004386733L);

		entry.put("op", "c");

		entry.put("ns", "person.$cmd");

		Map<String, String> o = new HashMap<String, String>();
		o.put("create", "people");
		entry.put("o", o);
		return entry;
	}

	@Test
	public void readsAnInsertOperationDocument() {
		// Given
		given(mockOplogCollectionIterator.hasNext()).willReturn(true);
		DBObject entry = makeInsertOperationDBObject();
		given(mockOplogCollectionIterator.next()).willReturn(entry);

		// When
		String oplogDocumentString = reader.readDocument();

		// Then
		assertEquals(
				oplogDocumentString,
				("{ \"ts\" : { \"$ts\" : 1352094941 , \"$inc\" : 1} , \"h\" : -5637503227244014604 , \"op\" : \"i\" , \"ns\" : \"person.things\" , \"o\" : { \"name\" : \"test\"}}"));
	}

	private DBObject makeInsertOperationDBObject() {
		DBObject entry = new BasicDBObject();

		Map<String, Long> ts = new HashMap<String, Long>();
		ts.put("$ts", 1352094941L);
		ts.put("$inc", (long) 1);
		entry.put("ts", ts);

		entry.put("h", -5637503227244014604L);

		entry.put("op", "i");

		entry.put("ns", "person.things");

		Map<String, String> o = new HashMap<String, String>();
		o.put("name", "test");
		entry.put("o", o);
		return entry;
	}

	@Test
	public void readsAnUpdateOperationDocument() {
		// Given
		given(mockOplogCollectionIterator.hasNext()).willReturn(true);
		DBObject entry = makeUpdateOperationDBObject();
		given(mockOplogCollectionIterator.next()).willReturn(entry);

		// When
		String oplogDocumentString = reader.readDocument();

		// Then
		assertEquals(
				oplogDocumentString,
				("{ \"ts\" : { \"$ts\" : 1352094941 , \"$inc\" : 1} , \"h\" : -5637503227244014604 , \"op\" : \"u\" , \"ns\" : \"person.things\" , \"o2\" : { \"$oid\" : \"50978ff171274a096d2a9b33\"} , \"o\" : { \"name\" : \"test\"}}"));
	}

	private DBObject makeUpdateOperationDBObject() {
		DBObject entry = new BasicDBObject();

		Map<String, Long> ts = new HashMap<String, Long>();
		ts.put("$ts", 1352094941L);
		ts.put("$inc", (long) 1);
		entry.put("ts", ts);

		entry.put("h", -5637503227244014604L);

		entry.put("op", "u");

		entry.put("ns", "person.things");

		Map<String, String> o2 = new HashMap<String, String>();
		o2.put("$oid", "50978ff171274a096d2a9b33");
		entry.put("o2", o2);

		Map<String, String> o = new HashMap<String, String>();
		o.put("name", "test");
		entry.put("o", o);
		return entry;
	}

	@Test
	public void readsARemoveOperationDocument() {
		// Given
		given(mockOplogCollectionIterator.hasNext()).willReturn(true);
		DBObject entry = makeRemoveOperationDBObject();
		given(mockOplogCollectionIterator.next()).willReturn(entry);

		// When
		String oplogDocumentString = reader.readDocument();

		// Then
		assertEquals(
				oplogDocumentString,
				("{ \"ts\" : { \"$ts\" : 1352094941 , \"$inc\" : 1} , \"h\" : -5637503227244014604 , \"op\" : \"d\" , \"ns\" : \"person.things\" , \"b\" : true , \"o\" : { \"$oid\" : \"509754dd2862862d511f6b57\"}}"));
	}

	private DBObject makeRemoveOperationDBObject() {
		DBObject entry = new BasicDBObject();

		Map<String, Long> ts = new HashMap<String, Long>();
		ts.put("$ts", 1352094941L);
		ts.put("$inc", (long) 1);
		entry.put("ts", ts);

		entry.put("h", -5637503227244014604L);

		entry.put("op", "d");
		entry.put("ns", "person.things");
		entry.put("b", true);

		Map<String, String> o = new HashMap<String, String>();
		o.put("$oid", "509754dd2862862d511f6b57");
		entry.put("o", o);
		return entry;
	}

	@Test
	public void readsADropCollectionOperationDocument() {
		// Given
		given(mockOplogCollectionIterator.hasNext()).willReturn(true);
		DBObject entry = makeDropCollectionOperationDBObject();
		given(mockOplogCollectionIterator.next()).willReturn(entry);

		// When
		String oplogDocumentString = reader.readDocument();

		// Then
		assertEquals(
				oplogDocumentString,
				("{ \"ts\" : { \"$ts\" : 1351755677000 , \"$inc\" : 1} , \"h\" : 8548170154004386733 , \"op\" : \"c\" , \"ns\" : \"person.$cmd\" , \"o\" : { \"drop\" : \"people\"}}"));

	}

	private DBObject makeDropCollectionOperationDBObject() {
		DBObject entry = new BasicDBObject();

		Map<String, Long> ts = new HashMap<String, Long>();
		ts.put("$ts", 1351755677000L);
		ts.put("$inc", (long) 1);
		entry.put("ts", ts);

		entry.put("h", 8548170154004386733L);

		entry.put("op", "c");

		entry.put("ns", "person.$cmd");

		Map<String, String> o = new HashMap<String, String>();
		o.put("drop", "people");
		entry.put("o", o);
		return entry;
	}

	@Test
	public void shoutsWhenQueryingForDocumentWithAClosedReader() {
		// When
		reader.close();

		try {
			reader.hasDocument();
			fail("Should Not Allow To Work With A closed Reader");
		} catch (ReaderAlreadyClosed rac) {
			assertThat(rac.getMessage(), is("Reader Already Closed"));
		}
	}

	@Test
	public void shoutsWhenFetchingForDocumentWithAClosedReader() {
		// When
		reader.close();

		try {
			reader.readDocument();
			fail("Should Not Allow To Work With A closed Reader");
		} catch (ReaderAlreadyClosed rac) {
			assertThat(rac.getMessage(), is("Reader Already Closed"));
		}
	}
}
