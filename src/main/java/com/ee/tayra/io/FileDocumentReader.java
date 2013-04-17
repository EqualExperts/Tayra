package com.ee.tayra.io;

import java.io.BufferedReader;
import java.io.IOException;

public class FileDocumentReader implements DocumentReader {

  private BufferedReader delegate;
  private ReadNotifier notifier;

  public FileDocumentReader(final BufferedReader reader) {
    this.delegate = reader;
    notifier = ReadNotifier.NONE;
  }

  public final void setNotifier(final ReadNotifier notifier) {
    this.notifier = notifier;
  }

  @Override
  public final String readLine() {
    try {
      String document = delegate.readLine();
      if (document != null) {
        notifier.notifyReadSuccess(document);
      }
      return document;
    } catch (IOException ioe) {
      notifier.notifyReadFailure(null, ioe);
    }
    return null;
  }

}
