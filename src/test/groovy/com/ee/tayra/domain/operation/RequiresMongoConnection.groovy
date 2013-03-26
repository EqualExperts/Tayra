package com.ee.tayra.domain.operation

import spock.lang.*

import com.ee.tayra.parameters.EnvironmentProperties
import com.mongodb.MongoClient
import com.mongodb.MongoException
import com.mongodb.ServerAddress

abstract class RequiresMongoConnection extends Specification {
  static String HOST
  static int PORT
  static MongoClient standalone
  String dbName = 'tayra'

  def setupSpec() throws UnknownHostException,
  MongoException {
    HOST = EnvironmentProperties.secureStandaloneNode
    PORT = EnvironmentProperties.secureStandalonePort
    ServerAddress server = new ServerAddress(HOST, PORT)
    standalone = new MongoClient(server);
    standalone.getDB('admin').authenticate('admin', 'admin'.toCharArray())
  }

  def cleanupSpec() {
    standalone.close();
  }
}
