import com.ee.tayra.fixtures.MongoCommandFixture;
import com.ee.tayra.fixtures.MongoSourceAndTargetConnector;

import fit.Fixture;


public enum MongoDB {
SOURCE {

  @Override
  public Fixture getConnector(final MongoSourceAndTargetConnector
    mongoSourceAndTargetConnector) {
      return new MongoCommandFixture(
        mongoSourceAndTargetConnector.getSource());
  }

},
TARGET {
  @Override
  public Fixture getConnector(final MongoSourceAndTargetConnector
      mongoSourceAndTargetConnector) {
      return new MongoCommandFixture(
        mongoSourceAndTargetConnector.getDestination());
  }
};
public abstract Fixture getConnector(final MongoSourceAndTargetConnector
      mongoSourceAndTargetConnector);
}
