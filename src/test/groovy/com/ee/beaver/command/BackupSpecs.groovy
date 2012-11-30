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
		given: 'no arguments'
			def context = new Binding()
			context.setVariable('args', [])

		when: 'backup runs'
			new Backup(context).run()

		then: 'error message should be shown as'
			result.toString() == 'error: Missing required options: sf'
	}

	def shoutsWhenNoOutputFileIsSupplied() {
		given: 'argument contains -s option only'
			def context = new Binding()
			context.setVariable('args', ['-s', 'localhost'])

		when: 'backup runs with above args'
			new Backup(context).run()


		then: 'error message should be shown as'
			result.toString() == 'error: Missing required option: f'
	}

	def shoutsWhenNoSourceMongoDBIsSupplied() {
		given: 'argument contains -f option only'
			def context = new Binding()
			context.setVariable('args', ['-f', 'test.out'])

		when: 'backup runs with above args'
			new Backup(context).run()

		then: 'error message should be shown as'
			result.toString() == 'error: Missing required option: s'
	}

	def invokesBackupWhenAllMandatoryOptionsAreSupplied() {
		given:'arguments contains -s and -f option'
			def context = new Binding()
			context.setVariable('args', ['-s', 'localhost', '-f', 'test.out'])

		and: 'a result captor is injected'
			def result = new StringWriter()
			context.setVariable('writer', result)

		and: 'timestampfile does not exist'
			new File('timestamp.out').delete()

		when: 'backup runs with above args'
			new Backup(context).run()

		then: 'the output should contain "ts"'
			result.toString().contains('ts')
	}

	def shoutsWhenMongoDBUrlIsIncorrect() {
		given:'arguments containing non-existent source'
			def context = new Binding()
			context.setVariable('args', ['-s', 'nonexistentHost', '-f', 'test.out'])

		when: 'backup runs with above args'
			new Backup(context).run()

		then: 'error message should be shown as'
			result.toString().contains('Oops!! Could not perform backup...nonexistentHost')
	}

	def shoutsWhenSourceMongoDBIsNotAPartOfReplicaSet() {
		given: 'localhost not belonging to replica set'
			def context = new Binding()
			context.setVariable('args', ['-s', 'localhost', '-f', 'test.out', '-p', '27020'])

		when: 'backup runs with above args'
			new Backup(context).run()

		then: 'error message should be shown as'
			result.toString().contains('Oops!! Could not perform backup...localhost is not a part of ReplicaSet')
	}

}

