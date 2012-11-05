package com.ee.beaver;

import java.util.Iterator;

import com.mongodb.DBObject;

public interface MongoCollection {

  Iterator<DBObject> find();

}
