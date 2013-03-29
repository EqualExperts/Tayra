package com.ee.tayra;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public final class ConnectionData {

    public static final String MONGO_VERSION = "mongoVersion";
    public static final String PORT_PREFIX = "PORT_PREFIX";
    public static final String DEFAULT_PORT_PREFIX = "270";
    public static final String BLANK = "";
    private static ConnectionData singleton;

    private NamedParameters parameters;

    private ConnectionData(final String portPrefix) {
       parameters = load("connection.properties", portPrefix);
    }

    private NamedParameters load(final String propertiesFile,
    final String portPrefix) {
        NamedParameters params = new NamedParameters();
        Properties properties = new Properties();
        FileInputStream file = null;
        try {
            file = new FileInputStream(propertiesFile);
            properties.load(file);
            addTo(params, properties, portPrefix);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
              try {
                  file.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
            }
        }
        return params;
    }

    private void addTo(
      final NamedParameters params, final Properties props,
      final String portPrefix) {
        for (String propertyName : props.stringPropertyNames()) {
          String value = props.getProperty(propertyName);
             params.add("{" + propertyName + "}",
             substitutePortPrefix(value, portPrefix));
        }
    }

    private String substitutePortPrefix(final String data,
    final String portPrefix) {
        return data.replaceAll(PORT_PREFIX, portPrefix);
    }

    public static ConnectionData instance() {
      String mongoVersion =
        System.getProperty(MONGO_VERSION, BLANK).toUpperCase();
      String portPrefix = convertToPortPrefix(mongoVersion);
      System.out.println("Running tests with PORTPREFIX:" + portPrefix);
      if (singleton == null) {
        singleton = new ConnectionData(portPrefix);
      }
      return singleton;
    }

    public NamedParameters settings() {
        return parameters;
    }

    private static String convertToPortPrefix(final String mongoVersion) {
        if (mongoVersion.isEmpty())  {
            return DEFAULT_PORT_PREFIX;
        }
        return mongoVersion.replaceAll("\\.", BLANK);
    }

    public String getSecureSrcPort() {
        return parameters.get("{secureSrcPort}");
    }
    public String getSecureSrcNode() {
        return parameters.get("{secureSrcNode}");
    }
    public String getSecureTgtPort() {
        return parameters.get("{secureTgtPort}");
    }
    public String getSecureTgtNode() {
        return parameters.get("{secureTgtNode}");
    }
    public String getUnsecureSrcPort() {
        return parameters.get("{unsecureSrcPort}");
    }
    public String getUnsecureSrcNode() {
        return parameters.get("{unsecureSrcNode}");
    }
    public String getUnsecureTgtPort() {
        return parameters.get("{unsecureTgtPort}");
    }
    public String getUnsecureTgtNode() {
        return parameters.get("{unsecureTgtNode}");
    }
    public String getUsername() {
        return parameters.get("{username}");
    }
    public String getPassword() {
        return parameters.get("{password}");
    }
}
