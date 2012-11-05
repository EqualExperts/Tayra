package com.ee.beaver.command

import java.io.Writer;

import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassRegistry;
import groovy.lang.ProxyMetaClass;
import groovy.lang.Interceptor;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ee.beaver.io.OplogReader;

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

public class BackupSpecs {
	private static StringBuilder result;
	
	@BeforeClass
	public static void inject() {
		ExpandoMetaClass.enableGlobally()
		
		PrintWriter.metaClass.println = { String data ->
			result << data
		}
	}
	
	@Before
	public void setupResult() {
		result = new StringBuilder()
	}
	
	@Test
	public void invokesErrorInfoWhenNoArgsAreSupplied() {
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
	public void invokesErrorInfoWhenNoOutputFileIsSupplied() {
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
	public void invokesErrorInfoWhenNoSourceMongoDBIsSupplied() {
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
		Script backup = new Backup(context)
		
		com.ee.beaver.io.Copier.metaClass.copy = { fromReader, toWriter ->
			//Then
			assertThat toWriter, isA(FileWriter.class)
		}
		
		//When
		backup.run()
		
	}
	
	@Test
	public void detectsProblemWhenMongoDBUrlIsIncorrect() {
		//Given
		def context = new Binding()
		context.setVariable('args', ['-s', 'nonexistentHost', '-f', 'test.out'])
		Script backup = new Backup(context)
		
		//When
		backup.run()
		String expected = "Oops!! Could not perform backup...nonexistentHost"
		assertThat result.toString(), is(expected)
	}
}
