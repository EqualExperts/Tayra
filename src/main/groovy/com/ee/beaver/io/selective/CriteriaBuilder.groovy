package com.ee.beaver.io.selective;

import java.util.ArrayList;
import java.util.List;

public class CriteriaBuilder {

private List<Criterion> criteria = new ArrayList<Criterion>();

  public void withDatabase(String db) {
    criteria.add(new DbCriteria(db));
  }

  public void withUntil(String timestamp) {
    criteria.add(new TimestampCriteria(timestamp));
  }

  public Criterion build() {
    if(criteria.isEmpty()) {
      return Criterion.ALL;
    }
    return new MultiCriteria(criteria);
  }
}
