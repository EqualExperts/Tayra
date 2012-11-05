package com.ee.beaver.domain;

import java.util.Iterator;

import com.mongodb.DBObject;

public interface MongoCollection {

  Iterator<DBObject> find();

}