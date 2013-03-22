import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;

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

  private static final int SLEEP_TIME = 200;
  private MongoSourceAndTargetConnector connector;
  private String value = "";
  private String sourceNodeProp = null;
  private String sourcePortProp = null;
  private String targetNodeProp = null;
  private String targetPortProp = null;

  public GivenSourceReplicaSetAndTargetNodeAreRunning() {
    loadProperties();
  }

  private void loadProperties() {
    Properties properties = new Properties();
    try {
      properties.load(new FileInputStream(getPropertyFile()));
      sourceNodeProp = properties.getProperty("sourceNode");
      sourcePortProp = properties.getProperty("sourcePort");
      targetNodeProp = properties.getProperty("targetNode");
      targetPortProp = properties.getProperty("targetPort");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String getPropertyFile() {
    String env = System.getProperty("env", "dev").toUpperCase();
    return Environment.valueOf(env).getConfiguration();
  }

  public final void withSourceNodeOnPortAndTargetNodeOnPort(
      final String sourceNode, final String sourcePort,
      final String targetNode, final String targetPort)
      throws UnknownHostException {
    // connector = new MongoSourceAndTargetConnector(sourceNode, sourcePort,
    // targetNode, targetPort);
    connector = new MongoSourceAndTargetConnector(sourceNodeProp,
        Integer.parseInt(sourcePortProp), targetNodeProp,
        Integer.parseInt(targetPortProp));
  }

  public final Fixture openTerminal() {
    RunnerFixture runnerFixture = new RunnerFixture(value);
    return runnerFixture;
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
      addValue(JSON.serialize(cursor.next().get("ts")).replaceAll(
          "[\" ]", ""));
    } catch (MongoException problem) {
      throw new FitFailureException(problem.getMessage());
    } finally {
      System.out.println("closing cursor");
      cursor.close();
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
