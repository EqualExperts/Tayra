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
package com.ee.tayra.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class Copier {
  public final void copy(final CollectionReader from, final DocumentWriter to) {
    while (from.hasDocument()) {
      String document = from.readDocument();
      if (document.isEmpty()) {
        continue;
      }
      to.writeDocument(document);
    }
  }

    private Notifier createNotifier(final CopyListener[] listeners) {
        return new Notifier(listeners);
    }

    public final void copy(final Reader reader, final Replayer to,
      final CopyListener... listeners) {
    Notifier notifier = createNotifier(listeners);
    BufferedReader from = createBufferedReader(reader);
    String document = null;
    try {
      while ((document = from.readLine()) != null) {
        notifier.notifyReadSuccess(document);
        try {
          notifier.notifyWriteStart(document);
          if (to.replay(document)) {
            notifier.notifyWriteSuccess(document);
          }
        } catch (RuntimeException problem) {
          notifier.notifyWriteFailure(document, problem);
        }
      }
    } catch (IOException ioe) {
      notifier.notifyReadFailure(null, ioe);
    }
  }

  BufferedReader createBufferedReader(final Reader reader) {
    return new BufferedReader(reader);
  }
}
