package com.ee.tayra.exploratory

import spock.lang.Specification

import com.ee.tayra.ConnectionFactory
import com.ee.tayra.fixtures.MongoConnectorPair
import com.mongodb.CommandResult
import com.mongodb.DB
import com.mongodb.MongoClient
import com.mongodb.MongoException

class ExploratoryResource extends Specification {
  
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
  private static MongoConnectorPair connector;

  def setupSpec() throws UnknownHostException, MongoException {
    String cmdString = '''source {unsecureSrcNode} on port {unsecureSrcPort} ,
                   target {unsecureTgtNode} on port {unsecureTgtPort}'''

    connector = factory.createMongoSourceTargetConnector(cmdString)
    src = connector.source.mongo
    tgt = connector.target.mongo
    srcHOST = src.getAddress().host
    srcPORT = src.getAddress().port
    tgtHOST = tgt.getAddress().host
    tgtPORT = tgt.getAddress().port
  }
  
  def cleanupSpec() {
    connector.close()
  }

  protected final void run(final String cmdString, final DB db) {
    CommandResult result = db.doEval(cmdString);
  }
}

