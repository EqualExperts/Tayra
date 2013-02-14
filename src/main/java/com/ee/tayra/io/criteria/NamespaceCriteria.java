package com.ee.tayra.io.criteria;

public class NamespaceCriteria implements Criterion {
  private static final String BLANK = "";
  private String incomingNs;
  private String collectionName;
  private String dbName;

  public NamespaceCriteria(final String ns) {
    this.incomingNs = ns;
    this.dbName = extractDbName(ns);
    this.collectionName = extractCollectionName(ns);
  }

@Override
  public boolean isSatisfiedBy(final String document) {
    String documentNamespace = getNamespace(document);
    if (documentNamespace == BLANK) {
      return false;
    }
    if (isDDL(documentNamespace)) {
      return matchDDL(document, documentNamespace);
    }
    return matchDML(document, documentNamespace);
  }


private boolean matchDML(final String document,
    final String documentNamespace) {
  if (collectionName == BLANK) {
      return matchDbName(documentNamespace);
    }
    return matchDbAndCollectionName(documentNamespace);
}

  private boolean isDDL(final String documentNamespace) {
    String command = documentNamespace.split("\\.", 2)[1];
       return (("$cmd".equals(command)) || ("system.indexes".equals(command)));
  }
private boolean matchDDL(final String document,
    final String documentNamespace) {
  if (matchDbName(documentNamespace)) {
    if (collectionName == BLANK) {
      return true;
    }
    return matchCollectionInPayload(document, collectionName);
  }
  return false;
}

private boolean matchDbAndCollectionName(final String documentNamespace) {
  if (incomingNs.equals(documentNamespace)) {
      return true;
  }
  return false;
}

private boolean matchDbName(final String documentNamespace) {
  String documentDb = documentNamespace.split("\\.", 2)[0];
    if (dbName.equals(documentDb)) {
      return true;
    }
    return false;
}

  private boolean matchCollectionInPayload(final String document,
    final String incomingCollectionName) {

    int startIndex = document.indexOf("\"o\" :") - 1;
    String payload = document.substring(startIndex).split(":", 2)[1];
    if (payload.contains("dropDatabase")) {
      return true;
    }
    if (payload.contains("create")) {
      return matchCreateCollection(incomingCollectionName, payload);
    }
    if (payload.contains("drop")) {
      return matchDropCollection(incomingCollectionName, payload);
    }
    if (payload.contains("ns")) {
      return matchCreateIndex(incomingCollectionName, payload);
    }
    if (payload.contains("deleteIndexes")) {
      return matchDropIndex(incomingCollectionName, payload);
  }
    return false;
}

private boolean matchCreateIndex(final String incomingCollectionName,
  final String payload) {
  int startIndex = payload.indexOf("\"ns\" :") - 1;
    int endIndex = payload.indexOf(",", startIndex);
    String namespace = payload.substring(startIndex, endIndex)
            .split(":") [1].replaceAll("\"", BLANK).trim();
    String collection = namespace.split("\\.", 2) [1];
    if (incomingCollectionName.equals(collection)) {
      return true;
    }
  return false;
}

private boolean matchDropIndex(final String incomingCollectionName,
  final String payload) {
  int startIndex = payload.indexOf("\"deleteIndexes\" :") - 1;
    int endIndex = payload.indexOf(",", startIndex);
    String collection = payload.substring(startIndex, endIndex)
            .split(":") [1].replaceAll("\"", BLANK).trim();
    if (incomingCollectionName.equals(collection)) {
      return true;
    }
  return false;
}

private boolean matchDropCollection(final String incomingCollectionName,
    final String payload) {
    int startIndex = payload.indexOf("\"drop\" :") - 1;
    int endIndex = payload.indexOf("}", startIndex);
    String collection = payload.substring(startIndex, endIndex)
            .split(":") [1].replaceAll("\"", BLANK).trim();
    if (incomingCollectionName.equals(collection)) {
      return true;
    }
  return false;
}

private boolean matchCreateCollection(final String incomingCollectionName,
    final String payload) {
  int startIndex;
  int endIndex;
  startIndex = payload.indexOf("\"create\" :") - 1;
  endIndex = payload.indexOf("}", startIndex);
    if (payload.contains("capped")) {
        endIndex = payload.indexOf(",", startIndex);
    }
    String collection = payload.substring(startIndex, endIndex)
            .split(":") [1].replaceAll("\"", BLANK).trim();
    if (incomingCollectionName.equals(collection)) {
      return true;
    }
    return false;
}
  private String extractDbName(final String incomingNS) {
    return incomingNS.split("\\.", 2)[0];
  }

  private String extractCollectionName(final String incomingNS) {
    try {
      return incomingNS.split("\\.", 2)[1];
    } catch (ArrayIndexOutOfBoundsException e) {
      return BLANK;
    }
  }

private String getNamespace(final String document) {
      int startIndex = document.indexOf("ns") - 1;
      int endIndex = document.indexOf(",", startIndex);
      String namespace = document.substring(startIndex, endIndex)
        .split(":") [1];
      return namespace.replaceAll("\"", BLANK).trim();
    }

}
