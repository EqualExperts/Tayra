package com.ee.beaver.command

import java.io.Writer
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

public class BackupSpecs {

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
		def expected = 'error: Missing required options: sf'
		Script backup = new Backup(context)

		//When
		backup.run()

		//Then
		assertThat result.toString(), is(expected)
	}

	@Test
	public void shoutsWhenNoOutputFileIsSupplied() {
		//Given
		def context = new Binding()
		context.setVariable('args', ['-s', 'localhost'])
		def expected = 'error: Missing required option: f'
		Script backup = new Backup(context)

		//When
		backup.run()

		//Then
		assertThat result.toString(), is(expected)
	}

	@Test
	public void shoutsWhenNoSourceMongoDBIsSupplied() {
		//Given
		def context = new Binding()
		context.setVariable('args', ['-f', 'test.out'])
		def expected = 'error: Missing required option: s'
		Script backup = new Backup(context)

		//When
		backup.run()

		//Then
		assertThat result.toString(), is(expected)
	}

	@Test
	public void invokesBackupWhenAllMandatoryOptionsAreSupplied() {
		//Given
		def context = new Binding()
		context.setVariable('args', ['-s', 'localhost', '-f', 'test.out'])
		def result = new StringWriter()
		context.setVariable('writer', result)
		Script backup = new Backup(context)

		//When
		backup.run()

		//Then
		assertThat result.toString(), containsString('"ts"')
	}

	@Test
	public void shoutsWhenMongoDBUrlIsIncorrect() {
		//Given
		def context = new Binding()
		context.setVariable('args', ['-s', 'nonexistentHost', '-f', 'test.out'])
		Script backup = new Backup(context)

		//When
		backup.run()

		//Then
		String expected = "Oops!! Could not perform backup...nonexistentHost"
		assertThat result.toString(), containsString(expected)
	}

	@Test
	public void shoutsWhenSourceMongoDBIsNotAPartOfReplicaSet() {
		//Given
		def context = new Binding()
		context.setVariable('args', ['-s', 'localhost', '-f', 'test.out', '-p', '27020'])
		Script backup = new Backup(context)

		//When
		backup.run()

		//Then
		String expected = 'Oops!! Could not perform backup...localhost is not a part of ReplicaSet'
		assertThat result.toString(), containsString(expected)
	}
}

