package com.ee.beaver.domain;

import com.mongodb.DBObject;

public interface MongoCollection {

  MongoCollectionIterator<DBObject> find();

  MongoCollectionIterator<DBObject> find(boolean runContinuously);

}
