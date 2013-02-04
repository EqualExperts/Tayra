package com.ee.tayra.io;

import java.io.Writer;

public class EmptyProgressReporter implements CopyListener, Reporter {

  @Override
  public final void onReadSuccess(final String document) {
  }

  @Override
  public final void onWriteSuccess(final String document) {
  }

  @Override
  public final void onWriteFailure(final String document,
      final Throwable problem) {
  }

  @Override
  public final void onReadFailure(final String document,
      final Throwable problem) {
  }

  @Override
  public void summarizeTo(final Writer writer) {
  }
}
