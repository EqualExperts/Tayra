package com.ee.tayra.io.criteria;

public class NamespaceCriteria implements Criterion {
  private static final String BLANK = "";
  private String incomingNs;
  public NamespaceCriteria(final String ns) {
    this.incomingNs = ns;
  }

@Override
  public boolean isSatisfiedBy(final String document) {
    String documentNamespace = getNamespace(document);
    if (BLANK.equals(documentNamespace)) {
      return false;
    }
    OperationType type = OperationType.create(documentNamespace);
    return type.match(document, documentNamespace, incomingNs);
  }

private String getNamespace(final String document) {
  int startIndex = document.indexOf("ns") - 1;
  int endIndex = document.indexOf(",", startIndex);
  String namespace = document.substring(startIndex, endIndex)
      .split(":") [1];
  return namespace.replaceAll("\"", BLANK).trim();
}
}