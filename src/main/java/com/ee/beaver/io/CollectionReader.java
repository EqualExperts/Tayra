package com.ee.beaver.io;

public interface CollectionReader {

  boolean hasDocument();

  String readDocument();

  void close();

}
