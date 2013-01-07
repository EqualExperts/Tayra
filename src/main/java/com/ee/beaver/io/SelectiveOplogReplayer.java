package com.ee.beaver.io;

public class SelectiveOplogReplayer implements Replayer {
  private OplogReplayer target;
  private Criteria selectCriteria;

  public SelectiveOplogReplayer(final Criteria criteria,
    final OplogReplayer target) {
    this.target = target;
    this.selectCriteria = criteria;
  }

@Override
  public boolean replayDocument(final String document) {
    if (selectCriteria.isSatisfiedBy(document)) {
      target.replayDocument(document);
      return true;
    }
    return false;
  }
}
