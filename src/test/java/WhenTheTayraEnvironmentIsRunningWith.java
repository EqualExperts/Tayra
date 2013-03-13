import java.io.File;
import java.net.UnknownHostException;

import com.ee.tayra.fixtures.AssertMongoFixture;
import com.ee.tayra.fixtures.MongoSourceAndTargetConnector;
import com.ee.tayra.fixtures.RunnerFixture;

import fit.Fixture;
import fitlibrary.DoFixture;

public class WhenTheTayraEnvironmentIsRunningWith extends DoFixture {
  private MongoSourceAndTargetConnector mongoSourceAndTargetConnector;

  public WhenTheTayraEnvironmentIsRunningWith() {
  }

  public final void sourceReplicaSetOnPortAndTargetNodeOnPort(
    final String srcHost, final int srcPort, final String destHost,
    final int destPort) throws UnknownHostException, InterruptedException {
      mongoSourceAndTargetConnector = new MongoSourceAndTargetConnector(
        srcHost, srcPort, destHost, destPort);
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

  public final void cleanBackupFile(final String fileName) {
    File file = new File(fileName);
    if (file.exists()) {
      file.deleteOnExit();
    }
  }

  @Override
  protected final void tearDown() throws Exception {
    mongoSourceAndTargetConnector.close();
    super.tearDown();
  }
}
