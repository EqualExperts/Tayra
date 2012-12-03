package com.ee.beaver.domain;

import java.util.Iterator;

public interface MongoCollectionIterator<T> extends Iterator<String> {

  void close();

}
