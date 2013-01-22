package com.ee.tayra.io.criteria;

public interface Criterion {

    Criterion ALL = new Criterion() {
    @Override
    public boolean isSatisfiedBy(final String document) { return true; };
  };
  boolean isSatisfiedBy(String document);
}
