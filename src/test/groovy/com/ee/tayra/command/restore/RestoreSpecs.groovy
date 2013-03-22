package com.ee.tayra.command.restore

import spock.lang.*

import com.ee.tayra.command.backup.Backup;
import com.ee.tayra.connector.Authenticator
import com.ee.tayra.io.CopyListener
import com.ee.tayra.io.Replayer
import com.ee.tayra.io.Reporter
import com.mongodb.MongoException
import static com.ee.tayra.support.Resources.*

class RestoreSpecs extends Specification {

	private static StringBuilder result;
	private static final CharSequence NEW_LINE = System.getProperty("line.separator")
	private Replayer mockReplayer
	private CopyListener mockListener
	private Reporter mockReporter
	private def context

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
			context.setVariable('args', ['-d', secureSrcNode])

		when: 'restore runs'
			new Restore(context).run()

		then: 'error message should be displayed as'
			result.toString() == 'error: Missing required option: f'
	}

	def invokesRestoreWhenAllEssentialOptionsAreSuppliedForSecuredStandalone() {
		given:'arguments contains -d, -f, -u and -p options'
			context.setVariable('args', ['-d', secureStandaloneNode, "--port=$secureStandalonePort", '-f', backupFile, '-u', username, '-p', password])

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
			context.setVariable('args', ['-d', unsecureStandaloneNode, "--port=$unsecureStandalonePort", '-f', backupFile])

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
			context.setVariable('args', ['-d', secureStandaloneNode, "--port=$secureStandalonePort", '-f', backupFile])

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
			context.setVariable('args', ['-d', secureStandaloneNode, "--port=$secureStandalonePort", '-f', backupFile, '-u', 'incorrect', '-p', password])

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
			context.setVariable('args', ['-d', secureStandaloneNode, "--port=$secureStandalonePort", '-f', backupFile, '-u', username, '-p', 'incorrect'])

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
			context.setVariable('args', ['-d', 'nonexistentHost', '-f', backupFile])

		when: 'restore runs'
			new Restore(context).run()

		then:'error message should be displayed as'
			result.toString().contains("Oops!! Could not perform restore...nonexistentHost")
	}

	def invokesRestoreWhenSelectNamespaceOptionIsSupplied() {
		given:'arguments contains -d, --port and -f and --sNs options'
			context.setVariable('args', ['-d', unsecureStandaloneNode, "--port=$unsecureStandalonePort", '-f', backupFile, '--sNs=test'])

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
			context.setVariable('args', ['-d', secureStandaloneNode, "--port=$secureStandalonePort", '-f', backupFile, '-u', username, '-p', password])

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
			context.setVariable('args', ['-d', secureStandaloneNode, "--port=$secureStandalonePort", '-f', backupFile, '-u', username, '-p', password])

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
			context.setVariable('args', ['-d', secureStandaloneNode, "--port=$secureStandalonePort", '-f', backupFile, '-u', username, '-p', password])

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
			context.setVariable('args', ['-f', backupFile, '--dry-run'])

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
			context.setVariable('args', ['-f', backupFile, '--dry-run'])

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
			context.setVariable('args', ['-f', backupFile])
		
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
			context.setVariable('args', ['-f', backupFile,'--sUntil={ts:{$ts:1357537752,$inc:2}}', '-u', username, '-p', password])

		and: 'the reader is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)

		when: 'restore runs'
			new Restore(context).run()

		then: 'it performs the restore operation'
			1 * mockReplayer.replay('"ts"')
	}

	def invokesRestoreWhenTimestampForSinceIsSupplied() {
		given:'arguments contains -f, -u, -p and --sSince options'
			context.setVariable('args', ['-f', 'test.out','--sSince={ts:{$ts:1357537752,$inc:2}}', '-u', username, '-p', password])

		and: 'the reader is injected'
			def source = new BufferedReader(new StringReader('"ts"' + NEW_LINE))
			context.setVariable('reader', source)

		when: 'restore runs'
			new Restore(context).run()

		then: 'it performs the restore operation'
			1 * mockReplayer.replay('"ts"')
	}

	def invokesRestoreWhenTimestampForSinceIsSupplied() {
		given:'arguments contains -f, -u, -p and --sSince options'
			context.setVariable('args', ['-f', 'test.out','--sSince={ts:{$ts:1357537752,$inc:2}}', '-u', username, '-p', password])

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
			context.setVariable('args', ['-f', backupFile,'--sExclude','--sNs=test','--sUntil={ts:{$ts:1357537752,$inc:2}}', '-u', username, '-p', password])
			
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
			context.setVariable('args', ['-f', backupFile,'--sExclude', '-u', username, '-p', password])

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
			context.setVariable('args', ['-d', unsecureStandaloneNode, "--port=$unsecureStandalonePort", '-f', backupFile, '--sNsss=users'])
			
		when: 'backup runs with above args'
			new Restore(context).run()
				
		then: 'error message should be thrown as'
		println result.toString()
			result.toString().contains('Cannot Understand [--sNsss, users]')
	}
	
}
