package com.ee.beaver.command

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.Reader
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.mockito.Mockito.*
import com.ee.beaver.io.OplogWriter

class RestoreSpecs {

	private static StringBuilder result;
	
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
		def result = new StringReader('"ts"')
		context.setVariable('reader', result)
		def mockOplogWriter = mock(OplogWriter)
		context.setVariable('writer', mockOplogWriter)
		Script restore = new Restore(context)
		
		//When
		restore.run()
		
		//Then
		verify(mockOplogWriter).writeDocument('"ts"')
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
		assertThat result.toString(), is(expected)
	}
	
	@Test
	public void shoutsWhenDestinationMongoDBIsNotAPartOfReplicaSet() {
		//Given
		def context = new Binding()
		context.setVariable('args', ['-d', 'localhost', '-f', 'test.out', '-p', '27020'])
		Script restore = new Restore(context)
		
		//When
		restore.run()
		
		//Then
		String expected = 'Oops!! Could not perform restore...localhost is not a part of ReplicaSet'
		assertThat result.toString(), is(expected)
	}
}
