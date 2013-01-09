package com.ee.beaver.io;


public class Criteria {

  private final String filter;
  private final String filterValue;

  public Criteria(final String filter) {
    this.filter = filter;
    this.filterValue = getFilterValue(filter);
  }

  public boolean notGiven() {
    if ("".equals(filter)) {
      return true;
    }
  return false;
}

  public boolean isSatisfiedBy(final String document) {
    if (filter.contains("-sDb")) {
      String dbName = getDbName(document);
      if (dbName.equals(filterValue)) {
        return true;
      }
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
