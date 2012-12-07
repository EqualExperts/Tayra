package com.ee.beaver.io;

import com.ee.beaver.domain.operation.Operation;
import com.ee.beaver.domain.operation.OperationsFactory;

public class OplogReplayer {

  private final OperationsFactory operations;

  public OplogReplayer(final OperationsFactory operations) {
    this.operations = operations;
  }

  public void replayDocument(final String document) {
    final String operationCode = extractOpcode(document);
    Operation operation = operations.get(operationCode);
    operation.execute(document);
  }

  private String extractOpcode(final String document) {
    int opcodeStartIndex = document.indexOf("op") - 1;
    int opcodeEndIndex = document.indexOf(",", opcodeStartIndex);
    String opcodeSpec = document.substring(opcodeStartIndex, opcodeEndIndex);
    String quotedOpcode = opcodeSpec.split(":")[1];
    return quotedOpcode.replaceAll("\"", "").trim();
  }
}

