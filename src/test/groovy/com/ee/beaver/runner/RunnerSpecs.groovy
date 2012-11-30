package com.ee.beaver.runner

import groovy.mock.interceptor.*
import spock.lang.*

class RunnerSpecs extends Specification {
	
	private static StringBuilder result
	
	def setupSpec() {
		ExpandoMetaClass.enableGlobally()
		
		PrintWriter.metaClass.println = { String data ->
			result << data
		}
	}
	
	def setup() {
		result = new StringBuilder()
	}

	def invokesBackupCommand() {
		given: 'a backup script without required options'
			Binding binding = new Binding()
			def scriptName = 'backup'
			String [] args = new String[1]
			args[0] = scriptName
			binding.setVariable('args', args)
			
		and: 'a script runner'
			Script runner = new Runner(binding)

		when: 'runner runs the script'		
			runner.run()
		
		then: 'error message should be shown as'
			result.toString() == 'error: Missing required options: sf'
	}	
	
	def shoutsWhenAnUnknownCommandIsInvoked() {
		given: 'an unknown script'
			Binding binding = new Binding()
			def scriptName = 'unknown'
			String [] args = new String[1]
			args[0] = scriptName
			binding.setVariable('args', args)
			
		and: 'a script runner'
			Script runner = new Runner(binding)
		
		when: 'runner runs the script'
			runner.run()
			
		then: 'error message should be shown as'
			def problem = thrown(IllegalArgumentException)
			problem.message == "Don't know how to process: $scriptName"
	}
	
	
	def invokesRestoreCommand() {
		given: 'a restore script without requires options'
			Binding binding = new Binding()
			def scriptName = 'restore'
			String [] args = new String[1]
			args[0] = scriptName
			binding.setVariable('args', args)
			
		and: 'a script runner'
			Script runner = new Runner(binding)

		when: 'runner runs the script'
			runner.run()
		
		then: 'error message should be shown as'
			result.toString() == 'error: Missing required options: df'
	}
}
