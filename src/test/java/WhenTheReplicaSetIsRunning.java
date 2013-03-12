import java.io.File;
import java.net.UnknownHostException;

import com.ee.tayra.fixtures.AssertMongoFixture;
import com.ee.tayra.fixtures.MongoConnector;
import com.ee.tayra.fixtures.MongoFixture;
import com.ee.tayra.fixtures.RunnerFixture;

import fit.Fixture;
import fitlibrary.DoFixture;

public class WhenTheReplicaSetIsRunning extends DoFixture {
  private MongoConnector mongoConnector;

public WhenTheReplicaSetIsRunning() {
  }

  public final Fixture openTerminal() {
     return new RunnerFixture();
  }

  public final Fixture connectToMongoDBNodeOnPort(final String host,
  final int port) throws UnknownHostException {
    return new MongoFixture(host, port);
  }

  public final void forSourceNodeOnPortAndDestinationNodeOnPort(
    final String srcHost, final int srcPort, final String destHost,
    final int destPort) throws UnknownHostException, InterruptedException {
    mongoConnector = new MongoConnector(srcHost, srcPort, destHost, destPort);
  }
  public final Fixture assertThat() {
    return new AssertMongoFixture(mongoConnector);
  }

  public final void cleanBackupFile(final String fileName) {
    File file = new File(fileName);
    if (file.exists()) {
      file.deleteOnExit();
    }
  }

  @Override
  protected final void tearDown() throws Exception {
    mongoConnector.getSource().close();
    mongoConnector.getDestination().close();
    super.tearDown();
  }
}
