package com.ee.tayra.command.backup

import com.ee.tayra.utils.StringDocumentWriter
import spock.lang.*

import com.ee.tayra.connector.Authenticator
import com.ee.tayra.io.listener.CopyListener;
import com.ee.tayra.io.listener.ProgressReporter;
import com.ee.tayra.io.listener.Reporter;
import com.ee.tayra.io.writer.DocumentWriter;

import static com.ee.tayra.ConnectionFactory.*
import com.mongodb.MongoClient
import com.mongodb.MongoException
import com.mongodb.ServerAddress

public class BackupSpecs extends Specification {
  private static String backupFile = 'test.out'
  private static StringBuilder result
  private CopyListener mockListener
  private Reporter mockReporter
  private DocumentWriter mockDocumentWriter
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
    mockDocumentWriter = Mock(DocumentWriter)
    context.setVariable('listener', mockListener)
    context.setVariable('reporter', mockReporter)
    context.setVariable('writer', mockDocumentWriter)
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
      context.setVariable('args', ['-s', secureSrcNode])

    when: 'backup runs with above args'
      new Backup(context).run()

    then: 'error message should be shown as'
      result.toString() == 'error: Missing required option: f'
  }

  def shoutsWhenMongoDBUrlIsIncorrect() {
    given:'arguments containing non-existent source'
      context.setVariable('args', ['-s', 'nonexistentHost', '-f', backupFile])

    when: 'backup runs with above args'
      new Backup(context).run()

    then: 'error message should be shown as'
      result.toString().contains('Oops!! Could not perform backup...nonexistentHost')
  }

  def shoutsWhenSourceMongoDBIsNotAPartOfReplicaSet() {
    given: 'localhost not belonging to replica set'
      context.setVariable('args', ['-s', unsecureTgtNode, '-f', backupFile, "--port=$unsecureTgtPort"])

    when: 'backup runs with above args'
      new Backup(context).run()

    then: 'error message should be shown as'
      result.toString().contains('Oops!! Could not perform backup...node is not a part of ReplicaSet')
  }

  def invokesBackupWhenAllEssentialOptionsAreSuppliedForSecureReplicaSet() {
    given:'arguments contains -s, -f, -u and -p options'
      context.setVariable('args', ['-s', secureSrcNode, "--port=$secureSrcPort", '-f', backupFile, '-u', username, '-p', password])

    and: 'a result captor is injected'
      def writer = new StringDocumentWriter()
      context.setVariable('writer', writer)

    when: 'backup runs with above args'
      new Backup(context).run()

    then: 'the output should contain "ts"'
      writer.toString().contains('ts')
  }

  def invokesBackupWhenAllMandatoryOptionsAreSuppliedForUnsecureReplicaSet() {
    given:'arguments contains -s, -f options'
      context.setVariable('args', ['-s', unsecureSrcNode, '-f', backupFile, "--port=$unsecureSrcPort"])

    and: 'a result captor is injected'
      def writer = new StringDocumentWriter()
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
    ServerAddress server = new ServerAddress(unsecureSrcNode, unsecureSrcPort)
    def UnsecuredReplicaset = new MongoClient(server)
    UnsecuredReplicaset.getDB('admin').addUser('admin', 'admin'.toCharArray())
  }

  def shoutsWhenNoUsernameIsGivenForSecuredReplicaSet() {
    given:'arguments contains -s, -f options but not --username'
      context.setVariable('args', ['-s', secureSrcNode, "--port=$secureSrcPort", '-f', backupFile])

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
      context.setVariable('args', ['-s', secureSrcNode, "--port=$secureSrcPort", '-f', backupFile, '-u', username, '-p', 'incorrect'])

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
      context.setVariable('args', ['-s', secureSrcNode, "--port=$secureSrcPort", '-f', backupFile, '-u', 'incorrect', '-p', password])

    and: 'have a authenticator that does not authenticate'
      def mockAuthenticator = Mock(Authenticator)
      context.setVariable('authenticator', mockAuthenticator)
      mockAuthenticator.authenticate('incorrect', password) >> { throw new MongoException('Authentication Failed to localhost') }

    when: 'backup runs with above args'
      new Backup(context).run()

    then: 'error message should be thrown as'
      result.toString().contains('Authentication Failed to localhost')
  }

  def setsDefaultValuesOfOptions() {
    given: 'arguments contain all essential options and not -s, --port, -u, -p'
      context.setVariable('args', ["--port=$secureSrcPort", '-f', backupFile])

    when: 'backup runs'
      new Backup(context).run()

    then: 'following variables get default values'
      def config = context.getVariable('config')
      config.source == 'localhost'
      config.username == ''
      config.password == ''
  }

  def summarizesOnFinishingBackupProcess() {
    given:'arguments contains -s, -f, -u and -p options'
      context.setVariable('args', ['-s', secureSrcNode, "--port=$secureSrcPort", '-f', backupFile, '-u', username, '-p', password])

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
      context.setVariable('args', ['-s', secureSrcNode, "--port=$secureSrcPort", '-f',backupFile, '--sExclude', backupFile,  '-u', username, '-p', password])

    and: 'a result captor is injected'
      def writer = new StringDocumentWriter()
      context.setVariable('writer', writer)

    when: 'backup runs with above args'
      new Backup(context).run()

    then: 'the output should contain "ts"'
      writer.toString() == ''
  }

  def shoutsWhenWrongArgumentsAreSupplied() {
    given:'arguments contains -s, -f valid options and --sNssss a not valid option'
      def context = new Binding()
      context.setVariable('args', ['-s', unsecureSrcNode, "--port=$unsecureSrcPort", '-f', backupFile, '--sNssss=users'])

    when: 'backup runs with above args'
      new Backup(context).run()

    then: 'error message should be thrown as'
      result.toString().contains('Cannot understand [--sNssss, users]')
  }

  def shoutsWhenNoPasswordIsGivenForSecuredReplicaSet() {
    given:'arguments contains -s, -f, -u and -p options'
      context.setVariable('args', ['-s', secureSrcNode, "--port=$secureSrcPort", '-f', backupFile, '-u', username])

    when: 'backup runs with above args'
      new Backup(context).run()

    then: 'error message should be thrown as'
      result.toString().contains('Cannot Read Password Input, please use -p command line option')
  }

  def itCreatesTimestampFile() {
    given:'arguments contains -s, -f and --port'
      def context = new Binding()
      context.setVariable('args', ['-s', unsecureSrcNode, "--port=$unsecureSrcPort", '-f', backupFile])

    when: 'backup runs with above args'
      new Backup(context).run()

    then: 'timestamp file should be created'
      def timestampFile = new File('timestamp.out')
      timestampFile.exists()
      timestampFile.text.contains('ts')
  }

  def notifiesUserWhenBackupStartsFromBeginningOfOplog() {
	  given:'arguments contains -s and -f option'
		  context.setVariable('args', ['-s', unsecureSrcNode, "--port=$unsecureSrcPort", '-f', backupFile])

	  and: 'timestamp.out is not present'
		  File timestampFile = new File('timestamp.out')
		  timestampFile.delete()

	  when: 'backup runs with above args'
		  new Backup(context).run()

	  then: 'Notification message should be Shown as'
		  result.toString().contains('Backing up from start of oplog')
  }

  def notifiesUserTheTimestampFromWhereTheBackupStarts() {
	  given:'arguments contains -s and -f option'
		context.setVariable('args', ['-s', unsecureSrcNode, "--port=$unsecureSrcPort", '-f', backupFile])

	  and: 'backup runs with above args'
		new Backup(context).run()

	  when: 'backup is run again and timestamp file exists'
		new Backup(context).run()

	  then: 'Notification message should be shown as'
		result.toString().contains('Backup is starting from:')
	}
}
