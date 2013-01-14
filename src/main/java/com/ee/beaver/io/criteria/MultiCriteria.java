package com.ee.beaver.io.criteria;

import java.util.List;
import static java.util.Collections.unmodifiableList;

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

  public final List<Criterion> criteria() {
    return unmodifiableList(criteria);
  }

}
