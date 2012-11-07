package com.ee.beaver.io;

import java.io.IOException;
import java.io.Writer;

public class Copier {

  private static final CharSequence NEW_LINE =
    System.getProperty("line.separator");

  public Copier() {
  }

  public final void copy(final OplogReader fromReader, final Writer toWriter)
    throws IOException {
    while (fromReader.hasDocument()) {
      String document = fromReader.readDocument();
      toWriter.append(document);
      toWriter.append(NEW_LINE);
    }
    toWriter.flush();
  }
}
