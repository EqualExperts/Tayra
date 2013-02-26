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

import java.util.ArrayList;
import java.util.List;

import com.ee.tayra.io.criteria.Criterion;
import com.ee.tayra.io.criteria.MultiCriteria;
import com.ee.tayra.io.criteria.TimestampCriteria;

public class CriteriaBuilder {

  private List<Criterion> criteria = new ArrayList<Criterion>();

  public void usingUntil(String timestamp, toExclude) {
    criteria.add(new TimestampCriteria(timestamp, toExclude));
  }
  
  public void usingNamespace(String namespace, toExclude) {
	  criteria.add(new NamespaceCriteria(namespace, toExclude));
	}
	

  public Criterion build(Closure closure = {}) {
    def clonedClosure = closure.clone()
    clonedClosure.resolveStrategy = Closure.DELEGATE_FIRST
    clonedClosure.delegate = this
    clonedClosure()
    if(criteria.isEmpty()) {
      return Criterion.ALL
    }
    return new MultiCriteria(criteria);
  }
}
