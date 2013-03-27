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

import java.util.Arrays;
import java.util.List;

public class NamespaceCriterion implements Criterion {
  private static final String COMMA = ",";
  private static final String BLANK = "";
  private List<String> incomingNamespaces;

  public NamespaceCriterion(final String ns) {
    this.incomingNamespaces = getNamespaces(ns);
  }

  private List<String> getNamespaces(final String ns) {
    return Arrays.asList(ns.split(COMMA));
  }

  @Override
  public final boolean isSatisfiedBy(final String document) {
    String documentNamespace = getNamespace(document);
    if (BLANK.equals(documentNamespace)) {
      return false;
    }
      return isSatisfiedByEachCriteria(document, documentNamespace);
  }

  private boolean isSatisfiedByEachCriteria(final String document,
        final String documentNamespace) {
    OperationType type = OperationType.create(documentNamespace);
    for (String incomingNS : incomingNamespaces) {
      if (type.match(document, documentNamespace, incomingNS)) {
        return true;
      }
    }
    return false;
  }

  private String getNamespace(final String document) {
    int startIndex = document.indexOf("ns") - 1;
    int endIndex = document.indexOf(COMMA, startIndex);
    String namespace = document.substring(startIndex, endIndex).split(":") [1];
    return namespace.replaceAll("\"", BLANK).trim();
  }
}
