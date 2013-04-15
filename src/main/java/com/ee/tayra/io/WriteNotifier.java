package com.ee.tayra.io;

public interface WriteNotifier {
  void notifyWriteSuccess(String document);

  void notifyWriteFailure(String document, Throwable problem);

  void notifyWriteStart(String document);

  WriteNotifier NONE = new WriteNotifier() {
    @Override
    public final void notifyWriteSuccess(final String document) {
    }

    @Override
    public final void notifyWriteFailure(
    final String document, final Throwable problem) {
    }

    @Override
    public final void notifyWriteStart(final String document) {
    }
  };
}
