package com.ee.tayra.io;

import com.ee.tayra.domain.operation.Operation;
import com.ee.tayra.domain.operation.OperationsFactory;

public class OplogReplayer implements Replayer {

  private final OperationsFactory operations;

  public OplogReplayer(final OperationsFactory operations) {
    this.operations = operations;
  }

  public boolean replay(final String document) {
    final String operationCode = extractOpcode(document);
    Operation operation = operations.get(operationCode);
    operation.execute(document);
    return true;
  }

  private String extractOpcode(final String document) {
    int opcodeStartIndex = document.indexOf("op") - 1;
    int opcodeEndIndex = document.indexOf(",", opcodeStartIndex);
    String opcodeSpec = document.substring(opcodeStartIndex, opcodeEndIndex);
    String quotedOpcode = opcodeSpec.split(":")[1];
    return quotedOpcode.replaceAll("\"", "").trim();
  }
}

