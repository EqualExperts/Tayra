package com.ee.tayra.fixtures;

import java.net.UnknownHostException;

import com.ee.tayra.fixtures.support.NamedParameters;
import com.mongodb.MongoClient;

public class MongoSourceAndTargetConnector {

  private MongoClient source;
  private MongoClient destination;

  public MongoSourceAndTargetConnector(final NamedParameters namedParams)
    throws UnknownHostException {
      source = new MongoClient(namedParams.get("{sourceNode}"),
        Integer.parseInt(namedParams.get("{sourcePort}")));
      destination = new MongoClient(namedParams.get("{targetNode}"),
        Integer.parseInt(namedParams.get("{targetPort}")));
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
