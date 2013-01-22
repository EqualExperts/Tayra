package com.ee.tayra.domain;

import java.util.Iterator;

public interface MongoCollectionIterator<T> extends Iterator<String> {

  void close();

}
