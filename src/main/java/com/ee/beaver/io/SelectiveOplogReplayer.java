package com.ee.beaver.io;

public class SelectiveOplogReplayer implements Replayer {
  private Replayer target;
  private Criteria criteria;

  public SelectiveOplogReplayer(final Criteria criteria,
    final Replayer target) {
    this.target = target;
    this.criteria = criteria;
  }

@Override
  public boolean replay(final String document) {
    if (criteria.isSatisfiedBy(document) || criteria.notGiven()) {
      return target.replay(document);
    }
    return false;
  }
}
