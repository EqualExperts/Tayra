package com.ee.tayra.fixtures;

public class MongoConnectorPair {
  public static final String SOURCE = "SOURCE";
  public static final String TARGET = "TARGET";
  private MongoConnector source;
  private MongoConnector target;

  public MongoConnectorPair(
  final MongoConnector source, final MongoConnector target) {
    this.source = source;
    this.target = target;
  }

  public final MongoConnector get(final String nodeName) {
    final String node = nodeName.toUpperCase();
    if (SOURCE.equalsIgnoreCase(node)) {
      return source;
    }
    if (TARGET.equalsIgnoreCase(node)) {
      return target;
    }
    throw new IllegalArgumentException("Don't know how to process " + nodeName);
  }

  public final MongoConnector getSource() {
    return source;
  }

  public final MongoConnector getTarget() {
    return target;
  }

  public final void close() {
    source.close();
    target.close();
  }
}
