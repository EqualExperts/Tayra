package com.ee.tayra.io;

import com.mongodb.MongoException;

public class MongoExceptionBubbler implements CopyListener {

  public void onReadSuccess(String document) {
  }

  public void onWriteSuccess(String document) {
  }

  public void onWriteFailure(String document, Throwable problem) {
  }

  public void onReadFailure(String document, Throwable problem) {
    if (problem instanceof MongoException) {
      throw (MongoException)problem;
    }
  }
}
