import java.io.IOException;
import java.net.UnknownHostException;

import com.ee.tayra.ConnectionData;
import com.ee.tayra.MongoSourceTargetPair;
import com.ee.tayra.NamedParameters;
import com.ee.tayra.fixtures.AssertMongoFixture;
import com.ee.tayra.fixtures.MongoSourceAndTargetConnector;
import com.ee.tayra.fixtures.RunnerFixture;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

import fit.Fixture;
import fit.Parse;
import fit.exception.FitFailureException;
import fit.exception.MissingCellsFailureException;
import fitlibrary.DoFixture;

public class GivenSourceReplicaSetAndTargetNodeAreRunning extends DoFixture {

  private static final int SLEEP_TIME = 800;
  private MongoSourceAndTargetConnector connector;
  private NamedParameters parameters;

  public GivenSourceReplicaSetAndTargetNodeAreRunning()
      throws UnknownHostException {
    parameters = ConnectionData.instance().settings();
    parameters.add("{file}", "test.out");
  }

  public final Fixture openTerminal() {
    return new RunnerFixture(parameters);
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
    sleep(SLEEP_TIME);
    return new AssertMongoFixture(connector);
  }

  public final boolean
    openOplogForNodeAndTravelDocumentsBackInTimeAndSaveTimestampIn(
      final String nodeName, final int howMany, final String keyToSave) {
    Node node = Node.valueOf(nodeName.toUpperCase(), connector);
    MongoClient mongo = node.getMongo();
    DBCursor cursor = null;
    try {
      DBCollection collection = mongo.getDB("local").getCollection("oplog.rs");
      cursor = collection.find().skip((int) collection.count() - howMany);
      parameters.add(keyToSave, JSON.serialize(cursor.next().get("ts"))
              .replaceAll("[\" ]", ""));
    } catch (MongoException problem) {
      throw new FitFailureException(problem.getMessage());
    } finally {
      cursor.close();
    }
    return true;
  }

  public final Fixture ensureSourceAndTargetHas() {
    return ensuringTargetIsConsistentWithSource();
  }

  public final void withConfiguration(final Parse cells) throws IOException {
    Parse args = cells.more;
    if (args == null) {
      throw new MissingCellsFailureException(cells.text()
          + " requires an argument");
    }
    String cmdString = args.text();
    MongoSourceTargetPair details =
         new MongoSourceTargetPair(cmdString, parameters);
    connector = new MongoSourceAndTargetConnector(details);
    cmdString = parameters.substitueValuesIn(cmdString);
    args.addToBody("<hr/>" + label("Substituted Output") + "<hr/>");
    args.addToBody("<pre/>" + cmdString + "</pre>");
  }

  @Override
  protected final void tearDown() throws Exception {
    connector.close();
    super.tearDown();
  }
}
