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

public enum DDLStrategy {

CREATECOLLECTION {
  @Override
  public String extractCollection(final String payload) {
    String create = "\"create\" :";
    int startIndex = payload.indexOf(create) - 1;
    int endIndex = payload.indexOf("}", startIndex);
    if (payload.contains("capped")) {
      endIndex = payload.indexOf(",", startIndex);
    }
  return payload.substring(startIndex, endIndex)
    .split(":") [1].replaceAll("\"", BLANK).trim();
  }
},

DROPCOLLECTION {
  @Override
  public String extractCollection(final String payload) {
    String drop = "\"drop\" :";
    int startIndex = payload.indexOf(drop) - 1;
    int endIndex = payload.indexOf("}", startIndex);
      return payload.substring(startIndex, endIndex)
        .split(":") [1].replaceAll("\"", BLANK).trim();
  }
},

CREATEINDEX {
  @Override
  public String extractCollection(final String payload) {
    String ns = "\"ns\" :";
    int startIndex = payload.indexOf(ns) - 1;
    int endIndex = payload.indexOf(",", startIndex);
    String namespace = payload.substring(startIndex, endIndex)
      .split(":") [1].replaceAll("\"", BLANK).trim();
    return namespace.split("\\.", 2) [1];
  }
},

DROPINDEX {
  @Override
  public String extractCollection(final String payload) {
    String deleteIndexes = "\"deleteIndexes\" :";
    int startIndex = payload.indexOf(deleteIndexes) - 1;
    int endIndex = payload.indexOf(",", startIndex);
    return payload.substring(startIndex, endIndex)
      .split(":") [1].replaceAll("\"", BLANK).trim();
  }
},

NO_STRATEGY {
  @Override
  public String extractCollection(final String payload) {
    throw new DDLOperationNotSupported("Cannot recognize operation "
      + "in the payload " + payload);
  }
};

  private static final String DELETE_INDEXES = ".*\"deleteIndexes\".*";
  private static final String NS = ".*\"ns\".*";
  private static final String DROP = ".*\"drop\".*";
  private static final String CREATE = ".*\"create\".*";
  private static final String BLANK = "";

  public boolean matchCollection(final String incomingCollectionName,
    final String payload) {
    String collection = extractCollection(payload);
    return incomingCollectionName.equals(collection);
  }

  abstract String extractCollection(String payload);

  public static DDLStrategy create(final String payload) {
    if (payload.matches(CREATE)) {
      return DDLStrategy.CREATECOLLECTION;
    }
    if (payload.matches(DROP)) {
      return DDLStrategy.DROPCOLLECTION;
    }
    if (payload.matches(NS)) {
      return DDLStrategy.CREATEINDEX;
    }
    if (payload.matches(DELETE_INDEXES)) {
      return DDLStrategy.DROPINDEX;
    }
      return DDLStrategy.NO_STRATEGY;
  }
}
