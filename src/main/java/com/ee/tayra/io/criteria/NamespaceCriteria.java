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
    if(isDDL(documentNamespace)) {
      return matchDDL(document, documentNamespace);
    }
    if (collectionName == BLANK) {
      return matchDbName(document, documentNamespace);
    }
    return matchDbAndCollectionName(document, documentNamespace);
  }

  private boolean isDDL(final String documentNamespace) {
    String command = documentNamespace.split("\\.", 2)[1];
       return (("$cmd".equals(command)) || ("system.indexes".equals(command)));
  }
private boolean matchDDL(final String document,
    final String documentNamespace) {

  if (collectionName == BLANK) {
      return matchDbName(document, documentNamespace);
  }
  if (matchDbName(document, documentNamespace)) {
    return matchCollectionInPayload(document, collectionName);
  }
  return false;
}


private boolean matchDbAndCollectionName(final String document, String documentNamespace) {
  if (documentNamespace.equals(incomingNs)) {
      return true;
  }
  return false;
}

private boolean matchDbName(String document, String documentNamespace) {
  String documentDb = documentNamespace.split("\\.", 2)[0];
    if (documentDb.equals(dbName)) {
      return true;
    }
    return false;
}

  private boolean matchCollectionInPayload(final String document,
    final String incomingCollectionName) {
    int startIndex = document.indexOf("\"o\" :") - 1;
    String payload = document.substring(startIndex).split(":", 2)[1];
    //TO_DO Handle payloads for : create, drop ,deleteIndexes, ns(createIndex)
    if (payload.contains(incomingCollectionName)) {
      return true;
    } else if (payload.contains("dropDatabase")) {
      return true;
    }
    return false;
}

  private String extractDbName(String incomingNS) {
	  try{
		  return incomingNS.split("\\.", 2)[0];
	  } catch (ArrayIndexOutOfBoundsException e) {
		  return incomingNS;
	  }
  }
  
  private String extractCollectionName(String incomingNS) {
    try{
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
