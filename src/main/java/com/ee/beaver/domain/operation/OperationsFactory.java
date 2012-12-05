package com.ee.beaver.domain.operation;

public interface OperationsFactory {

  Operation get(final String opCode);

}
