package com.ee.beaver.io;

import java.io.IOException;
import java.io.Writer;

public class TimestampWriter extends Writer {

  private final StringBuilder timestamp;
  private final Writer delegate;

  public TimestampWriter(final Writer delegate) {
    timestamp = new StringBuilder();
    this.delegate = delegate;
  }

  @Override
  public final void write(final String document, final int off, final int len)
    throws IOException {
    delegate.append(document, off, len);
    registerTimestampFrom(document);
  }

  public final String getTimestamp() {
    return timestamp.toString();
  }

  private void registerTimestampFrom(final String document) throws IOException {
    timestamp.delete(0, timestamp.length());
    timestamp.append(timestampFrom(document));
}

  private String timestampFrom(final String data) {
    return data.substring(data.indexOf("\"ts\" :"), data.indexOf("}") + 1);
  }

  @Override
  public final void flush() throws IOException {
    delegate.flush();
  }

  @Override
  public final void close() throws IOException {
    delegate.close();
  }

  @Override
  public final void write(final char[] data, final int off, final int len)
  throws IOException {
    delegate.write(data, off, len);
  }

}
