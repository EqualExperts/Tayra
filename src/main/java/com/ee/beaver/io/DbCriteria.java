package com.ee.beaver.io;


public class DbCriteria implements Criteria {

  private String filter;

  public DbCriteria(final String filter) {
    this.filter = filter;
  }
@Override
public final boolean isSatisfiedBy(final String document) {
  if (filter.equals(null)) {
    return false;
  }
  if (filter.contains("-sDb")) {
    String filterValue = getFilterValue(filter);
    String namespace = getNameSpace(document);
    if (namespace.contains(filterValue)) {
      return true;
    }
  }
  return false;
}

  private String getNameSpace(final String document) {
    return document.substring(document.indexOf("\"ns\""),
      document.indexOf("."));
  }

  private String getFilterValue(final String dbfilter) {
    return dbfilter.substring(dbfilter.indexOf("=") + 1);
  }
}
