package com.ee.beaver.io;


public class DbCriteria implements Criterion {

  private final String filterValue;

  public DbCriteria(final String filter) {
    this.filterValue = getFilterValue(filter);
  }

  public boolean isSatisfiedBy(final String document) {
      String dbName = getDbName(document);
      if (dbName.equals(filterValue)) {
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

  private String getFilterValue(final String dbfilter) {
    return dbfilter.substring(dbfilter.indexOf("=") + 1);
  }

}
