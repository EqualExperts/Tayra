package com.ee.beaver.io;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TimestampWriterSpecs {

	@Mock
	private Writer mockTargetWriter;
	private Writer destination;
	private TimestampWriter timestampWriter;
	private char[] data;

	@Before
	public void setup() {
		data = new String("{ \"ts\" : { \"$ts\" : 1352094941 , \"$inc\" : 1} , \"h\" : -5637503227244014604 , \"op\" : \"i\" , \"ns\" : \"person.things\" , \"o\" : { \"_id\" : { \"$oid\" : \"509754dd2862862d511f6b57\"} , \"name\" : \"xyz\"}}").toCharArray();
		destination = new StringWriter();
		timestampWriter = new TimestampWriter(destination, mockTargetWriter);
	}

	@Test
	public void writesTimestamptoDestination() throws IOException {
		//When
		timestampWriter.write(data, 0, data.length);

		//Then
		String expectedTimestamp = ("\"ts\" : { \"$ts\" : 1352094941 , \"$inc\" : 1}\"");
		System.out.println("dest" + destination.toString());
		assertThat(destination.toString(), is(expectedTimestamp));
	}

	@Test
	public void delegatesWritesToTargetWriter() throws IOException {
		// When
		timestampWriter.write(data, 0, data.length);

		// Then
		verify(mockTargetWriter).write(data, 0, data.length);
	}

	@Test
	public void doesNotWriteToDestinationWhenDelegateWriterFails()
			throws IOException {
		// Given
		doThrow(IOException.class).when(mockTargetWriter).write(data, 0,
				data.length);

		// When
		try {
			timestampWriter.write(data, 0, data.length);
		} catch (IOException e) {
			// Then
			assertThat(destination.toString(), is(""));
		}
	}

}
