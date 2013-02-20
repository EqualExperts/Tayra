package com.ee.tayra.io.criteria;

public class DDLOperation extends OperationType {

 @Override
 public final boolean match(final String document,
                    final String documentNamespace, final String incomingNs) {
        if (!matchDbName(document, documentNamespace, incomingNs)) {
          return false;
        }
        String collectionName = extractCollectionName(incomingNs);
        if (collectionName == BLANK) {
          return true;
        }
        return matchCollectionInPayload(document, collectionName);
}

  private boolean matchCollectionInPayload(final String document,
          final String incomingCollectionName) {

    int startIndex = document.indexOf("\"o\" :") - 1;
    String payload = document.substring(startIndex).split(":", 2)[1].trim();
    if (payload.matches(".*\"dropDatabase\".*")) {
            return true;
     }
    DDLStrategy strategy = DDLStrategy.create(payload);
    if (DDLStrategy.NO_STRATEGY.equals(strategy)) {
      return false;
    }
   return strategy.matchCollection(incomingCollectionName, payload);
}
}
