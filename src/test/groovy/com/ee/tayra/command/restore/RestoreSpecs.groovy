package com.ee.tayra.command.restore

import spock.lang.*

import com.ee.tayra.io.ConsoleReplayer;
import com.ee.tayra.io.CopyListener;
import com.ee.tayra.io.DeafAndDumbReporter;
import com.ee.tayra.io.Replayer
import com.ee.tayra.io.OplogReplayer
import com.ee.tayra.io.Reporter
import com.ee.tayra.io.SelectiveOplogReplayer
import com.ee.tayra.command.restore.Restore;
import com.ee.tayra.connector.Authenticator;
import com.mongodb.MongoException

class RestoreSpecs extends Specification {

	private static StringBuilder result;
	private static final CharSequence NEW_LINE = System.getProperty("line.separator")
	private Replayer mockReplayer
	private CopyListener mockListener
	private Reporter mockReporter
	private def context

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
		context = new Binding()
		mockReplayer = Mock(Replayer)
		mockListener = Mock(CopyListener)
		mockReporter = Mock(Reporter)
		context.setVariable('writer', mockReplayer)
		context.setVariable('listener', mockListener)
		context.setVariable('reporter', mockReporter)
	}

	def shoutsWhenNoMandatoryArgsAreSupplied() {
		given: 'no arguments are supplied'
			context.setVariable('args', [])

		when: 'restore runs'
			new Restore(context).run()

		then: 'error message should be shown as'
			result.toString() == 'error: Missing required option: f'
	}

	def shoutsWhenInvalidArgsAreSupplied() {
		given: 'Invalid arguments are supplied'
			context.setVariable('args', ['-h', '-i'])

		when: 'restore runs'
			new Restore(context).run()

		then: 'error message should be shown as'
			result.toString() == 'error: Missing required option: f'
	}

	def shoutsWhenNoOutputFileIsSupplied() {
		given: 'argument list does not contain output file option -f'
			context.setVariable('args', ['-d', 'localhost'])

		when: 'restore runs'
			new Restore(context).run()

		then: 'error message should be displayed as'
			result.toString() == 'error: Missing required option: f'
	}

	def invokesRestoreWhenAllEssentialOptionsAreSuppliedForSecuredStandalone() {
		given:'arguments contains -d, -f, -u and -p options'
			context.setVariable('args', ['-d', 'localhost', '--port=27020', '-f', 'test.out', '-u', username, '-p', password])

		and: 'the reader and writer is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)

		when: 'restore runs'
			new Restore(context).run()

		then: 'perform the restore operation'
			1 * mockReplayer.replay('"ts"')
	}

	def invokesRestoreWhenAllEssentialOptionsAreSuppliedForUnsecuredStandalone() {
		given:'arguments contains -d, -port and -f options'
			context.setVariable('args', ['-d', 'localhost', '--port=27021', '-f', 'test.out'])

		and: 'the reader and writer is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)

		when: 'restore runs'
			new Restore(context).run()

		then: 'perform the restore operation'
			1 * mockReplayer.replay('"ts"')
	}

	def shoutsWhenNoUsernameIsGivenForSecuredStandalone() {
		given:'arguments contains -d, -f and --port options but not --username'
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
			context.setVariable('args', ['-d', 'nonexistentHost', '-f', 'test.out'])

		when: 'restore runs'
			new Restore(context).run()

		then:'error message should be displayed as'
			result.toString().contains("Oops!! Could not perform restore...nonexistentHost")
	}

	def invokesRestoreWhenSelectNamespaceOptionIsSupplied() {
		given:'arguments contains -d, --port and -f and --sNs options'
			context.setVariable('args', ['-d', 'localhost', '--port=27021', '-f', 'test.out','--sNs=test'])

		and: 'the reader is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)

		when: 'restore runs'
			new Restore(context).run()

		then: 'it performs the restore operation'
			1 * mockReplayer.replay('"ts"')
	}

	def notifiesListenerOnSuccessfulReadOperation() {
		given:'arguments contain all essential options'
			context.setVariable('args', ['-d', 'localhost', '--port=27020', '-f', 'test.out', '-u', username, '-p', password])

		and: 'the reader is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)

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
			context.setVariable('args', ['-d', 'localhost', '--port=27020', '-f', 'test.out', '-u', username, '-p', password])

		and: 'the reader is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)

		when: 'restore runs'
			new Restore(context).run()

		then: 'it summarizes'
			1 * mockReporter.summarizeTo(_)
	}

	def reportsStartTime() {
		given:'arguments contain all essential options'
			context.setVariable('args', ['-d', 'localhost', '--port=27020', '-f', 'test.out', '-u', username, '-p', password])

		and: 'the reader is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)

		when: 'restore runs'
			new Restore(context).run()

		then: 'it reports start time'
			1 * mockReporter.writeStartTimeTo(_)
	}

	def notifiesListenerOnSuccessfulReadOperationWithDryrun() {
		given:'arguments contains -f and -dry-run options'
			context.setVariable('args', ['-f', 'test.out', '--dry-run'])

		and: 'the reader is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)

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
			context.setVariable('args', ['-f', 'test.out', '--dry-run'])

		and: 'the reader is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)

		when: 'restore runs'
			new Restore(context).run()

		then: 'it performs the restore operation'
			1 * mockReplayer.replay('"ts"')
	}

	def setsDefaultValuesOfOptions() {
		given: 'arguments contain all essential options and not -d, --port, -u, -p'
			context.setVariable('args', ['-f', 'test.out'])
		
		when: 'restore runs'
			new Restore(context).run()
			
		then: 'following variables get default values'
			def config = context.getVariable('config')
			config.destination == 'localhost'
			config.port == 27017
			config.username == ''
			config.password == ''
			config.exceptionFile == 'exception.documents'
	}

	def invokesRestoreWhenTimeStampOptionIsSupplied() {
		given:'arguments contains -f, -u, -p and --sUntil options'
			context.setVariable('args', ['-f', 'test.out','--sUntil={ts:{$ts:1357537752,$inc:2}}', '-u', username, '-p', password])

		and: 'the reader is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)

		when: 'restore runs'
			new Restore(context).run()

		then: 'it performs the restore operation'
			1 * mockReplayer.replay('"ts"')
	}

	def invokesRestoreWhenSExcludeOptionIsGiven() {
		given:'arguments contains -f, -u, -p, --sUntil, --sNs and --sExclude options'
			context.setVariable('args', ['-f', 'test.out','--sExclude','--sNs=test','--sUntil={ts:{$ts:1357537752,$inc:2}}', '-u', username, '-p', password])
			
		and: 'the reader is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)
		
		when: 'restore runs'
			new Restore(context).run()
			
		then: 'it performs the restore operation'
			1 * mockReplayer.replay('"ts"')
	}
	
	def returnsNoDocumentWhenOnlySExcludeOptionIsGiven() {
		given:'arguments contains -f, -u, -p,--sExclude options'
			context.setVariable('args', ['-f', 'test.out','--sExclude', '-u', username, '-p', password])

		and: 'the reader is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)

		when: 'restore runs'
			new Restore(context).run()

		then: 'it performs the restore operation and no document is restored'
			result.toString() == ''
	}
	
	def shoutsWhenWrongArgumentsAreSupplied() {
		given:'arguments contains -d, -f valid options and --sNssss: not valid option'
			def context = new Binding()
			context.setVariable('args', ['-d', 'localhost', '--port=27021','-f', 'test.out', '--sNsss=users'])
			
		when: 'backup runs with above args'
			new Restore(context).run()
				
		then: 'error message should be thrown as'
		println result.toString()
			result.toString().contains('Cannot Understand [--sNsss, users]')
	}
	
}
