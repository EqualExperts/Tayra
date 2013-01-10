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
      return Criterion.ALL;
    }
}
