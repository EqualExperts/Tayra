package com.ee.beaver;

import java.util.Iterator;

public interface OplogCollection {

  Iterator<OplogDocument> find();

}
