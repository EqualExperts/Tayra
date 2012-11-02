package com.ee.beaver;

import java.util.Iterator;

public interface MongoCollection {

  Iterator<OplogDocument> find();

}
