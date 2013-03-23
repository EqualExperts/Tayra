import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map.Entry;
import java.util.Properties;

import com.ee.tayra.fixtures.AssertMongoFixture;
import com.ee.tayra.fixtures.MongoSourceAndTargetConnector;
import com.ee.tayra.fixtures.RunnerFixture;
import com.ee.tayra.fixtures.support.NamedParameters;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

import fit.Fixture;
import fit.Parse;
import fit.exception.FitFailureException;
import fit.exception.MissingCellsFailureException;
import fitlibrary.DoFixture;

public class GivenSourceReplicaSetAndTargetNodeAreRunning extends DoFixture {
  public enum Environment {
    DEV {
      @Override
      public String getConfiguration() {
        return "dev.properties";
      }
    },
    TEST {
      @Override
      public String getConfiguration() {
        return "test.properties";
      }
    };

    public abstract String getConfiguration();
  };

  private static final int SLEEP_TIME = 500;
  private MongoSourceAndTargetConnector connector;
  private NamedParameters namedParams;

  public GivenSourceReplicaSetAndTargetNodeAreRunning()
      throws UnknownHostException {
    namedParams = new NamedParameters();
    loadProperties();
    connector = new MongoSourceAndTargetConnector(namedParams);
  }

  private void loadProperties() {
    Properties properties = new Properties();
    try {
      properties.load(new FileInputStream(getPropertyFile()));
      add(properties);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void add(final Properties properties) {
    namedParams.add("{sourceNode}", properties.getProperty("sourceNode"));
    namedParams.add("{sourcePort}", properties.getProperty("sourcePort"));
    namedParams.add("{targetNode}", properties.getProperty("targetNode"));
    namedParams.add("{targetPort}", properties.getProperty("targetPort"));
    namedParams.add("{file}", properties.getProperty("file"));
  }

  private String getPropertyFile() {
    String env = System.getProperty("env", "dev").toUpperCase();
    return Environment.valueOf(env).getConfiguration();
  }

  public final Fixture openTerminal() {
    return new RunnerFixture(namedParams);
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

  public final boolean openOplogForNodeAndTravelDocumentsBackInTime(
      final String nodeName, final int howMany) {
    Node node = Node.valueOf(nodeName.toUpperCase(), connector);
    Mongo mongo = node.getMongo();
    DBCursor cursor = null;
    try {
      DBCollection collection = mongo.getDB("local").getCollection(
          "oplog.rs");
      cursor = collection.find().skip((int) collection.count() - howMany);
      namedParams.add("{Until}", JSON.serialize(cursor.next().get("ts"))
          .replaceAll("[\" ]", ""));
    } catch (MongoException problem) {
      throw new FitFailureException(problem.getMessage());
    } finally {
      System.out.println("closing cursor");
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
    cmdString = injectValuesIn(cmdString, namedParams);
    args.addToBody("<hr/>" + label("Substituted Values Output") + "<hr/>");
    args.addToBody("<pre/>" + cmdString + "</pre>");
  }

  private String injectValuesIn(final String cmdString,
      final NamedParameters namedParams) {
    String result = new String(cmdString);
    for (Entry<String, String> nameValue : namedParams.entrySet()) {
      String key = nameValue.getKey();
      String value = nameValue.getValue();
      result = result.replace(key, value);
    }
    return result;
  }

  @Override
  protected final void tearDown() throws Exception {
    connector.close();
    super.tearDown();
  }
}
