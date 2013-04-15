package com.ee.tayra.io;

public interface ReadNotifier {
  void notifyReadSuccess(String document);

  void notifyReadFailure(String document, Throwable problem);

  void notifyReadStart(String document);

  ReadNotifier NONE = new  ReadNotifier() {
      @Override
      public final void notifyReadSuccess(final String document) {
      }

      @Override
      public final void notifyReadFailure(
      final String document, final Throwable problem) {
      }

      @Override
      public final void notifyReadStart(final String document) {
      }
  };
}
