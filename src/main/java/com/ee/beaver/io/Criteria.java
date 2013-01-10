package com.ee.beaver.io;


public class Criteria {
private String filter;

public Criteria(final String filter) {
  this.filter = filter;
}
    public Criterion getCriterion() {
      if (filter.contains("-sDb")) {
        return new DbCriteria(filter);
      }
      if (filter.contains("-sUntil")) {
        return new TimestampCriteria(filter);
      }
      return Criterion.ALL;
    }
}
