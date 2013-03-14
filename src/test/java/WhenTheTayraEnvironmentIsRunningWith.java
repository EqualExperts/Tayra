import java.net.UnknownHostException;

import com.ee.tayra.fixtures.AssertMongoFixture;
import com.ee.tayra.fixtures.MongoSourceAndTargetConnector;
import com.ee.tayra.fixtures.RunnerFixture;

import fit.Fixture;
import fitlibrary.DoFixture;

public class WhenTheTayraEnvironmentIsRunningWith extends DoFixture {
  private static final int TARGET_PORT = 7;
  private static final int TARGET = 5;
  private static final int SOURCE_PORT = 3;
  private static final int SOURCE = 1;
  private MongoSourceAndTargetConnector mongoSourceAndTargetConnector;
  private String sourceNode;
  private int sourcePort;
  private String targetNode;
  private int targetPort;

  public WhenTheTayraEnvironmentIsRunningWith() {
  }

  @Override
  protected final void setUp() throws Exception {
    sourceNode = args[SOURCE];
    sourcePort = Integer.parseInt(args[SOURCE_PORT]);
    targetNode = args[TARGET];
    targetPort = Integer.parseInt(args[TARGET_PORT]);
    mongoSourceAndTargetConnector = new MongoSourceAndTargetConnector(
      sourceNode, sourcePort, targetNode, targetPort);
  }

  public final Fixture openTerminal() {
     return new RunnerFixture();
  }

  public final Fixture runMongoCommandOn(final String node)
    throws UnknownHostException {
    MongoDB mongoNode = MongoDB.valueOf(node.toUpperCase());
    return mongoNode.getConnector(mongoSourceAndTargetConnector);
  }

  public final Fixture ensureThat() {
    return new AssertMongoFixture(mongoSourceAndTargetConnector);
  }

  @Override
  protected final void tearDown() throws Exception {
    mongoSourceAndTargetConnector.close();
    super.tearDown();
  }
}
