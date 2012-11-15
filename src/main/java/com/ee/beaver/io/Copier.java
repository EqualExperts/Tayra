package com.ee.beaver.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;


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
    Notifier notifier = new Notifier(listeners);
    String document = null;
    try {
      while ((document = from.readLine()) != null) {
        notifier.notifyReadSuccess(document);
        try {
          to.replayDocument(document);
          notifier.notifyWriteSuccess(document);
        } catch (RuntimeException problem) {
          notifier.notifyWriteFailure(document, problem);
        }
      }
    } catch (IOException ioe) {
      notifier.notifyReadFailure(null, ioe);
    }
  }
}
