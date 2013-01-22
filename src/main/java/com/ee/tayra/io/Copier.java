package com.ee.tayra.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class Copier {

  public final void copy(final CollectionReader from, final Writer to,
    final CopyListener... listeners) {
    Notifier notifier = new Notifier(listeners);
    try {
      while (from.hasDocument()) {
        String document = from.readDocument();
        notifier.notifyReadSuccess(document);
        try {
          to.append(document);
          to.flush();
          notifier.notifyWriteSuccess(document);
        } catch (IOException problem) {
          notifier.notifyWriteFailure(document, problem);
        }
      }
    } catch (RuntimeException problem) {
      notifier.notifyReadFailure(null, problem);
    }
  }

  public final void copy(final Reader reader, final Replayer to,
    final CopyListener... listeners) {
    Notifier notifier = new Notifier(listeners);
    BufferedReader from = createBufferedReader(reader);
    String document = null;
    try {
      while ((document = from.readLine()) != null) {
        notifier.notifyReadSuccess(document);
        try {
          if (to.replay(document)) {
            notifier.notifyWriteSuccess(document);
          }
        } catch (RuntimeException problem) {
          notifier.notifyWriteFailure(document, problem);
        }
      }
    } catch (IOException ioe) {
      notifier.notifyReadFailure(null, ioe);
    }
  }

  BufferedReader createBufferedReader(final Reader reader) {
    return new BufferedReader(reader);
  }
}
