package com.ee.beaver.command

import java.io.Writer
import spock.lang.*

public class BackupSpecs extends Specification {

	private static StringBuilder result;

	def setupSpec() {
		ExpandoMetaClass.enableGlobally()

		PrintWriter.metaClass.println = { String data ->
			result << data
		}
	}

	public void setup() {
		result = new StringBuilder()
	}

	def shoutsWhenNoMandatoryArgsAreSupplied() {
		given: 'that script does not run with any options'
			def context = new Binding()
			context.setVariable('args', [])
			Script backup = new Backup(context)

		when: 'it runs'
			backup.run()

		then: 'error message should be shown as'
			result.toString() == 'error: Missing required options: sf'
	}
	
	def shoutsWhenNoOutputFileIsSupplied() {
		given: 'that script executes with -s option only'
		def context = new Binding()
		context.setVariable('args', ['-s', 'localhost'])
		Script backup = new Backup(context)

		when: 'it runs'
			backup.run()

			
		then: 'error message should be shown as'
			result.toString() == 'error: Missing required option: f'
	}

	def shoutsWhenNoSourceMongoDBIsSupplied() {
		given: 'that script executes with -f option only'
			def context = new Binding()
			context.setVariable('args', ['-f', 'test.out'])
			Script backup = new Backup(context)

		when: 'it runs'
			backup.run()

		then: 'error message should be shown as'
			result.toString() == 'error: Missing required option: s'
	}

	def invokesBackupWhenAllMandatoryOptionsAreSupplied() {
		given:'that script executes with -s and -f option'
			def context = new Binding()
			context.setVariable('args', ['-s', 'localhost', '-f', 'test.out'])
			def result = new StringWriter()
			context.setVariable('writer', result)
			Script backup = new Backup(context)

		when:'it runs'
			backup.run()

		then: 'the output should contain "ts"'
			result.toString().contains('ts')
	}

	def shoutsWhenMongoDBUrlIsIncorrect() {
		given:'that script executes with non-existent source'
			def context = new Binding()
			context.setVariable('args', ['-s', 'nonexistentHost', '-f', 'test.out'])
			Script backup = new Backup(context)

		when: 'it runs'
			backup.run()

		then: 'error message should be shown as'
			result.toString().contains('Oops!! Could not perform backup...nonexistentHost')
	}

	def shoutsWhenSourceMongoDBIsNotAPartOfReplicaSet() {
		given: 'that script is run on a node that does not belong to rep set'
			def context = new Binding()
			context.setVariable('args', ['-s', 'localhost', '-f', 'test.out', '-p', '27020'])
			Script backup = new Backup(context)

		when: 'it runs'
			backup.run()

		then: 'error message should be shown as'
			result.toString().contains('Oops!! Could not perform backup...localhost is not a part of ReplicaSet')
	}
}

