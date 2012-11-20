package com.ee.beaver.io;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class GenericWriter extends Writer {

  private final Writer timestampWriter;
  private final Writer delegate;
  private static final CharSequence NEW_LINE =
    System.getProperty("line.separator");

  public GenericWriter(final Writer timestampWriter, final Writer delegate) {
    this.timestampWriter = timestampWriter;
    this.delegate = delegate;
  }

  @Override
  public final void write(final String document, final int off, final int len)
    throws IOException {
    doBackupOf(document, off, len);
    storeTimestampOf(document);
  }

  private void storeTimestampOf(final String document) throws IOException {
    String ts = extractTimestamp(document);
    ((StringWriter) timestampWriter).getBuffer().delete(0, ts.length());
    timestampWriter.write(ts, 0, ts.length());
}

  private void doBackupOf(final String document, final int off, final int len)
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
