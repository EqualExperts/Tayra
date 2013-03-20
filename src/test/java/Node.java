import com.ee.tayra.fixtures.MongoCommandFixture;
import com.ee.tayra.fixtures.MongoSourceAndTargetConnector;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

import fit.Fixture;

public abstract class Node {
  public abstract Fixture getMongoFixture();

  public abstract Mongo getMongo();

  public static final String SOURCE = "SOURCE";
  public static final String TARGET = "TARGET";

  public static Node valueOf(final String nodeName,
      final MongoSourceAndTargetConnector connector) {
    if (SOURCE.equalsIgnoreCase(nodeName)) {
      return new MongoNode(connector.getSource());
    }
    if (TARGET.equalsIgnoreCase(nodeName)) {
      return new MongoNode(connector.getDestination());
    }
    throw new IllegalArgumentException("Don't know how to process "
        + nodeName);
  }

  private static class MongoNode extends Node {

    private final MongoClient mongo;

    public MongoNode(final MongoClient mongo) {
      this.mongo = mongo;
    }

    @Override
    public Fixture getMongoFixture() {
      return new MongoCommandFixture(mongo);
    }

    @Override
    public Mongo getMongo() {
      return mongo;
    }

  }
}
