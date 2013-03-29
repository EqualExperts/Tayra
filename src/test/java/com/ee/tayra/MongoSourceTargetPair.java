package com.ee.tayra;

import java.util.ArrayList;
import java.util.List;

public class MongoSourceTargetPair {
    private String srcNode;
    private int srcPort;
    private String tgtNode;
    private int tgtPort;
    private String username;
    private String password;

    public MongoSourceTargetPair(
    final String cmdString, final NamedParameters namedParams) {
      List<String> keys = extractKeysFrom(cmdString);
      srcNode = namedParams.get(key("SrcNode", keys));
      srcPort = Integer.parseInt(namedParams.get(key("SrcPort", keys)));
      tgtNode = namedParams.get(key("TgtNode", keys));
      tgtPort = Integer.parseInt(namedParams.get(key("TgtPort", keys)));
      username = namedParams.get(key("username", keys));
      password = namedParams.get(key("password", keys));
    }

    private List<String> extractKeysFrom(final String cmdString) {
        List<String> userKeys = new ArrayList<String>();
        for (String key : cmdString.split(" ")) {
            if (key.contains("{")) {
                userKeys.add(key);
            }
        }
        return userKeys;
    }

    private String key(final String key,
                                   final List<String> userKeys) {
        for (String userKey : userKeys) {
            if (userKey.contains(key)) {
                return userKey;
            }
        }
        return key;
    }

    public final String getSrcNode() {
        return srcNode;
    }

    public final int getSrcPort() {
        return srcPort;
    }

    public final String getUsername() {
        return username;
    }

    public final String getPassword() {
        return password;
    }

    public final String getTgtNode() {
        return tgtNode;
    }

    public final int getTgtPort() {
        return tgtPort;
    }
}
