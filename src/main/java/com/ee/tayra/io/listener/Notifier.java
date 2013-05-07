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

import com.ee.tayra.io.reader.ReadNotifier;
import com.ee.tayra.io.writer.WriteNotifier;

public class Notifier implements ReadNotifier, WriteNotifier {

  private final CopyListener[] listeners;

  public Notifier(final CopyListener... listeners) {
    this.listeners = listeners;
  }

  @Override
  public final void notifyReadSuccess(final String document) {
    for (CopyListener listener : listeners) {
      listener.onReadSuccess(document);
    }
  }

  @Override
  public final void notifyWriteSuccess(final String document) {
    for (CopyListener listener : listeners) {
      listener.onWriteSuccess(document);
    }
  }

  @Override
  public final void notifyWriteFailure(final String document,
      final Throwable problem) {
    for (CopyListener listener : listeners) {
      listener.onWriteFailure(document, problem);
    }
  }

  @Override
  public final void notifyReadFailure(final String document,
      final Throwable problem) {
    for (CopyListener listener : listeners) {
        listener.onReadFailure(document, problem);
    }
  }

  @Override
  public final void notifyReadStart(final String document) {
    for (CopyListener listener : listeners) {
      listener.onReadStart(document);
    }
  }

  @Override
  public final void notifyWriteStart(final String document) {
    for (CopyListener listener : listeners) {
        listener.onWriteStart(document);
    }
  }
}
