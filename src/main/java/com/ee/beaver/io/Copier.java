package com.ee.beaver.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import fj.Effect;
import static fj.data.List.list;

public class Copier {

  private static final CharSequence NEW_LINE =
    System.getProperty("line.separator");

  public final void copy(final OplogReader from, final Writer to)
    throws IOException {
    while (from.hasDocument()) {
      String document = from.readDocument();
      to.append(document);
      to.append(NEW_LINE);
      to.flush();
    }
  }

  public final void copy(final BufferedReader from, final OplogReplayer to,
    final CopyListener... listeners) {
    String document = null;
    try {
      while ((document = from.readLine()) != null) {
        notifyReadSuccess(document, listeners);
        try {
          to.replayDocument(document);
          notifyWriteSuccess(document, listeners);
        } catch (RuntimeException problem) {
          notifyWriteFailure(document, problem, listeners);
        }
      }
    } catch (IOException ioe) {
      notifyReadFailure(null, ioe, listeners);
    }
  }

  private void notifyReadFailure(final String document, final IOException prob,
    final CopyListener... listeners) {
    list(listeners).foreach(new Effect<CopyListener>() {
      @Override
      public void e(final CopyListener listener) {
        listener.onReadFailure(document, prob);
      }
    });
  }

  private void notifyWriteFailure(final String document, final Throwable prob,
    final CopyListener... listeners) {
    list(listeners).foreach(new Effect<CopyListener>() {
      @Override
      public void e(final CopyListener listener) {
        listener.onWriteFailure(document, prob);
      }
    });
  }

  private void notifyWriteSuccess(final String document,
    final CopyListener... listeners) {
    list(listeners).foreach(new Effect<CopyListener>() {
      @Override
      public void e(final CopyListener listener) {
        listener.onWriteSuccess(document);
      }
    });
  }

  private void notifyReadSuccess(final String document,
    final CopyListener... listeners) {
    list(listeners).foreach(new Effect<CopyListener>() {
      @Override
      public void e(final CopyListener listener) {
        listener.onReadSuccess(document);
      }
    });
  }
}
