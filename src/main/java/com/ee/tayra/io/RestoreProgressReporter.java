package com.ee.tayra.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class RestoreProgressReporter extends ProgressReporter {

  private final Writer exceptionDocsWriter;
  private int exceptionDocuments = 0;

  public RestoreProgressReporter(final Writer exceptionDocsWriter,
      final PrintWriter progressWriter) {
    super(progressWriter);
    this.exceptionDocsWriter = exceptionDocsWriter;
  }

  @Override
  public final void onWriteFailure(final String document,
      final Throwable problem) {
    if (document == null) {
      problem.printStackTrace(getProgressWriter());
      return;
    }

    exceptionDocuments++;
    if (exceptionDocsWriter == null) {
      getProgressWriter().printf("===> Unable to Write Document(s) %s",
          problem.getMessage());
      return;
    }

    try {
      exceptionDocsWriter.append(document);
      exceptionDocsWriter.append(NEW_LINE);
      exceptionDocsWriter.flush();
    } catch (IOException e) {
      getProgressWriter().printf(
          "===> Unable to Write Exceptioning Document(s) %s",
          e.getMessage());
      problem.printStackTrace(getProgressWriter());
    }
  }

  public final int getExceptionDocuments() {
    return exceptionDocuments;
  }

  @Override
  public final void summarizeTo(final Writer writer) {
    try {
      writeln(writer, "");
      writeln(writer, "---------------------------------");
      writeln(writer, "             Summary             ");
      writeln(writer, "---------------------------------");
      writeln(writer, "Total Documents Read: " + super.getDocumentsRead());
      writeln(writer, "Documents Written: " + super.getDocumentsWritten());
      writeln(writer, "Exception Documents: " + exceptionDocuments);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
