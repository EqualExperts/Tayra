package com.ee.beaver.io;

import java.io.IOException;
import java.io.Writer;


class RestoreListener implements CopyListener {

  private Writer exceptionsWriter;

  public RestoreListener(final Writer exceptionsWriter) {
    this.exceptionsWriter = exceptionsWriter;
  }
  @Override
  public final void onReadSuccess(final String document) {
  }

  @Override
  public final void onWriteSuccess(final String document) {
  }

  @Override
  public final void onWriteFailure(final String document,
    final Throwable problem) {
    try {
      exceptionsWriter.append(document);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public final void onReadFailure(final String document,
    final Throwable problem) {
  }
}
