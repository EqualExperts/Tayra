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
package com.ee.tayra.io.listener.timestamp;

import java.io.IOException;

import com.ee.tayra.io.listener.CopyListener;

public class TimestampRecorder implements CopyListener {

  private final StringBuilder documentTimestamp; //for performance
  private String lastDocumentTimestamp;
  private final TimestampRepository timestampRepository;

  public TimestampRecorder(final TimestampRepository timestampRepository)
  throws IOException {
    this.timestampRepository = timestampRepository;
    documentTimestamp = new StringBuilder();
    lastDocumentTimestamp = timestampRepository.retrieve();
  }

  public final String getDocumentTimestamp() {
    return documentTimestamp.toString();
  }

  private void registerTimestampFrom(final String document) {
    if (document.contains("\"ts\"")) {
      documentTimestamp.delete(0, documentTimestamp.length());
      documentTimestamp.append("{ " + timestampFrom(document) + " }");
    }
  }

  private String timestampFrom(final String data) {
    return data.substring(data.indexOf("\"ts\""), data.indexOf("}") + 2);
  }

  public final void stop() throws IOException {
    if (documentTimestamp.length() > 0) {
      timestampRepository.save(getDocumentTimestamp());
    }
  }

  public final String getLastDocumentTimestamp() {
    return lastDocumentTimestamp;
  }

  @Override
  public final void onReadSuccess(final String document) {
    registerTimestampFrom(document);
  }

  @Override
  public final void onWriteSuccess(final String document) {
    lastDocumentTimestamp = getDocumentTimestamp();
  }

  @Override
  public final void onWriteFailure(
    final String document, final Throwable problem) {
  }

  @Override
  public final void onReadFailure(
    final String document, final Throwable problem) {
  }

  @Override
  public final void onReadStart(final String document) {
  }

  @Override
  public final void onWriteStart(final String document) {
  }
}
