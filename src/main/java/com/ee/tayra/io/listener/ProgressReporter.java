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
import java.util.Date;


class ProgressReporter implements CopyListener, Reporter {

  private static final int MS_TO_SECONDS = 1000;
  private static final int PAD_BY = 2;
  protected static final String NEW_LINE = System
      .getProperty("line.separator");
  private final PrintWriter progressWriter;
  private final String[] spinner = new String[] {"/", "-", "\\", "|"};
  private int documentsWritten = 0;
  private int documentsRead = 0;
  private long startTime;

  public ProgressReporter(final PrintWriter progressWriter) {
    this.progressWriter = progressWriter;
    startTime = new Date().getTime();
  }

  @Override
  public final void onReadSuccess(final String document) {
    documentsRead++;
  }

  @Override
  public final void onWriteSuccess(final String document) {
    ++documentsWritten;
    getProgressWriter().printf(
        "%s Wrote %d Document(s)                                         \r",
        spinner[documentsWritten % spinner.length], documentsWritten);
  }

  @Override
  public void onWriteFailure(final String document, final Throwable problem) {
    getProgressWriter().printf("===> Unable to Write Document(s) %s",
        problem.getMessage());
  }

  @Override
  public final void onReadFailure(final String document,
      final Throwable problem) {
    getProgressWriter().printf("===> Unable to Read Documents: %s\r",
        problem.getMessage());
  }

  public final int getDocumentsRead() {
    return documentsRead;
  }

  public final int getDocumentsWritten() {
    return documentsWritten;
  }

  @Override
  public void summarizeTo(final Writer writer) {
    try {
      writeln(writer, "");
      writeln(writer, "Completed in : "
          + (new Date().getTime() - startTime) / MS_TO_SECONDS
          + " seconds");
      writeln(writer, "---------------------------------");
      writeln(writer, "             Summary             ");
      writeln(writer, "---------------------------------");
      writeln(writer, "Total Documents Read: " + documentsRead);
      writeln(writer, "Documents Written: " + documentsWritten);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public final void writeln(final Writer writer, final String data)
      throws IOException {
    int len = data.length() + PAD_BY;
    String result = String.format("%" + len + "s", data);
    writer.write(result);
    writer.write(NEW_LINE);
    writer.flush();
  }

  public PrintWriter getProgressWriter() {
    return progressWriter;
  }

  @Override
  public final void writeStartTimeTo(final Writer writer) {
    try {
      writeln(writer, "Process started on : " + new Date(startTime));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onReadStart(final String document) {
    if (document.isEmpty()) {
      getProgressWriter().printf(
            "%s Wrote %d Document(s). Waiting for documents...\r",
            spinner[documentsWritten % spinner.length], documentsWritten);
    }
  }

  @Override
  public void onWriteStart(final String document) {
    getProgressWriter().printf("%s Writing Document....\r",
        spinner[documentsWritten % spinner.length]);
  }

}
