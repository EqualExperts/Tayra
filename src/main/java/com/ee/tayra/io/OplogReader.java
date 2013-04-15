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

import com.ee.tayra.domain.MongoCollection;
import com.ee.tayra.domain.MongoCollectionIterator;

public class OplogReader implements CollectionReader {

  private MongoCollectionIterator<String> iterator;
  private ReadNotifier notifier;

  public OplogReader(final MongoCollection collection,
    final String fromDocument, final boolean tailable) {
    iterator = collection.find(fromDocument, tailable);
    notifier = ReadNotifier.NONE;
  }

  public final void setNotifier(final ReadNotifier notifier) {
    this.notifier = notifier;
  }

  @Override
  public final boolean hasDocument() {
    if (iterator == null) {
        throw new ReaderAlreadyClosed("Reader Already Closed");
    }
    boolean hasNext = false;
    try {
      notifier.notifyReadStart("");
      hasNext = iterator.hasNext();
    } catch (Exception e) {
      notifier.notifyReadFailure(null, e);
    }
    return hasNext;
  }

  @Override
  public final String readDocument() {
    if (iterator == null) {
      throw new ReaderAlreadyClosed("Reader Already Closed");
    }
    String document = null;
    try {
      document = iterator.next();
      notifier.notifyReadSuccess(document);
    } catch (Exception e) {
      notifier.notifyReadFailure(null, e);
    }
    return document;
  }

  public final void close() {
    if (iterator == null) {
      throw new ReaderAlreadyClosed("Reader Already Closed");
    }
    iterator.close();
    iterator = null;
  }
}
