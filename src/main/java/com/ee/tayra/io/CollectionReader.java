package com.ee.tayra.io;

public interface CollectionReader {

  boolean hasDocument();

  String readDocument();

  void close();

}
