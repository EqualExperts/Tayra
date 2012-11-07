package com.ee.beaver.runner

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.mockito.Mockito.*
import groovy.mock.interceptor.*

import com.ee.beaver.command.Backup

class RunnerSpecs {
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
	void invokesBackupCommand() {
		//Given
		Binding binding = new Binding()
		def scriptName = 'backup'
		String [] args = new String[1]
		args[0] = scriptName
		binding.setVariable('args', args)
		Script runner = new Runner(binding)

		//When		
		runner.run()
		
		//Then
		def expected = 'error: Missing required options: sf'
		assertThat result.toString(), is(expected)
	}
	
	@Test
	void shoutsWhenAnUnknownCommandIsInvoked() {
		//Given
		Binding binding = new Binding()
		def scriptName = 'unknown'
		String [] args = new String[1]
		args[0] = scriptName
		binding.setVariable('args', args)
		Script runner = new Runner(binding)
		
		//When
		try {
			runner.run()
		} catch (IllegalArgumentException problem) {
			//Then
			assertThat(problem.message, containsString("Don't know how to process: $scriptName"))
		}
	}
	
	@Test
	void invokesRestoreCommand() {
		//Given
		Binding binding = new Binding()
		def scriptName = 'restore'
		String [] args = new String[1]
		args[0] = scriptName
		binding.setVariable('args', args)
		Script runner = new Runner(binding)

		//When
		runner.run()
		
		//Then
		def expected = 'error: Missing required options: df'
		assertThat result.toString(), is(expected)
	}
}
