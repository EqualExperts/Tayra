package com.ee.beaver.command

import spock.lang.*

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
		result = new StringBuilder()
	}

	public void cleanup() {
		new File('timestamp.out').delete()
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
			context.setVariable('args', ['-s', '192.168.3.106', '-f', 'test.out', '--port=27017'])
		
		and: 'a result captor is injected'
			def writer = new StringWriter()
			context.setVariable('writer', writer)
		
		when: 'backup runs with above args'
			new Backup(context).run()
		
		then: 'the output should contain "ts"'
			writer.toString().contains('ts')
	}
		
	def shoutsWhenNoCredentialsAreGivenForSecuredReplicaSet() {
		given:'arguments contains -s, -f options but not -u, -p'
			def context = new Binding()
			context.setVariable('args', ['-s', 'localhost', '-f', 'test.out'])
			
		and: 'timestampfile does not exist'
			new File('timestamp.out').delete()
			
		when: 'backup runs with above args'
			new Backup(context).run()
		
		then: 'error message should be thrown as'
			result.toString().contains('Required correct username or password')
	}
	
	def shoutsWhenIncorrectPasswordIsSupplied() {
		given:'arguments contains -s and -f option'
			def context = new Binding()
			context.setVariable('args', ['-s', 'localhost', '-f', 'test.out', '-u', username, '-p', 'incorrect'])
	
		when: 'backup runs with above args'
			new Backup(context).run()
	
		then: 'error message should be thrown as'
			result.toString().contains('Authentication Failed to localhost')
	}
	
	def shoutsWhenIncorrectUsernameIsSupplied() {
		given:'arguments contains -s and -f option'
			def context = new Binding()
			context.setVariable('args', ['-s', 'localhost', '-f', 'test.out', '-u', 'incorrect', '-p', password])
	
		when: 'backup runs with above args'
			new Backup(context).run()
	
		then: 'error message should be thrown as'
			result.toString().contains('Authentication Failed to localhost')
	}
	
//	def promptsForPasswordWhenNotGiven() {
//		given:'arguments contains -s, -f, -u options but not -p'
//			def context = new Binding()
//			context.setVariable('args', ['-s', 'localhost', '-f', 'test.out', '-u', username])
//	
//		and: 'a result captor is injected'
//			def result = new StringWriter()
//			context.setVariable('writer', result)
//	
//		and: 'timestampfile does not exist'
//			new File('timestamp.out').delete()
//	
//		when: 'backup runs with above args'
//			new Backup(context).run()
//	
//		then: 'it should prompt for password'
//			println "Result: " + result.toString()
//	}
	
}

