package com.ee.tayra.io;

import com.ee.tayra.io.criteria.Criterion;

public class SelectiveOplogReplayer implements Replayer {
  private final Replayer target;
  private final Criterion criterion;

  public SelectiveOplogReplayer(final Criterion criterion,
    final Replayer target) {
    this.target = target;
    this.criterion = criterion;
  }

  @Override
  public boolean replay(final String document) {
    if (criterion.isSatisfiedBy(document)) {
      return target.replay(document);
    }
    return false;
  }
}
