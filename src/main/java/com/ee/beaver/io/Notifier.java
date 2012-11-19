package com.ee.beaver.io;

import static fj.data.List.list;

import fj.Effect;

public class Notifier {

  private final CopyListener[] listeners;

  public Notifier(final CopyListener... listeners) {
    this.listeners = listeners;
  }

  public final void notifyReadSuccess(final String document) {
    list(listeners).foreach(new Effect<CopyListener>() {
      @Override
      public void e(final CopyListener listener) {
        listener.onReadSuccess(document);
      }
    });
  }

  public final void notifyWriteSuccess(final String document) {
    list(listeners).foreach(new Effect<CopyListener>() {
      @Override
      public void e(final CopyListener listener) {
        listener.onWriteSuccess(document);
      }
    });
  }

  public final void notifyWriteFailure(final String document,
    final Throwable problem) {
    list(listeners).foreach(new Effect<CopyListener>() {
      @Override
      public void e(final CopyListener listener) {
        listener.onWriteFailure(document, problem);
      }
    });
  }

  public final void notifyReadFailure(final String document,
  final Throwable problem) {
    list(listeners).foreach(new Effect<CopyListener>() {
      @Override
      public void e(final CopyListener listener) {
        listener.onReadFailure(document, problem);
      }
    });
  }

}
