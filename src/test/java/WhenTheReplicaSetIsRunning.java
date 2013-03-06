import java.net.UnknownHostException;
import com.ee.tayra.fixtures.AssertMongoFixture;
import com.ee.tayra.fixtures.MongoFixture;
import com.ee.tayra.fixtures.RunnerFixture;

import fit.Fixture;
import fitlibrary.DoFixture;

public class WhenTheReplicaSetIsRunning extends DoFixture {
  public WhenTheReplicaSetIsRunning() {
  }

  public final Fixture openTerminal() {
     return new RunnerFixture();
  }

  public final Fixture connectToMongoDBNodeOnPort(final String host,
  final int port) throws UnknownHostException {
    return new MongoFixture(host, port);
  }

  public final Fixture forSourceNodeOnPortAndDestinationNodeOnPort(
   final String srcHost, final int srcPort, final String destHost,
   final int destPort) throws UnknownHostException {
    return new AssertMongoFixture(srcHost, srcPort, destHost, destPort);
  }

  public final void sleepFor(final long time) throws InterruptedException {
    Thread.sleep(time);
  }
}
