package com.ee.tayra.io;

import com.mongodb.MongoException;

public class MongoExceptionBubbler implements CopyListener {

  public final void onReadSuccess(final String document) {
  }

  public final void onWriteSuccess(final String document) {
  }

  public final void onWriteFailure(final String document,
      final Throwable problem) {
  }

  public final void onReadFailure(final String document,
      final Throwable problem) {
    if (problem instanceof MongoException) {
      throw (MongoException) problem;
    }
  }

  @Override
  public void onReadStart(final String document) {
  }

  @Override
  public void onWriteStart(final String document) {
  }

}
