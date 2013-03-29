package com.ee.tayra.fixtures;

import com.ee.tayra.connector.MongoAuthenticator;
import com.mongodb.MongoClient;
import fit.Fixture;

import java.net.UnknownHostException;

public class MongoConnector {
  private final MongoClient mongo;

  public MongoConnector(final String name, final int port,
  final String username, final String password) throws UnknownHostException {
    mongo = new MongoClient(name, port);
    new MongoAuthenticator(mongo).authenticate(username, password);
  }

  public final MongoClient getMongo() {
    return mongo;
  }

  public final Fixture createMongoFixture() {
    return new MongoCommandFixture(mongo);
  }

  public final void close() {
    mongo.close();
  }
}
