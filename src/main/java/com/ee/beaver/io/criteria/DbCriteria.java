package com.ee.beaver.io.criteria;


public class DbCriteria implements Criterion {

  private final String db;

  public DbCriteria(final String dbName) {
    this.db = dbName;
  }

  public boolean isSatisfiedBy(final String document) {
      String dbName = getDbName(document);
      if (dbName.equals(db)) {
        return true;
      }
  return false;
}

  private String getDbName(final String document) {
    int startIndex = document.indexOf("ns") - 1;
    int endIndex = document.indexOf(",", startIndex);
    String namespace = document.substring(startIndex, endIndex).split(":") [1];
    return namespace.replaceAll("\"", "").trim().split("\\.")[0];
  }

}
