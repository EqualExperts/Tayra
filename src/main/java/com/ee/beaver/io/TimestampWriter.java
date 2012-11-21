package com.ee.beaver.io;

import java.io.IOException;
import java.io.Writer;

public class TimestampWriter extends Writer {

  private String timestamp = "";
  private final Writer delegate;
  private static final CharSequence NEW_LINE =
    System.getProperty("line.separator");

  public TimestampWriter(final Writer delegate) {
    this.delegate = delegate;
  }

  @Override
  public final void write(final String document, final int off, final int len)
    throws IOException {
    writeToDelegate(document, off, len);
    storeTimestampFrom(document);
  }

  public final String getTimestamp() {
    return timestamp;
  }

  private void storeTimestampFrom(final String document) throws IOException {
    timestamp = extractTimestamp(document);
}

  private void writeToDelegate(final String document, final int off,
    final int len)
    throws IOException {
    delegate.append(document, off, len);
    delegate.append(NEW_LINE);
    delegate.flush();
  }

  private String extractTimestamp(final String data) {
    String oplogDocument = new String(data);
    String ts = oplogDocument.substring(oplogDocument.indexOf("\"ts\" :"),
      oplogDocument.indexOf("}") + 1);
    return ts;
  }

  @Override
  public void flush() throws IOException {
  }

  @Override
  public void close() throws IOException {
  }

@Override
public void write(final char[] arg0, final int arg1, final int arg2)
  throws IOException {
}

}
