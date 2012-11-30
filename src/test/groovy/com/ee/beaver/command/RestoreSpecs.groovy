package com.ee.beaver.command

import spock.lang.*

import com.ee.beaver.io.OplogReplayer

class RestoreSpecs extends Specification {

	private static StringBuilder result;
	private static final CharSequence NEW_LINE = System.getProperty("line.separator")
	private OplogReplayer mockOplogReplayer

	def setupSpec() {
		ExpandoMetaClass.enableGlobally()

		PrintWriter.metaClass.println = { String data ->
			result << data
		}
	}

	def setup() {
		result = new StringBuilder()
		mockOplogReplayer = Mock(OplogReplayer)
	}

	def shoutsWhenNoMandatoryArgsAreSupplied() {
		given: 'no arguments are supplied'
			def context = new Binding()
			context.setVariable('args', [])

		when: 'restore runs'
			new Restore(context).run()

		then: 'error message should be shown as'
			result.toString() == 'error: Missing required options: df'
	}

	def shoutsWhenInvalidArgsAreSupplied() {
		given: 'Invalid arguments are supplied'
			def context = new Binding()
			context.setVariable('args', ['-h', '-i'])

		when: 'restore runs'
			new Restore(context).run()

		then: 'error message should be shown as'
			result.toString() == 'error: Missing required options: df'
	}

	def shoutsWhenNoOutputFileIsSupplied() {
		given: 'argument list does not contain output file option -f'
			def context = new Binding()
			context.setVariable('args', ['-d', 'localhost'])

		when: 'restore runs'
			new Restore(context).run()

		then: 'error message should be displayed as'
			result.toString() == 'error: Missing required option: f'
	}

	def shoutsWhenNoDestinationMongoDBIsSupplied() {
		given: 'argument list does not contain the destination option -d'
			def context = new Binding()
			context.setVariable('args', ['-f', 'test.out'])

		when: 'restore runs'
			new Restore(context).run()

		then: 'error message should be displayed as'
			result.toString() == 'error: Missing required option: d'
	}

	def invokesRestoreWhenAllMandatoryOptionsAreSupplied() {
		given: 'argument list has both the necessary arguments -d and -f'
			def context = new Binding()
			context.setVariable('args', ['-d', 'localhost', '-f', 'test.out'])

		and: 'the reader and writer is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)
			context.setVariable('writer', mockOplogReplayer)

		when: 'restore runs'
			new Restore(context).run()

		then: 'perform the restore operation'
			1 * mockOplogReplayer.replayDocument('"ts"')
	}

	def shoutsWhenMongoDBUrlIsIncorrect() {
		given: 'the destination host is incorrect or does not exist'
			def context = new Binding()
			context.setVariable('args', ['-d', 'nonexistentHost', '-f', 'test.out'])

		when: 'restore runs'
			new Restore(context).run()

		then:'error message should be displayed as'
			result.toString().contains("Oops!! Could not perform restore...nonexistentHost")
	}


}
