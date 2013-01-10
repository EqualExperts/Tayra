package com.ee.beaver.io;


public class SelectiveOplogReplayer implements Replayer {
  private final Replayer target;
  private final Criteria criteria;

  public SelectiveOplogReplayer(final Criteria criteria,
    final Replayer target) {
    this.target = target;
    this.criteria = criteria;
  }

@Override
  public boolean replay(final String document) {
  Criterion criterion = criteria.getCriterion();
    if (criterion.isSatisfiedBy(document)) {
      return target.replay(document);
    }
    return false;
}
}
