package com.ee.beaver.domain;

public interface MongoCollection {

  MongoCollectionIterator<String> find(String query, boolean runContinuously);

}
