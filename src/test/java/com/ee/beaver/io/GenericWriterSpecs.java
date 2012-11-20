package com.ee.beaver.io;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ee.beaver.domain.MongoCollection;
import com.ee.beaver.domain.MongoCollectionIterator;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@RunWith(MockitoJUnitRunner.class)
public class GenericWriterSpecs {

	@Mock
	private Writer mockTargetWriter;
	@Mock
	private MongoCollection mockOplogCollection;
	@Mock
	private MongoCollectionIterator<DBObject> mockOplogCollectionIterator;
	private OplogReader reader;
	private StringWriter timestampWriter;
	private GenericWriter genericWriter;

	@Before
	public void setup() {
		// Given
		given(mockOplogCollection.find()).willReturn(
				mockOplogCollectionIterator);
		reader = new OplogReader(mockOplogCollection, false);
		given(mockOplogCollectionIterator.hasNext()).willReturn(true)
				.willReturn(true).willReturn(false);
		DBObject document1 = makeDBObject1();
		DBObject document2 = makeDBObject2();
		given(mockOplogCollectionIterator.next()).willReturn(document1)
				.willReturn(document2);

		timestampWriter = new StringWriter();
		genericWriter = new GenericWriter(timestampWriter, mockTargetWriter);
	}

	private DBObject makeDBObject1() {
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

	private DBObject makeDBObject2() {
		DBObject entry = new BasicDBObject();

		Map<String, Long> ts = new HashMap<String, Long>();
		ts.put("$ts", 1352094942L);
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
	public void writesTimestamptoDestination() throws IOException {
		// When
		String documentRead = reader.readDocument();
		genericWriter.write(documentRead, 0, documentRead.length());

		// Then
		String expectedTimestamp = ("\"ts\" : { \"$ts\" : 1352094941 ,"
				+ " \"$inc\" : 1}");
		assertThat(timestampWriter.toString(), is(expectedTimestamp));
	}

	@Test
	public void delegatesWritesToTargetWriter() throws IOException {
		// When
		String documentRead = reader.readDocument();
		genericWriter.write(documentRead, 0, documentRead.length());

		// Then
		verify(mockTargetWriter).append(documentRead, 0, documentRead.length());
	}

	@Test
	public void doesNotWriteTimestampWhenDelegateWriterFails()
			throws IOException {
		// Given
		String documentRead = reader.readDocument();
		doThrow(IOException.class).when(mockTargetWriter).append(documentRead,
				0, documentRead.length());

		// When
		try {
			genericWriter.write(documentRead, 0, documentRead.length());
			fail("Should not have written timestamp!");
		} catch (IOException e) {
			// Then
			assertThat(timestampWriter.toString(), is(""));
		}
	}

	@Test
	public void writesTimestampOfLastDocumentReadToDestination()
			throws IOException {
		// When
		while (reader.hasDocument()) {
			String documentRead = reader.readDocument();
			genericWriter.write(documentRead, 0, documentRead.length());
		}

		// Then
		String expectedTimestamp = ("\"ts\" : { \"$ts\" : 1352094942 ,"
				+ " \"$inc\" : 1}");
		assertThat(timestampWriter.toString(), is(expectedTimestamp));
	}
}
