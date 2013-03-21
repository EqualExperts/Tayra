import java.net.UnknownHostException;

import com.ee.tayra.fixtures.AssertMongoFixture;
import com.ee.tayra.fixtures.MongoSourceAndTargetConnector;
import com.ee.tayra.fixtures.RunnerFixture;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

import fit.Fixture;
import fit.exception.FitFailureException;
import fitlibrary.DoFixture;

public class GivenSourceReplicaSetAndTargetNodeAreRunning extends DoFixture {

  private MongoSourceAndTargetConnector connector;
  private String value = "";

  public GivenSourceReplicaSetAndTargetNodeAreRunning() {
  }

  public final void withSourceNodeOnPortAndTargetNodeOnPort(
      final String sourceNode, final int sourcePort,
      final String targetNode, final int targetPort)
      throws UnknownHostException {
    connector = new MongoSourceAndTargetConnector(sourceNode, sourcePort,
        targetNode, targetPort);
  }

  public final Fixture openTerminal() {
    return new RunnerFixture(value);
  }
  public final void sleep(final int duration) {
    try {
    Thread.sleep(duration);
  } catch (InterruptedException e) {
    e.printStackTrace();
  }
  }

  public final Fixture runMongoCommandOn(final String nodeName)
      throws UnknownHostException {
    Node node = Node.valueOf(nodeName.toUpperCase(), connector);
    return node.getMongoFixture();
  }

  public final Fixture ensuringTargetIsConsistentWithSource() {
    return new AssertMongoFixture(connector);
  }

  public final boolean openOplogForNodeAndTravelDocumentsBackInTime(
      final String nodeName, final int howMany) {
    Node node = Node.valueOf(nodeName.toUpperCase(), connector);
    Mongo mongo = node.getMongo();
    try {
      DBCollection collection = mongo.getDB("local").getCollection(
          "oplog.rs");
      DBCursor cursor = collection.find().skip(
          (int) collection.count() - howMany);
      addValue(JSON.serialize(cursor.next().get("ts")).replaceAll(
          "[\" ]", ""));
    } catch (MongoException problem) {
      throw new FitFailureException(problem.getMessage());
    }
    return true;
  }

  private void addValue(final String value) {
    this.value = value;
  }

  public final Fixture ensureSourceAndTargetHas() {
    return ensuringTargetIsConsistentWithSource();
  }

  @Override
  protected final void tearDown() throws Exception {
    connector.close();
    super.tearDown();
  }
}
