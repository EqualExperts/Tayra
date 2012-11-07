package com.ee.beaver.domain;

import java.util.Iterator;
import com.mongodb.DBObject;

public interface MongoCollectionIterator<T> extends Iterator<DBObject> {

  void close();

}
