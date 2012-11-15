package com.ee.beaver.io;

public interface CopyListener {

  void onReadSuccess(String document);

  void onWriteSuccess(String document);

  void onWriteFailure(String document, Throwable problem);

  void onReadFailure(String document, Throwable problem);
}
