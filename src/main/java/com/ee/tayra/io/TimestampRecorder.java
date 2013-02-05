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
import java.io.Writer;

public class TimestampRecorder extends Writer {

  private final StringBuilder timestamp;
  private final Writer delegate;

  public TimestampRecorder(final Writer delegate) {
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
    if (document.contains("\"ts\"")) {
      timestamp.delete(0, timestamp.length());
      timestamp.append("{ " + timestampFrom(document) + " }");
    }
}

  private String timestampFrom(final String data) {
    return data.substring(data.indexOf("\"ts\""), data.indexOf("}") + 1);
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
