/*******************************************************************************
 * Copyright (c) 2013, Equal Experts Ltd
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * Ê Êthis list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * Ê Ênotice, this list of conditions and the following disclaimer in the
 * Ê Êdocumentation and/or other materials provided with the distribution.
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
package com.ee.tayra.io;

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
