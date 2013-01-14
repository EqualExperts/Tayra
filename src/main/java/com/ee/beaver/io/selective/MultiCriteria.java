package com.ee.beaver.io.selective;

import java.util.List;

public class MultiCriteria implements Criterion {

  private List<Criterion> criteria;

  public MultiCriteria(final List<Criterion> criteria) {
    this.criteria = criteria;
  }

  @Override
  public final boolean isSatisfiedBy(final String document) {
    boolean isSatisfied = false;
    for (Criterion criterion: criteria) {
      isSatisfied = criterion.isSatisfiedBy(document);
      if (!isSatisfied) {
        break;
      }
    }
    return isSatisfied;
  }

}
