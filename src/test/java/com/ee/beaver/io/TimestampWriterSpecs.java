package com.ee.beaver.io;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

@RunWith(MockitoJUnitRunner.class)
public class TimestampWriterSpecs {

	@Mock
	private Writer mockTargetWriter;
	private TimestampWriter timestampWriter;

	@Before
	public void givenThereExists() {
		timestampWriter = new TimestampWriter(mockTargetWriter);
	}

	private String documentOne() {
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
		return entry.toString();
	}

	private String documentTwo() {
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
		return entry.toString();
	}

	@Test
	public void writesTimestamptoDestination() throws IOException {
		// When
		String document = documentOne();
		timestampWriter.write(document, 0, document.length());

		// Then
		String expectedTimestamp = ("\"ts\" : { \"$ts\" : 1352094941 ,"
				+ " \"$inc\" : 1}");
		assertThat(timestampWriter.getTimestamp(), is(expectedTimestamp));
	}

	@Test
	public void delegatesWritesToTargetWriter() throws IOException {
		// When
		String document = documentOne();
		timestampWriter.write(document, 0, document.length());

		// Then
		verify(mockTargetWriter).append(document, 0, document.length());
	}

	@Test
	public void writesTimestampOfLastDocumentReadToDestination()
			throws IOException {
		// Given
		String documentOne = documentOne();
		timestampWriter.write(documentOne, 0, documentOne.length());

		//When
		String documentTwo = documentTwo();
		timestampWriter.write(documentTwo, 0, documentTwo.length());

		// Then
		String expectedTimestamp = ("\"ts\" : { \"$ts\" : 1352094942 ,"
				+ " \"$inc\" : 1}");
		assertThat(timestampWriter.getTimestamp(), is(expectedTimestamp));
	}

	@Test
	public void doesNotWriteTimestampWhenDelegateWriterFails()
			throws IOException {
		// Given
		String documentOne = documentOne();
		timestampWriter.write(documentOne, 0, documentOne.length());
		String lastRecordedTimestamp = timestampWriter.getTimestamp();

		String documentTwo = documentTwo();
		doThrow(new IOException("Disk Full")).when(mockTargetWriter).append(documentTwo,
				0, documentTwo.length());

		// When
		try {
			timestampWriter.write(documentTwo, 0, documentTwo.length());
			fail("Should not have written timestamp!");
		} catch (IOException e) {
			// Then
			assertThat(timestampWriter.getTimestamp(), is(lastRecordedTimestamp));
		}
	}
	
	@Test
	public void writesTimestampOnlyIfDocumentHasTimestampEntry() throws Exception {
		//Given
		String document = new BasicDBObjectBuilder()
							.start().add("name", "test")
							.get()
							.toString();
		
		//When
		timestampWriter.write(document , 0, document.length());

		// Then
		assertThat(timestampWriter.getTimestamp(), is(""));
	}


}
