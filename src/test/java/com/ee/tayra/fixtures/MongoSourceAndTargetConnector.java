package com.ee.tayra.fixtures;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.ee.tayra.NamedParameters;
import com.ee.tayra.connector.MongoAuthenticator;
import com.mongodb.MongoClient;

public class MongoSourceAndTargetConnector {

  private MongoClient source;
  private MongoClient destination;

  public MongoSourceAndTargetConnector(final String cmdString,
      final NamedParameters namedParams) throws UnknownHostException {
    createMongoClientsFrom(cmdString, namedParams);
    authenticateMongoClients(namedParams);
  }

  private void authenticateMongoClients(final NamedParameters namedParams) {
    new MongoAuthenticator(source).authenticate(
        namedParams.get("{username}"), namedParams.get("{password}"));
    new MongoAuthenticator(destination).authenticate(
        namedParams.get("{username}"), namedParams.get("{password}"));
  }

  private void createMongoClientsFrom(final String cmdString,
      final NamedParameters namedParams) throws UnknownHostException {
    List<String> params = new ArrayList<String>();
    for (String param : cmdString.split(" ")) {
      if (param.contains("{")) {
        params.add(param);
      }
    }
    int index = 0;
    source = new MongoClient(namedParams.get(params.get(index++)),
        Integer.parseInt(namedParams.get(params.get(index++))));
    destination = new MongoClient(namedParams.get(params.get(index++)),
        Integer.parseInt(namedParams.get(params.get(index))));
  }

  public final MongoClient getSource() {
    return source;
  }

  public final MongoClient getDestination() {
    return destination;
  }

  public final void close() {
    source.close();
    destination.close();
  }
}
