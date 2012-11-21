package com.ee.beaver.command

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.io.Reader
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.mockito.Mockito.*
import com.ee.beaver.io.OplogReplayer
import com.mongodb.BasicDBObject

class RestoreSpecs {

	private static StringBuilder result;
	private static final CharSequence NEW_LINE = System.getProperty("line.separator")

	@BeforeClass
	public static void setupInterceptor() {
		ExpandoMetaClass.enableGlobally()

		PrintWriter.metaClass.println = { String data ->
			result << data
		}
	}

	@Before
	public void setupResultCollector() {
		result = new StringBuilder()
	}

	@Test
	public void shoutsWhenNoMandatoryArgsAreSupplied() {
		//Given
		def context = new Binding()
		context.setVariable('args', [])
		def expected = 'error: Missing required options: df'
		Script restore = new Restore(context)

		//When
		restore.run()

		//Then
		assertThat result.toString(), is(expected)
	}

	@Test
	public void shoutsWhenNoOutputFileIsSupplied() {
		//Given
		def context = new Binding()
		context.setVariable('args', ['-d', 'localhost'])
		def expected = 'error: Missing required option: f'
		Script restore = new Restore(context)

		//When
		restore.run()

		//Then
		assertThat result.toString(), is(expected)
	}

	@Test
	public void shoutsWhenNoDestinationMongoDBIsSupplied() {
		//Given
		def context = new Binding()
		context.setVariable('args', ['-f', 'test.out'])
		def expected = 'error: Missing required option: d'
		Script restore = new Restore(context)

		//When
		restore.run()

		//Then
		assertThat result.toString(), is(expected)
	}

	@Test
	public void invokesRestoreWhenAllMandatoryOptionsAreSupplied() {
		//Given
		def context = new Binding()
		context.setVariable('args', ['-d', 'localhost', '-f', 'test.out'])
		def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
		context.setVariable('reader', source)
		def mockOplogReplayer = mock(OplogReplayer)
		context.setVariable('writer', mockOplogReplayer)
		Script restore = new Restore(context)

		//When
		restore.run()

		//Then
		verify(mockOplogReplayer).replayDocument('"ts"')
	}

	@Test
	public void shoutsWhenMongoDBUrlIsIncorrect() {
		//Given
		def context = new Binding()
		context.setVariable('args', ['-d', 'nonexistentHost', '-f', 'test.out'])
		Script restore = new Restore(context)

		//When
		restore.run()

		//Then
		String expected = "Oops!! Could not perform restore...nonexistentHost"
		assertThat result.toString(), containsString(expected)
	}
}
