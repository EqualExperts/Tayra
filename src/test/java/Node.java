import com.ee.tayra.fixtures.MongoCommandFixture;
import com.ee.tayra.fixtures.MongoSourceAndTargetConnector;

import fit.Fixture;


public enum Node {

  SOURCE {
    @Override
    public Fixture getMongoFixture(final MongoSourceAndTargetConnector
        connector) {
      return new MongoCommandFixture(connector.getSource());
    }
  },

  TARGET {
    @Override
    public Fixture getMongoFixture(final MongoSourceAndTargetConnector
        connector) {
      return new MongoCommandFixture(connector.getDestination());
    }
  };

  public abstract Fixture getMongoFixture(final MongoSourceAndTargetConnector
      connector);

}
