package com.ee.beaver.domain;

public interface MongoCollection {

  MongoCollectionIterator<String> find();

  MongoCollectionIterator<String> find(String query);

  MongoCollectionIterator<String> find(String query, boolean runContinuously);
}
