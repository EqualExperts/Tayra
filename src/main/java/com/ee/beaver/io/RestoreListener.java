package com.ee.beaver.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

class RestoreListener implements CopyListener {

  private final Writer exceptionsWriter;
  private final PrintWriter progressWriter;
  private final String [] spinner = new String [] {"/", "-", "\\", "|"};
  private static final CharSequence NEW_LINE =
    System.getProperty("line.separator");
  private int documentsWritten = 0;
  private int documentsRead = 0;
  private int exceptionDocuments = 0;

  public RestoreListener(final Writer exceptionsWriter,
    final PrintWriter progressWriter) {
    this.exceptionsWriter = exceptionsWriter;
    this.progressWriter = progressWriter;
  }

  @Override
  public final void onReadSuccess(final String document) {
    documentsRead++;
  }

  @Override
  public final void onWriteSuccess(final String document) {
    ++documentsWritten;
    progressWriter.printf("%s Restored %d document(s)\r",
      spinner[documentsWritten % spinner.length], documentsWritten);
  }

  @Override
  public final void onWriteFailure(final String document,
    final Throwable problem) {
    if (document == null) {
      problem.printStackTrace(progressWriter);
      return;
    }

    try {
      exceptionsWriter.append(document);
      exceptionsWriter.append(NEW_LINE);
      exceptionsWriter.flush();
      exceptionDocuments++;
    } catch (IOException e) {
      problem.printStackTrace(progressWriter);
    }
  }

  @Override
  public final void onReadFailure(final String document,
    final Throwable problem) {
  }

  public int getDocumentsRead() {
    return documentsRead;
  }

  public int getExceptionDocuments() {
    return exceptionDocuments;
  }

  public int getDocumentsWritten() {
    return documentsWritten;
  }
}
