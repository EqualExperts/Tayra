package com.ee.tayra.fixtures;

import java.net.UnknownHostException;

import com.ee.tayra.MongoSourceTargetPair;
import com.ee.tayra.connector.MongoAuthenticator;
import com.mongodb.MongoClient;

public class MongoSourceAndTargetConnector {

  private MongoClient source;
  private MongoClient destination;

  public MongoSourceAndTargetConnector(final MongoSourceTargetPair details)
  throws UnknownHostException {
      source = new MongoClient(details.getSrcNode(), details.getSrcPort());
      String username = details.getUsername();
      String password = details.getPassword();
      new MongoAuthenticator(source).authenticate(username, password);

      destination = new MongoClient(details.getTgtNode(), details.getTgtPort());
      new MongoAuthenticator(destination).authenticate(username, password);
  }

  public final MongoClient getSource() {
    return source;
  }

  public final MongoClient getDestination() {
    return destination;
  }

  public final void close() {
    source.close();
    destination.close();
  }
}
