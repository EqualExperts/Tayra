package com.ee.tayra.io.criteria;

public class DMLOperation extends OperationType {

  @Override
  public final boolean match(final String document,
                    final String documentNamespace,
      final String incomingNs) {
    String collectionName = extractCollectionName(incomingNs);
         if (collectionName == BLANK) {
              return matchDbName(document, documentNamespace, incomingNs);
            }
            return matchDbAndCollectionName(document, documentNamespace,
              incomingNs);
  }

}
