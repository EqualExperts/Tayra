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
package com.ee.tayra.domain.operation;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.Mongo;

public class Operations implements OperationsFactory {

  private final Mongo mongo;
  private final Map<String, Operation> operations;
  private SchemaOperationsFactory schemaOperationsFactory;

  public Operations(final Mongo mongo) {
    this.mongo = mongo;
    operations = new HashMap<String, Operation>();
    this.schemaOperationsFactory = new SchemaOperationsFactory(mongo);
    fillOperations();
  }

  private void fillOperations() {
    operations.put("c",
      new DefaultSchemaOperation(mongo, schemaOperationsFactory));
    operations.put("i", new InsertDocument(mongo));
    operations.put("d", new DeleteDocument(mongo));
    operations.put("u", new UpdateDocument(mongo));
  }

  public final Operation get(final String opCode) {
    if (operations.containsKey(opCode)) {
      return operations.get(opCode);
    }
    return Operation.NO_OP;
  }
}
