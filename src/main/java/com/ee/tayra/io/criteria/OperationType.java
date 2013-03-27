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
package com.ee.tayra.io.criteria;

public abstract class OperationType {

  private static final String DOT = "\\.";
  private static final String SYSTEM_INDEXES = "system.indexes";
  private static final String CMD = "$cmd";
  static final String BLANK = "";
  public static OperationType create(final String documentNamespace) {
    if (isDDL(documentNamespace)) {
      return new DDLOperation();
    }
    return new DMLOperation();
  }

  private static boolean isDDL(final String documentNamespace) {
    String command = documentNamespace.split(DOT, 2)[1];
    return ((CMD.equals(command)) || (SYSTEM_INDEXES.equals(command)));
  }

  final String extractCollectionName(final String incomingNS) {
    try {
      return incomingNS.split(DOT, 2)[1];
    } catch (ArrayIndexOutOfBoundsException e) {
      return BLANK;
    }
  }

  final boolean matchDbName(final String document,
    final String documentNamespace, final String incomingNs) {
      String dbName = extractDbName(incomingNs);
      String documentDb = documentNamespace.split(DOT, 2)[0];
      if (dbName.equals(documentDb)) {
        return true;
      }
      return false;
  }

  final boolean matchDbAndCollectionName(final String document,
    final String documentNamespace, final String incomingNs) {
      if (incomingNs.equals(documentNamespace)) {
        return true;
      }
        return false;
  }

  private String extractDbName(final String incomingNS) {
    return incomingNS.split(DOT, 2)[0];
  }

  public abstract boolean match(final String document,
    final String documentNamespace, final String incomingNs);
}
