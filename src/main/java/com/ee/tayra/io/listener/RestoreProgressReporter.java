/*******************************************************************************
 * Copyright (c) 2013, Equal Experts Ltd
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation
 * are those of the authors and should not be interpreted as representing
 * official policies, either expressed or implied, of the Tayra Project.
 ******************************************************************************/
package com.ee.tayra.io.listener;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class RestoreProgressReporter extends ProgressReporter {

  private static final String EXCEPTION_DETAILS_FILE = "exception.details";
  private final Writer exceptionDocsWriter;
  private int exceptionDocuments = 0;
  private final Writer exceptionDetailsWriter;

  public RestoreProgressReporter(final Writer exceptionDocsWriter,
  final Writer exceptionDetailsWriter, final PrintWriter progressWriter) {
    super(progressWriter);
    this.exceptionDocsWriter = exceptionDocsWriter;
    this.exceptionDetailsWriter = exceptionDetailsWriter;
  }

  @Override
  public final void onWriteFailure(final String document,
      final Throwable problem) {
    if (document == null) {
      problem.printStackTrace(getProgressWriter());
      return;
    }

    exceptionDocuments++;
    if (exceptionDocsWriter == null || exceptionDetailsWriter == null) {
        getProgressWriter().printf("===> Unable to Write Document(s) %s",
                problem.getMessage());
      return;
    }

    try {
      writeLine(exceptionDocsWriter, document);
      writeLine(exceptionDetailsWriter, document);
      writeLine(exceptionDetailsWriter, problem.getMessage());
      writeLine(exceptionDetailsWriter, "");
    } catch (IOException e) {
      getProgressWriter().printf(
          "===> Unable to Write Exceptioning Document(s) %s",
          e.getMessage());
      problem.printStackTrace(getProgressWriter());
    }
  }

  private void writeLine(final Writer writer, final String data)
  throws IOException {
    writer.append(data);
    writer.append(NEW_LINE);
    writer.flush();
  }

  public final int getExceptionDocuments() {
    return exceptionDocuments;
  }

  @Override
  public final void summarizeTo(final Writer writer) {
    try {
      super.summarizeTo(writer);
      writeln(writer, "Exception Documents: " + exceptionDocuments);
      if (exceptionDocuments > 0) {
         writeln(writer, "");
         writeln(writer, "NOTE: For further reasons for "
           + "exceptioning documents,");
         writeln(writer, "You may want to refer to the file: "
           + EXCEPTION_DETAILS_FILE);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public final void onReadStart(final String document) {
    if (document.isEmpty()) {
      getProgressWriter().printf(
            "%s Wrote %d Document(s)...\r",
            spinner[documentsWritten % spinner.length], documentsWritten);
    }
  }
}
