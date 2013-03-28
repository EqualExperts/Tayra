package com.ee.tayra.domain.operation

import spock.lang.*

import static com.ee.tayra.support.Resources.*
import com.mongodb.MongoClient
import com.mongodb.MongoException
import com.mongodb.ServerAddress

abstract class RequiresMongoConnection extends Specification {
  static String HOST = secureStandaloneNode
  static int PORT = secureStandalonePort
  static MongoClient standalone
  String dbName = 'tayra'

  def setupSpec() throws UnknownHostException,
  MongoException {
    ServerAddress server = new ServerAddress(HOST, PORT)
    standalone = new MongoClient(server);
    standalone.getDB('admin').authenticate('admin', 'admin'.toCharArray())
  }

  def cleanupSpec() {
    standalone.close();
  }
}
