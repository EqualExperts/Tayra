package com.ee.tayra.command

import spock.lang.*

import com.ee.tayra.io.ConsoleReplayer;
import com.ee.tayra.io.CopyListener;
import com.ee.tayra.io.EmptyProgressReporter;
import com.ee.tayra.io.Replayer
import com.ee.tayra.io.OplogReplayer
import com.ee.tayra.io.Reporter
import com.ee.tayra.io.SelectiveOplogReplayer
import com.ee.tayra.command.Authenticator;
import com.ee.tayra.command.Restore;
import com.mongodb.MongoException

class RestoreSpecs extends Specification {

	private static StringBuilder result;
	private static final CharSequence NEW_LINE = System.getProperty("line.separator")
	private Replayer mockReplayer
	private String username = 'admin'
	private String password = 'admin'

	def setupSpec() {
		ExpandoMetaClass.enableGlobally()

		PrintWriter.metaClass.println = { String data ->
			result << data
		}
	}

	def setup() {
		result = new StringBuilder()
		mockReplayer = Mock(Replayer)
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

	def invokesRestoreWhenAllEssentialOptionsAreSuppliedForSecuredStandalone() {
		given:'arguments contains -d, -f, -u and -p options'
			def context = new Binding()
			context.setVariable('args', ['-d', 'localhost', '--port=27020', '-f', 'test.out', '-u', username, '-p', password])

		and: 'the reader and writer is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)
			context.setVariable('writer', mockReplayer)

		when: 'restore runs'
			new Restore(context).run()

		then: 'perform the restore operation'
			1 * mockReplayer.replay('"ts"')
	}

	def invokesRestoreWhenAllEssentialOptionsAreSuppliedForUnsecuredStandalone() {
		given:'arguments contains -d, -port and -f options'
			def context = new Binding()
			context.setVariable('args', ['-d', 'localhost', '--port=27021', '-f', 'test.out'])

		and: 'the reader and writer is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)
			context.setVariable('writer', mockReplayer)

		when: 'restore runs'
			new Restore(context).run()

		then: 'perform the restore operation'
			1 * mockReplayer.replay('"ts"')
	}

	def shoutsWhenNoUsernameIsGivenForSecuredStandalone() {
		given:'arguments contains -d, -f and --port options but not --username'
			def context = new Binding()
			context.setVariable('args', ['-d', 'localhost', '--port=27020', '-f', 'test.out'])

		and: 'have a authenticator that does not authenticate'
			def mockAuthenticator = Mock(Authenticator)
			context.setVariable('authenticator', mockAuthenticator)
			mockAuthenticator.authenticate('', '') >> { throw new MongoException('Username cannot be empty') }

		when: 'backup runs with above args'
			new Restore(context).run()

		then: 'error message should be thrown as'
			result.toString().contains('Username cannot be empty')
	}

	def shoutsWhenIncorrectUsernameIsSupplied() {
		given:'arguments contains -d, --port, -f, -u and -p option'
			def context = new Binding()
			context.setVariable('args', ['-d', 'localhost', '--port=27020', '-f', 'test.out', '-u', 'incorrect', '-p', password])

		and: 'have a authenticator that does not authenticate'
			def mockAuthenticator = Mock(Authenticator)
			context.setVariable('authenticator', mockAuthenticator)
			mockAuthenticator.authenticate('incorrect', password) >> { throw new MongoException('Authentication Failed to localhost') }

		when: 'backup runs with above args'
			new Restore(context).run()

		then: 'error message should be thrown as'
			result.toString().contains('Authentication Failed to localhost')
	}

	def shoutsWhenIncorrectPasswordIsSupplied() {
		given:'arguments contains -d, --port, -f and -u option'
			def context = new Binding()
			context.setVariable('args', ['-d', 'localhost', '--port=27020', '-f', 'test.out', '-u', username, '-p', 'incorrect'])

		and: 'have a authenticator that does not authenticate'
			def mockAuthenticator = Mock(Authenticator)
			context.setVariable('authenticator', mockAuthenticator)
			mockAuthenticator.authenticate(username, 'incorrect') >> { throw new MongoException('Authentication Failed to localhost') }

		when: 'backup runs with above args'
			new Restore(context).run()

		then: 'error message should be thrown as'
			result.toString().contains('Authentication Failed to localhost')
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

	def invokesRestoreWhenSelectDbOptionIsSupplied() {
		given:'arguments contains -d, --port and -f and --sDb options'
			def context = new Binding()
			context.setVariable('args', ['-d', 'localhost', '--port=27021', '-f', 'test.out','--sDb="test"'])

		and: 'the reader and writer is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)
			context.setVariable('writer', mockReplayer)

		when: 'restore runs'
			new Restore(context).run()

		then: 'perform the restore operation'
			1 * mockReplayer.replay('"ts"')
	}


	def notifiesListenerOnSuccessfulReadOperation() {
		given:'arguments contain all essential options'
			def context = new Binding()
			context.setVariable('args', ['-d', 'localhost', '--port=27020', '-f', 'test.out', '-u', username, '-p', password])

		and: 'the reader and writer is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)
			context.setVariable('writer', mockReplayer)

		and: 'an listener is injected'
			CopyListener mockListener = Mock(CopyListener)
			context.setVariable('listener', mockListener)

		when: 'restore runs'
			new Restore(context).run()

		then: 'invokes Read Success on listener'
			1 * mockListener.onReadSuccess('"ts"')
			0 * mockListener.onWriteSuccess('"ts"')
			0 * mockListener.onReadFailure('"ts"', _)
			0 * mockListener.onWriteFailure('"ts"', _)
	}


	def reportsSummary() {
		given:'arguments contain all essential options'
			def context = new Binding()
			context.setVariable('args', ['-d', 'localhost', '--port=27020', '-f', 'test.out', '-u', username, '-p', password])

		and: 'the reader and writer is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)
			context.setVariable('writer', mockReplayer)

		and: 'an empty reporter is injected'
			Reporter mockReporter = Mock(Reporter)
			context.setVariable('reporter', mockReporter)

		when: 'restore runs'
			new Restore(context).run()

		then: 'it summarizes'
			1 * mockReporter.summarizeTo(_)
	}


	def reportsStartTime() {
		given:'arguments contain all essential options'
			def context = new Binding()
			context.setVariable('args', ['-d', 'localhost', '--port=27020', '-f', 'test.out', '-u', username, '-p', password])

		and: 'the reader and writer is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)
			context.setVariable('writer', mockReplayer)

		and: 'an empty reporter is injected'
			Reporter mockReporter = Mock(Reporter)
			context.setVariable('reporter', mockReporter)

		when: 'restore runs'
			new Restore(context).run()

		then: 'it reports start time'
			1 * mockReporter.writeStartTimeTo(_)
	}


	def notifiesListenerOnSuccessfulReadOperationWithDryrun() {
		given:'arguments contains -f and -dry-run options'
			def context = new Binding()
			context.setVariable('args', ['-f', 'test.out', '--dry-run'])

		and: 'the reader and writer is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)
			context.setVariable('writer', mockReplayer)

		and: 'an empty listener is injected'
			CopyListener mockEmptyListener = Mock(CopyListener)
			context.setVariable('listener', mockEmptyListener)

		when: 'restore runs'
			new Restore(context).run()

		then: 'invokes Read Success on listener'
			1 * mockEmptyListener.onReadSuccess('"ts"')
			0 * mockEmptyListener.onWriteSuccess('"ts"')
			0 * mockEmptyListener.onReadFailure('"ts"', _)
			0 * mockEmptyListener.onWriteFailure('"ts"', _)
	}


	def ignoresMandatoryDestinationOptionWhenDryRunOptionIsGiven() {
		given:'arguments contains -f and --dry-run options'
			def context = new Binding()
			context.setVariable('args', ['-f', 'test.out', '--dry-run'])

		and: 'the reader and writer is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)
			mockReplayer = Mock(Replayer)
			context.setVariable('writer', mockReplayer)

		when: 'restore runs'
			new Restore(context).run()

		then: 'it performs the restore operation'
			1 * mockReplayer.replay('"ts"')
	}

}
