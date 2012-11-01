package com.ee.beaver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.Iterator;

import org.bson.types.BSONTimestamp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ReadOperationsSpecs {
	@Mock
	private OplogCollection mockOplogCollection;

	@Mock
	private Iterator<OplogDocument> mockOplogCollectionIterator;

	private OplogReader reader;

	@Before
	public void setupOplogReader() {
		given(mockOplogCollection.find()).willReturn(mockOplogCollectionIterator);
		reader = new OplogReader(mockOplogCollection);
	}

	@Test
	public void itReadsCreateCollectionOperation() {
		//Given
		given(mockOplogCollectionIterator.hasNext()).willReturn(true);
		OplogDocument entry = new OplogDocument();
		entry.ts = new BSONTimestamp(123456, 0);
		entry.h = 5881556024799929122L;
		entry.op = "c";
		entry.ns = "ee.$cmd";
		entry.o = new CreateCollection("people");
		given(mockOplogCollectionIterator.next()).willReturn(entry);

		//When
		OplogDocument oplogDocument = reader.readDocument();

		//Then
		Gson gson = new GsonBuilder().serializeNulls().create();
		String expected = gson.toJson(entry).replaceAll("\\\\", "");
		assertThat(oplogDocument.toJson(), is(expected));
	}

	@Test
	public void itDoesNotReadEmptyCollection() {
		//Given
		given(mockOplogCollectionIterator.hasNext()).willReturn(false);

		//When-Then
		assertThat(reader.readDocument(), nullValue());
	}
}
