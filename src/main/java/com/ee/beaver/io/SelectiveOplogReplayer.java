package com.ee.beaver.io;


public class SelectiveOplogReplayer implements Replayer {
  private final Replayer target;
  private final Criterion criterion;

  public SelectiveOplogReplayer(final Criteria criteria,
    final Replayer target) {
    this.target = target;
    this.criterion = criteria.getCriterion();
  }

@Override
  public boolean replay(final String document) {
    if (criterion.isSatisfiedBy(document)) {
      return target.replay(document);
    }
    return false;
}
}
