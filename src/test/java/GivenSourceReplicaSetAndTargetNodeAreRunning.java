import java.io.IOException;
import java.net.UnknownHostException;

import com.ee.tayra.ConnectionFactory;
import com.ee.tayra.NamedParameters;
import com.ee.tayra.fixtures.MongoAssertFixture;
import com.ee.tayra.fixtures.MongoConnectorPair;
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
  private final ConnectionFactory factory = ConnectionFactory.instance();
  private final NamedParameters parameters;
  private MongoConnectorPair connector;

  public GivenSourceReplicaSetAndTargetNodeAreRunning()
  throws UnknownHostException {
    parameters = factory.settings();
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

  public final Fixture runMongoCommandOn(final String node)
      throws UnknownHostException {
    return connector.get(node).createMongoFixture();
  }

  public final Fixture ensuringTargetIsConsistentWithSource() {
    //FIXME: Get rid of this sleep!!
    sleep(SLEEP_TIME);
    final MongoClient source = connector.getSource().getMongo();
    final MongoClient target = connector.getTarget().getMongo();
    return new MongoAssertFixture(source, target);
  }

  public final boolean
  openOplogForNodeAndTravelDocumentsBackInTimeAndSaveTimestampIn(
    final String node, final int howMany, final String keyToSave) {
    final MongoClient mongo = connector.get(node).getMongo();
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
    connector = factory.createMongoSourceTargetConnector(cmdString);
    cmdString = parameters.replaceValuesIn(cmdString);
    args.addToBody("<hr/>" + label("Substituted Output") + "<hr/>");
    args.addToBody("<pre/>" + cmdString + "</pre>");
  }

  @Override
  protected final void tearDown() throws Exception {
    connector.close();
    super.tearDown();
  }
}
