package com.ee.beaver.io;


public interface Criterion {

    Criterion ALL = new Criterion() {
    @Override
    public boolean isSatisfiedBy(final String document) { return true; };
  };
  boolean isSatisfiedBy(String document);
}
