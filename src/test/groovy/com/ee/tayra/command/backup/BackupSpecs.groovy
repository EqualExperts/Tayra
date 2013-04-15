package com.ee.tayra.command.backup

import com.ee.tayra.io.DocumentWriter
import spock.lang.*

import com.ee.tayra.connector.Authenticator
import com.ee.tayra.io.CopyListener
import com.ee.tayra.io.ProgressReporter
import com.ee.tayra.io.Reporter
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

  def invokesBackupWhenAllEssentialOptionsAreSuppliedForSecuredConnection() {
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
      result.toString().contains('Cannot Understand [--sNssss, users]')
  }

  def shoutsWhenNoPasswordIsGivenForSecuredReplicaSet() {
    given:'arguments contains -s, -f, -u and -p options'
      context.setVariable('args', ['-s', secureSrcNode, "--port=$secureSrcPort", '-f', backupFile, '-u', username])

    when: 'backup runs with above args'
      new Backup(context).run()

    then: 'the output should contain "ts"'
      result.toString().contains('Cannot Read Password Input, please use -p command line option')
  }
}
