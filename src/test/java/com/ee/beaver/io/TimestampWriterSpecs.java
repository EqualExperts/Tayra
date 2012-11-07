package com.ee.beaver.io;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TimestampWriterSpecs {

	@Mock
	private Writer mockTargetWriter;
	private StringWriter destination;
	private TimestampWriter timestampWriter;
	private char[] data;

	@Before
	public void setup() {
		data = new StringBuilder()
				.append("{ \"ts\" : { \"$ts\" : 1352094941 , \"$inc\" : 1} , ")
				.append("\"h\" : -5637503227244014604 , \"op\" : \"i\" ,")
				.append(" \"ns\" : \"person.things\" , \"o\" : { \"_id\"")
				.append(" : { \"$oid\" : \"509754dd2862862d511f6b57\"} ,")
				.append(" \"name\" : \"xyz\"}}")
				.toString()
				.toCharArray();
		destination = new StringWriter();
		timestampWriter = new TimestampWriter(destination, mockTargetWriter);
	}

	@Test
	public void writesTimestamptoDestination() throws IOException {
		// When
		timestampWriter.write(data, 0, data.length);

		// Then
		String expectedTimestamp = ("\"ts\" : { \"$ts\" : 1352094941 ," +
				" \"$inc\" : 1}");
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
			fail("Should not have written timestamp!");
		} catch (IOException e) {
			// Then
			assertThat(destination.toString(), is(""));
		}
	}
}
