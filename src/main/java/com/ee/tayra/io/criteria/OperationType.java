package com.ee.tayra.io.criteria;

public abstract class OperationType {

   static final String BLANK = "";
public static OperationType create(final String documentNamespace) {
    if (isDDL(documentNamespace)) {
      return new DDLOperation();
    }
    return new DMLOperation();
  }

  private static boolean isDDL(final String documentNamespace) {
     String command = documentNamespace.split("\\.", 2)[1];
      return (("$cmd".equals(command)) || ("system.indexes".equals(command)));
  }

   final String extractCollectionName(final String incomingNS) {
      try {
        return incomingNS.split("\\.", 2)[1];
      } catch (ArrayIndexOutOfBoundsException e) {
        return BLANK;
    }
  }

    final boolean matchDbName(final String document,
      final String documentNamespace, final String incomingNs) {
      String dbName = extractDbName(incomingNs);
      String documentDb = documentNamespace.split("\\.", 2)[0];
      if (dbName.equals(documentDb)) {
        return true;
      }
      return false;
    }

   final boolean matchDbAndCollectionName(final String document,
          final String documentNamespace, final String incomingNs) {
          if (incomingNs.equals(documentNamespace)) {
          return true;
        }
        return false;
      }

  private String extractDbName(final String incomingNS) {
      return incomingNS.split("\\.", 2)[0];
  }

  public abstract boolean match(final String document,
                  final String documentNamespace, final String incomingNs);
}
