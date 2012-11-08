package com.ee.beaver.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
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

  public final void copy(final OplogReplayer from, final Reader to)
    throws IOException {
    BufferedReader bufferedReader = new BufferedReader(to);
    String document = null;
    while ((document = bufferedReader.readLine()) != null) {
        from.replayDocument(document);
    }
  }
}
