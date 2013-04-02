package com.ee.tayra.exploratory

import spock.lang.Specification

import com.ee.tayra.ConnectionFactory
import com.ee.tayra.command.backup.Backup
import com.ee.tayra.fixtures.MongoConnectorPair
import com.mongodb.CommandResult
import com.mongodb.DB
import com.mongodb.MongoClient
import com.mongodb.MongoException

class ExploratoryTestSupport extends Specification {
  
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
    timestampFile.delete()
    deleteBackupFiles(0)
    deleteDataFrom(src)
    connector.close()
  }

  def setup() {
    new File('timestamp.out').text = timestamp
  }

  def updateTimestampFileToNow() {
    String time = '{ "ts" : { "$ts" : ' + ((int)(System.currentTimeMillis()/1000)) + ', "$inc" : 0} }'
    timestampFile.text = time
    timestamp = timestampFile.text
  }

  private final void run(final String cmdString, final DB db) {
    CommandResult result = db.doEval(cmdString);
  }

//  protected final void insert (final BasicDBObject doc, final DBCollection collection){
//    collection.insert(doc)
//  }

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

  protected void deleteDataFrom(MongoClient mongoNode) {
    mongoNode.getDB("DL").dropDatabase()
    mongoNode.getDB("Tayra").dropDatabase()
    mongoNode.getDB("EELab").dropDatabase()
  }

  protected void addExtraDataTo(MongoClient mongoNode) {
    DB ExtraDb = mongoNode.getDB("Extra")
    run('db.thing.insert({name:"One"})', ExtraDb)
    run('db.thing.insert({name:"Two"})', ExtraDb)
    run('db.thing.insert({name:"Three"})', ExtraDb)
    run('db.thing.insert({name:"Four"})', ExtraDb)
    run('db.thing.insert({name:"Five"})', ExtraDb)
    run('db.thing.insert({name:"Six"})', ExtraDb)
  }
  
  protected void deleteExtraDataFrom(MongoClient mongoNode) {
    mongoNode.getDB("Extra").dropDatabase()
  }

  protected void deleteBackupFiles(int noOfFiles) {
    File backup = new File(backupFile)
    if(backup.exists()) {
      backup.delete()
    }
    for(int i=1; i<=noOfFiles; i++) {
      File file = new File(backupFile + "." + i)
      if(file.exists()) {
        file.delete()
      }
    }
  }
}
