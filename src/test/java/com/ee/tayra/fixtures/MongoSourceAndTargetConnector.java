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

  private void createMongoClientsFrom(final String cmdString,
      final NamedParameters namedParams) throws UnknownHostException {
    List<String> userKeys = extractUserKeysFrom(cmdString);
    String srcNode = namedParams.get(userGivenKeyFor("SrcNode", userKeys));
    String srcPort = namedParams.get(userGivenKeyFor("SrcPort", userKeys));
    String tgtNode = namedParams.get(userGivenKeyFor("TgtNode", userKeys));
    String tgtPort = namedParams.get(userGivenKeyFor("TgtPort", userKeys));
    source = new MongoClient(srcNode, Integer.parseInt(srcPort));
    destination = new MongoClient(tgtNode, Integer.parseInt(tgtPort));
  }

private List<String> extractUserKeysFrom(final String cmdString) {
    List<String> userKeys = new ArrayList<String>();
    for (String key : cmdString.split(" ")) {
      if (key.contains("{")) {
        userKeys.add(key);
      }
    }
  return userKeys;
}

  private String userGivenKeyFor(final String key,
                                 final List<String> userKeys) {
    for (String userKey : userKeys) {
      if (userKey.contains(key)) {
        return userKey;
      }
    }
    return key;
  }

  private void authenticateMongoClients(final NamedParameters namedParams) {
    new MongoAuthenticator(source).authenticate(
        namedParams.get("{username}"), namedParams.get("{password}"));
    new MongoAuthenticator(destination).authenticate(
        namedParams.get("{username}"), namedParams.get("{password}"));
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
