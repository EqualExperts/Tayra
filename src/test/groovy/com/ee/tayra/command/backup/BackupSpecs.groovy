package com.ee.tayra.command.backup

import spock.lang.*

import com.ee.tayra.connector.Authenticator
import com.ee.tayra.io.CopyListener
import com.ee.tayra.io.ProgressReporter
import com.ee.tayra.io.Reporter
import com.ee.tayra.parameters.EnvironmentProperties
import com.mongodb.MongoClient
import com.mongodb.MongoException
import com.mongodb.ServerAddress

public class BackupSpecs extends Specification {

	private static StringBuilder result
	private CopyListener mockListener
	private Reporter mockReporter
	private Writer mockWriter
	private def context

	def setupSpec() {
		ExpandoMetaClass.enableGlobally()

		PrintWriter.metaClass.println = { String data ->
			result << data
		}
	}

	public void setup() {
		new File('timestamp.out').delete()
		result = new StringBuilder()
		context = new Binding()
		mockListener = Mock(CopyListener)
		mockReporter = Mock(Reporter)
		mockWriter = Mock(Writer)
		context.setVariable('listener', mockListener)
		context.setVariable('reporter', mockReporter)
		context.setVariable('writer', mockWriter)
	}

	def shoutsWhenNoMandatoryArgsAreSupplied() {
		given: 'no arguments'
			context.setVariable('args', [])

		when: 'backup runs'
			new Backup(context).run()

		then: 'error message should be shown as'
			result.toString() == 'error: Missing required option: f'
	}

	def shoutsWhenNoOutputFileIsSupplied() {
		given: 'argument contains -s option only'
			context.setVariable('args', ['-s', EnvironmentProperties.secureSrcNode])

		when: 'backup runs with above args'
			new Backup(context).run()

		then: 'error message should be shown as'
			result.toString() == 'error: Missing required option: f'
	}

	def shoutsWhenMongoDBUrlIsIncorrect() {
		given:'arguments containing non-existent source'
			context.setVariable('args', ['-s', 'nonexistentHost', '-f', EnvironmentProperties.backupFile])

		when: 'backup runs with above args'
			new Backup(context).run()

		then: 'error message should be shown as'
			result.toString().contains('Oops!! Could not perform backup...nonexistentHost')
	}

	def shoutsWhenSourceMongoDBIsNotAPartOfReplicaSet() {
		given: 'localhost not belonging to replica set'
			context.setVariable('args', ['-s', EnvironmentProperties.unsecureStandaloneNode, '-f', EnvironmentProperties.backupFile, "--port=$EnvironmentProperties.unsecureStandalonePort"])

		when: 'backup runs with above args'
			new Backup(context).run()

		then: 'error message should be shown as'
			result.toString().contains('Oops!! Could not perform backup...node is not a part of ReplicaSet')
	}

	def invokesBackupWhenAllEssentialOptionsAreSuppliedForSecuredConnection() {
		given:'arguments contains -s, -f, -u and -p options'
			context.setVariable('args', ['-s', EnvironmentProperties.secureSrcNode, '-f', EnvironmentProperties.backupFile, '-u', EnvironmentProperties.username, '-p', EnvironmentProperties.password])

		and: 'a result captor is injected'
			def writer = new StringWriter()
			context.setVariable('writer', writer)

		when: 'backup runs with above args'
			new Backup(context).run()

		then: 'the output should contain "ts"'
			writer.toString().contains('ts')
	}

	def invokesBackupWhenAllMandatoryOptionsAreSuppliedForUnsecureReplicaSet() {
		given:'arguments contains -s, -f options'
			context.setVariable('args', ['-s', EnvironmentProperties.unsecureSrcNode, '-f', EnvironmentProperties.backupFile, "--port=$EnvironmentProperties.unsecureSrcPort"])

		and: 'a result captor is injected'
			def writer = new StringWriter()
			context.setVariable('writer', writer)
			new File('timestamp.out').delete()

		and: 'any entry is present in the replicaSet for windows'
			forceTheTestToWorkOnWindows()

		when: 'backup runs with above args'
			new Backup(context).run()

		then: 'the output should contain "ts"'
			writer.toString().contains('ts')
	}

	def forceTheTestToWorkOnWindows() {
		ServerAddress server = new ServerAddress(EnvironmentProperties.unsecureSrcNode, EnvironmentProperties.unsecureSrcPort)
		def UnsecuredReplicaset = new MongoClient(server)
		UnsecuredReplicaset.getDB('admin').addUser('admin', 'admin'.toCharArray())
	}

	def shoutsWhenNoUsernameIsGivenForSecuredReplicaSet() {
		given:'arguments contains -s, -f options but not --username'
			context.setVariable('args', ['-s', EnvironmentProperties.secureSrcNode, '-f', EnvironmentProperties.backupFile])

		and: 'have a authenticator that does not authenticate'
			def mockAuthenticator = Mock(Authenticator)
			context.setVariable('authenticator', mockAuthenticator)
			mockAuthenticator.authenticate('', '') >> { throw new MongoException('Username cannot be empty') }

		when: 'backup runs with above args'
			new Backup(context).run()

		then: 'error message should be thrown as'
			result.toString().contains('Username cannot be empty')
	}

	def shoutsWhenIncorrectPasswordIsSupplied() {
		given:'arguments contains -s and -f option'
			context.setVariable('args', ['-s', EnvironmentProperties.secureSrcNode, '-f', EnvironmentProperties.backupFile, '-u', EnvironmentProperties.username, '-p', 'incorrect'])

		and: 'have a authenticator that does not authenticate'
			def mockAuthenticator = Mock(Authenticator)
			context.setVariable('authenticator', mockAuthenticator)
			mockAuthenticator.authenticate(EnvironmentProperties.username, 'incorrect') >> { throw new MongoException('Authentication Failed to localhost') }

		when: 'backup runs with above args'
			new Backup(context).run()

		then: 'error message should be thrown as'
			result.toString().contains('Authentication Failed to localhost')
	}

	def shoutsWhenIncorrectUsernameIsSupplied() {
		given:'arguments contains -s and -f option'
			def context = new Binding()
			context.setVariable('args', ['-s', EnvironmentProperties.secureSrcNode, '-f', EnvironmentProperties.backupFile, '-u', 'incorrect', '-p', EnvironmentProperties.password])

		and: 'have a authenticator that does not authenticate'
			def mockAuthenticator = Mock(Authenticator)
			context.setVariable('authenticator', mockAuthenticator)
			mockAuthenticator.authenticate('incorrect', EnvironmentProperties.password) >> { throw new MongoException('Authentication Failed to localhost') }

		when: 'backup runs with above args'
			new Backup(context).run()

		then: 'error message should be thrown as'
			result.toString().contains('Authentication Failed to localhost')
	}

	def setsDefaultValuesOfOptions() {
		given: 'arguments contain all essential options and not -s, --port, -u, -p'
			context.setVariable('args', ['-f', EnvironmentProperties.backupFile])

		when: 'backup runs'
			new Backup(context).run()

		then: 'following variables get default values'
			def config = context.getVariable('config')
			config.source == 'localhost'
			config.port == 27017
			config.username == ''
			config.password == ''
	}

	def summarizesOnFinishingBackupProcess() {
		given:'arguments contains -s, -f, -u and -p options'
			context.setVariable('args', ['-s', EnvironmentProperties.secureSrcNode, '-f', EnvironmentProperties.backupFile, '-u', EnvironmentProperties.username, '-p', EnvironmentProperties.password])

		and: 'a reporter is injected'
			def mockProgressReporter = Mock(ProgressReporter)
			context.setVariable('reporter', mockProgressReporter)

		when: 'backup runs with above args'
			new Backup(context).run()

		then: 'then reporter summarizes'
			(_) * mockProgressReporter.summarizeTo(_)
	}
	
	def backsUpNoDocumentWhenOnlySExcludeOptionIsGiven() {
		given:'arguments contains -s, -f, -u, -p and --sExclude options'
			context.setVariable('args', ['-s', EnvironmentProperties.secureSrcNode, '-f',EnvironmentProperties.backupFile, '--sExclude', EnvironmentProperties.backupFile,  '-u', EnvironmentProperties.username, '-p', EnvironmentProperties.password])

		and: 'a result captor is injected'
			def writer = new StringWriter()
			context.setVariable('writer', writer)

		when: 'backup runs with above args'
			new Backup(context).run()

		then: 'the output should contain "ts"'
			writer.toString() == ''
	}
	
	def shoutsWhenWrongArgumentsAreSupplied() {
		given:'arguments contains -s, -f valid options and --sNssss a not valid option'
			def context = new Binding()
			context.setVariable('args', ['-s', EnvironmentProperties.unsecureSrcNode, '-f', EnvironmentProperties.backupFile, '--sNssss=users'])
			
		when: 'backup runs with above args'
			new Backup(context).run()
							
		then: 'error message should be thrown as'
			result.toString().contains('Cannot Understand [--sNssss, users]')
	}
	
}
