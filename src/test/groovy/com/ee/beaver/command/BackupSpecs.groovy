package com.ee.beaver.command

import spock.lang.*

import com.mongodb.Mongo
import com.mongodb.MongoException
import com.mongodb.MongoOptions
import com.mongodb.ServerAddress

public class BackupSpecs extends Specification {

	private static StringBuilder result;
	private String username = 'admin'
	private String password = 'admin'

	def setupSpec() {
		ExpandoMetaClass.enableGlobally()

		PrintWriter.metaClass.println = { String data ->
			result << data
		}
	}

	public void setup() {
		new File('timestamp.out').delete()
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
			context.setVariable('args', ['-s', 'localhost', '-f', 'test.out', '--port=27021'])

		when: 'backup runs with above args'
			new Backup(context).run()

		then: 'error message should be shown as'
			result.toString().contains('Oops!! Could not perform backup...node is not a part of ReplicaSet')
	}

	def invokesBackupWhenAllEssentialOptionsAreSuppliedForSecuredConnection() {
		given:'arguments contains -s, -f, -u and -p options'
			def context = new Binding()
			context.setVariable('args', ['-s', 'localhost', '-f', 'test.out', '-u', username, '-p', password])

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
			def context = new Binding()
			context.setVariable('args', ['-s', 'localhost', '-f', 'test.out', '--port=17017'])

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
		def options = new MongoOptions()
		options.safe = true
		ServerAddress server = new ServerAddress("localhost", 17017)
		def UnsecuredReplicaset = new Mongo(server, options);
		UnsecuredReplicaset.getDB('admin').addUser('admin', 'admin'.toCharArray())
	}

	def shoutsWhenNoUsernameIsGivenForSecuredReplicaSet() {
		given:'arguments contains -s, -f options but not --username'
			def context = new Binding()
			context.setVariable('args', ['-s', 'localhost', '-f', 'test.out'])

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
			def context = new Binding()
			context.setVariable('args', ['-s', 'localhost', '-f', 'test.out', '-u', username, '-p', 'incorrect'])

		and: 'have a authenticator that does not authenticate'
			def mockAuthenticator = Mock(Authenticator)
			context.setVariable('authenticator', mockAuthenticator)
			mockAuthenticator.authenticate(username, 'incorrect') >> { throw new MongoException('Authentication Failed to localhost') }

		when: 'backup runs with above args'
			new Backup(context).run()

		then: 'error message should be thrown as'
			result.toString().contains('Authentication Failed to localhost')
	}

	def shoutsWhenIncorrectUsernameIsSupplied() {
		given:'arguments contains -s and -f option'
			def context = new Binding()
			context.setVariable('args', ['-s', 'localhost', '-f', 'test.out', '-u', 'incorrect', '-p', password])

		and: 'have a authenticator that does not authenticate'
			def mockAuthenticator = Mock(Authenticator)
			context.setVariable('authenticator', mockAuthenticator)
			mockAuthenticator.authenticate('incorrect', password) >> { throw new MongoException('Authentication Failed to localhost') }

		when: 'backup runs with above args'
			new Backup(context).run()

		then: 'error message should be thrown as'
			result.toString().contains('Authentication Failed to localhost')
	}
}

