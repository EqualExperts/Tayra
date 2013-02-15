package com.ee.tayra.io.criteria;

public class NamespaceCriteria implements Criterion {
  private static final String BLANK = "";
  private String incomingNs;
  public NamespaceCriteria(final String ns) {
    this.incomingNs = ns;
  }

@Override
  public boolean isSatisfiedBy(final String document) {
    OperationStrategy strategy = OperationStrategy.DML;
    String documentNamespace = getNamespace(document);
    if (documentNamespace == BLANK) {
      return false;
    }
    if (isDDL(documentNamespace)) {

      strategy = OperationStrategy.DDL;
      return strategy.match(document, documentNamespace, incomingNs);
    }
     return strategy.match(document, documentNamespace, incomingNs);
  }

private String getNamespace(final String document) {
  int startIndex = document.indexOf("ns") - 1;
  int endIndex = document.indexOf(",", startIndex);
  String namespace = document.substring(startIndex, endIndex)
      .split(":") [1];
  return namespace.replaceAll("\"", BLANK).trim();
}

private boolean isDDL(final String documentNamespace) {
  String command = documentNamespace.split("\\.", 2)[1];
  return (("$cmd".equals(command)) || ("system.indexes".equals(command)));
}

}
