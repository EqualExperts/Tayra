package com.ee.tayra;

import com.ee.tayra.fixtures.MongoConnector;
import com.ee.tayra.fixtures.MongoConnectorPair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.StringTokenizer;

public final class ConnectionFactory {
  //Create Factory at Start-up
  static {
      ConnectionFactory.instance();
  }

  public static final String MONGO_VERSION = "mongoVersion";
  public static final String PORT_PREFIX = "PORT_PREFIX";
  public static final String DEFAULT_PORT_PREFIX = "270";
  public static final String BLANK = "";
  public static final int DEFAULT_PORT = 27017;
  public static final String DEFAULT_HOST = "localhost";
  private static ConnectionFactory singleton;

  private static NamedParameters parameters;

  private ConnectionFactory(final String portPrefix) {
    parameters = load("connection.properties", portPrefix);
  }

  private static NamedParameters load(
  final String file, final String portPrefix) {
    NamedParameters params = new NamedParameters();
    Properties properties = new Properties();
    FileInputStream propsFile = null;
    try {
      propsFile = new FileInputStream(file);
      properties.load(propsFile);
      addTo(params, properties, portPrefix);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (propsFile != null) {
        try {
            propsFile.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return params;
  }

  private static void addTo(
  final NamedParameters params, final Properties props, final String prefix) {
    for (String propertyName : props.stringPropertyNames()) {
      String value = props.getProperty(propertyName);
      params.add("{" + propertyName + "}", substitute(value, prefix));
    }
  }

  private static String substitute(final String data, final String prefix) {
    return data.replaceAll(PORT_PREFIX, prefix);
  }

  public static ConnectionFactory instance() {
    String mongoVer = System.getProperty(MONGO_VERSION, BLANK).toUpperCase();
    String portPrefix = toPortPrefix(mongoVer);
    System.out.println("Using PORTPREFIX:" + portPrefix);
    if (singleton == null) {
      singleton = new ConnectionFactory(portPrefix);
    }
    return singleton;
  }

  public NamedParameters settings() {
    return parameters;
  }

  private static String toPortPrefix(final String mongoVersion) {
    if (mongoVersion.isEmpty())  {
      return DEFAULT_PORT_PREFIX;
    }
    return mongoVersion.replaceAll("\\.", BLANK);
  }

  public static int getSecureSrcPort() {
    return Integer.parseInt(parameters.get("{secureSrcPort}"));
  }

  public static String getSecureSrcNode() {
    return parameters.get("{secureSrcNode}");
  }

  public static int getSecureTgtPort() {
    return Integer.parseInt(parameters.get("{secureTgtPort}"));
  }

  public static String getSecureTgtNode() {
    return parameters.get("{secureTgtNode}");
  }

  public static int getUnsecureSrcPort() {
    return Integer.parseInt(parameters.get("{unsecureSrcPort}"));
  }

  public static String getUnsecureSrcNode() {
    return parameters.get("{unsecureSrcNode}");
  }

  public static int getUnsecureTgtPort() {
    return Integer.parseInt(parameters.get("{unsecureTgtPort}"));
  }

  public static String getUnsecureTgtNode() {
    return parameters.get("{unsecureTgtNode}");
  }

  public static String getUsername() {
    return parameters.get("{username}");
  }

  public static String getPassword() {
    return parameters.get("{password}");
  }

  public static String getBackupFile() {
    return parameters.get("{file}");
  }

  public MongoConnectorPair createMongoSourceTargetConnector(
  final String cmdString) throws UnknownHostException {
    MongoConnector src = createConnector("Src", cmdString);
    MongoConnector tgt = createConnector("Tgt", cmdString);
    return new MongoConnectorPair(src, tgt);
  }

  private MongoConnector createConnector(
  final String nodeName, final String cmdString) throws UnknownHostException {
    StringTokenizer tokenizer = new StringTokenizer(cmdString);
    int port = DEFAULT_PORT;
    String host = DEFAULT_HOST;
    while (tokenizer.hasMoreTokens()) {
      final String token = tokenizer.nextToken();
        if (token.matches("\\{.*\\}") && token.contains(nodeName)) {
          if (token.contains(nodeName + "Port")) {
            port = extractPort(token);
            continue;
          }
          if (token.contains(nodeName + "Node")) {
            host = extractHost(token);
            continue;
          }
        }
    }
    return new MongoConnector(host, port, getUsername(), getPassword());
  }

  private String extractHost(final String token) {
    return parameters.get(token);
  }

  private int extractPort(final String token) {
    return Integer.parseInt(parameters.get(token));
  }
}
