package com.ee.tayra.io.criteria;

public class CollectionCriteria implements Criterion {
  private static final String BLANK = "";
  private final String collectionName;

  public CollectionCriteria(final String collectionName) {
    this.collectionName = collectionName;
  }

  public boolean isSatisfiedBy(final String document) {
    String collection = getCollection(document);
      if (collectionName.equals(collection)) {
        return true;
      }
  return false;
  }

  private String getCollection(final String document) {
    int startIndex = document.indexOf("ns") - 1;
    int endIndex = document.indexOf(",", startIndex);
    String namespace = document.substring(startIndex, endIndex)
      .split(":") [1];
    try {
       namespace = namespace.replaceAll("\"", "").trim().split("\\.")[1];
    } catch (ArrayIndexOutOfBoundsException e) {
      return BLANK;
    }
    return namespace;
  }

}
