package com.ee.tayra.fixtures;

import java.net.UnknownHostException;

import com.mongodb.MongoClient;

public class MongoSourceAndTargetConnector {

  private MongoClient source;
  private MongoClient destination;

  public MongoSourceAndTargetConnector(final String srcHost, final int srcPort,
      final String destHost, final int destPort) throws UnknownHostException {
    source = new MongoClient(srcHost, srcPort);
    destination = new MongoClient(destHost, destPort);
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
