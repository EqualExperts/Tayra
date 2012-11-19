package com.ee.beaver.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

class ProgressReporter implements CopyListener {

  private final Writer exceptionDocsWriter;
  private final PrintWriter progressWriter;
  private final String [] spinner = new String [] {"/", "-", "\\", "|"};
  private static final CharSequence NEW_LINE =
    System.getProperty("line.separator");
  private int documentsWritten = 0;
  private int documentsRead = 0;
  private int exceptionDocuments = 0;

  public ProgressReporter(final Writer exceptionDocsWriter,
    final PrintWriter progressWriter) {
    this.exceptionDocsWriter = exceptionDocsWriter;
    this.progressWriter = progressWriter;
  }

  @Override
  public final void onReadSuccess(final String document) {
    documentsRead++;
  }

  @Override
  public final void onWriteSuccess(final String document) {
    ++documentsWritten;
    progressWriter.printf("%s Wrote %d Document(s)\r",
      spinner[documentsWritten % spinner.length], documentsWritten);
  }

  @Override
  public final void onWriteFailure(final String document,
    final Throwable problem) {
    if (document == null) {
      problem.printStackTrace(progressWriter);
      return;
    }

    exceptionDocuments++;
    if (exceptionDocsWriter == null) {
    progressWriter.printf("===> Unable to Write Document(s) %s",
      problem.getMessage());
    return;
    }

    try {
      exceptionDocsWriter.append(document);
      exceptionDocsWriter.append(NEW_LINE);
      exceptionDocsWriter.flush();
    } catch (IOException e) {
      progressWriter.printf("===> Unable to Write Exceptioning Document(s) %s",
        e.getMessage());
      problem.printStackTrace(progressWriter);
    }
  }

  @Override
  public final void onReadFailure(final String document,
    final Throwable problem) {
    progressWriter.printf("===> Unable to Read Documents: %s\r",
      problem.getMessage());
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
