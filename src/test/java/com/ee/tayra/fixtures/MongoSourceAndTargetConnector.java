package com.ee.tayra.fixtures;

import java.net.UnknownHostException;

import com.mongodb.MongoClient;

public class MongoSourceAndTargetConnector {

  private MongoClient source;
  private MongoClient destination;
  private static MongoSourceAndTargetConnector singleton = null;

  public MongoSourceAndTargetConnector(final String srcHost, final int srcPort,
      final String destHost, final int destPort) throws UnknownHostException {
    source = new MongoClient(srcHost, srcPort);
    destination = new MongoClient(destHost, destPort);
  }
//  private MongoSourceAndTargetConnector() {
//  source = new MongoClient(srcHost, srcPort);
//      destination = new MongoClient(destHost, destPort);
//  }

  public static MongoSourceAndTargetConnector connectTo(final String srcHost,
  final int srcPort, final String destHost, final int destPort)
  throws UnknownHostException {
     if (singleton == null) {
       singleton = new MongoSourceAndTargetConnector(srcHost, srcPort, destHost,
         destPort);
     }
     return singleton;
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
