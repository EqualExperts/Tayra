package com.ee.tayra.io;

import com.mongodb.MongoException;

public class MongoExceptionRelayer implements CopyListener {

  public void onReadSuccess(String document) {
  }

  public void onWriteSuccess(String document) {
  }

  public void onWriteFailure(String document, Throwable problem) {
  }

  public void onReadFailure(String document, Throwable problem)
      throws Throwable {
    if (problem instanceof MongoException) {
      throw problem;
    }
  }
}
