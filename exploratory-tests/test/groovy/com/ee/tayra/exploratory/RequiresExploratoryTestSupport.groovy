package com.ee.tayra.exploratory

import spock.lang.Specification

import com.ee.tayra.ConnectionFactory
import com.ee.tayra.command.backup.Backup
import com.ee.tayra.fixtures.MongoConnectorPair
import com.mongodb.CommandResult
import com.mongodb.DB
import com.mongodb.MongoClient
import com.mongodb.MongoException
public abstract class RequiresExploratoryTestSupport extends Specification {

  private static final ConnectionFactory factory = ConnectionFactory.instance();
  static MongoClient src
  static MongoClient tgt
  static String srcHOST
  static int srcPORT
  static String tgtHOST
  static int tgtPORT
  static String backupFile = 'test.out'
  static final String USERNAME = factory.username
  static final String PASSWORD = factory.password
  static  String timestamp
  private static MongoConnectorPair connector;
  private static File timestampFile = new File('timestamp.out')

  def setupSpec() throws UnknownHostException, MongoException {
    updateTimestampFileToNow()

    String cmdString = '''source {unsecureSrcNode} on port {unsecureSrcPort} ,
                   target {unsecureTgtNode} on port {unsecureTgtPort}'''

    connector = factory.createMongoSourceTargetConnector(cmdString)
    src = connector.source.mongo
    tgt = connector.target.mongo

    srcHOST = src.getAddress().host
    srcPORT = src.getAddress().port
    tgtHOST = tgt.getAddress().host
    tgtPORT = tgt.getAddress().port
    addDataTo(src)
  }

  def cleanupSpec() {
    deleteDataFrom(src)
    connector.close()
  }

  def setup() {
    new File('timestamp.out').text = timestamp
  }

  def updateTimestampFileToNow() {
    String time = '{ "ts" : { "$timestamp" : { "t" : ' + ((int)(System.currentTimeMillis()/1000)) + ', "i" : 0}} }'
    timestampFile.text = time
    timestamp = timestampFile.text
  }

  private final void run(final String cmdString, final DB db) {
    CommandResult result = db.doEval(cmdString);
  }

  protected final void takeBackup() {
    Binding backupContext = new Binding()
    backupContext.setVariable('args', ['-s', srcHOST, "--port=$srcPORT", '-f', backupFile])
    new Backup(backupContext).run()
  }

  private addDataTo(MongoClient mongoNode) {
    DB DLDb = mongoNode.getDB("DL")
    DB TayraDb = mongoNode.getDB("Tayra")
    DB EELabDb = mongoNode.getDB("EELab")
    run('db.profile.insert({name:"One"})', DLDb)
    run('db.thing.insert({name:"One"})', DLDb)
    run('db.profile.insert({name:"One"})', TayraDb)
    run('db.thing.insert({name:"One"})', TayraDb)
    run('db.profile.insert({name:"One"})', EELabDb)
    run('db.thing.insert({name:"One"})', EELabDb)
  }

  protected void addExtraNestedDataTo(MongoClient mongoNode) {
    DB PeopleDb = mongoNode.getDB("People")
    for (int index = 1; index <= 10; index++) {
      def name = 'name:{first:"ABC",last:"XYZ"}'
      def address = "address:{street:'MG Road',flatNo:$index}"
      def data = "{orderNo:$index,$name,$address}"
      def query = "db.Information.insert($data)"
      run(query, PeopleDb)
    }
  }

  protected void deleteDataFrom(MongoClient mongoNode) {
    mongoNode.getDB("DL").dropDatabase()
    mongoNode.getDB("Tayra").dropDatabase()
    mongoNode.getDB("EELab").dropDatabase()
  }
  protected void deleteExtraDataFrom(MongoClient mongoNode) {
    mongoNode.getDB("People").dropDatabase()
  }

  protected assertTargetNodeContainsAllDocuments() {
    tgt.getDB("DL").getCollection("profile").count == 1 &&
    tgt.getDB("DL").getCollection("thing").count() == 1 &&
    tgt.getDB("Tayra").getCollection("profile").count() == 1 &&
    tgt.getDB("Tayra").getCollection("thing").count() == 1 &&
    tgt.getDB("EELab").getCollection("profile").count() == 1 &&
    tgt.getDB("EELab").getCollection("thing").count() == 1
  }
}
