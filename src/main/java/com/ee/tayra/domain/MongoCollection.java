package com.ee.tayra.domain;

public interface MongoCollection {

  MongoCollectionIterator<String> find(String query, boolean runContinuously);

}
