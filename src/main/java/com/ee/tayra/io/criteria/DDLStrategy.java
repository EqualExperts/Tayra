package com.ee.tayra.io.criteria;

public enum DDLStrategy {

CREATECOLLECTION {
  @Override
  public String extractCollection(final String payload) {
  int startIndex = payload.indexOf("\"create\" :") - 1;
  int endIndex = payload.indexOf("}", startIndex);
  if (payload.contains("capped")) {
    endIndex = payload.indexOf(",", startIndex);
  }
return payload.substring(startIndex, endIndex)
       .split(":") [1].replaceAll("\"", BLANK).trim();
}
},

DROPCOLLECTION {
  @Override
  public String extractCollection(final String payload) {
    int startIndex = payload.indexOf("\"drop\" :") - 1;
    int endIndex = payload.indexOf("}", startIndex);
      return payload.substring(startIndex, endIndex)
        .split(":") [1].replaceAll("\"", BLANK).trim();
}
},

CREATEINDEX {
  @Override
  public String extractCollection(final String payload) {
     int startIndex = payload.indexOf("\"ns\" :") - 1;
     int endIndex = payload.indexOf(",", startIndex);
     String namespace = payload.substring(startIndex, endIndex)
         .split(":") [1].replaceAll("\"", BLANK).trim();
         return namespace.split("\\.", 2) [1];
  }
},

DROPINDEX {
  @Override
  public String extractCollection(final String payload) {
    int startIndex = payload.indexOf("\"deleteIndexes\" :") - 1;
    int endIndex = payload.indexOf(",", startIndex);
     return payload.substring(startIndex, endIndex)
    .split(":") [1].replaceAll("\"", BLANK).trim();
  }
},

NO_STRATEGY {

  @Override
  String extractCollection(final String payload) {
    throw new DDLOperationNotSupported("Cannot recognize operation "
  + "in the payload " + payload);
  }
};

  public boolean matchCollection(final String incomingCollectionName,
      final String payload) {
    String collection = extractCollection(payload);
    return incomingCollectionName.equals(collection);
}
abstract String extractCollection(String payload);
private static final String BLANK = "";

public static DDLStrategy create(final String payload) {
  if (payload.matches(".*\"create\".*")) {
      return DDLStrategy.CREATECOLLECTION;
    }
    if (payload.matches(".*\"drop\".*")) {
      return DDLStrategy.DROPCOLLECTION;
    }
    if (payload.matches(".*\"ns\".*")) {
      return DDLStrategy.CREATEINDEX;
    }
    if (payload.matches(".*\"deleteIndexes\".*")) {
      return DDLStrategy.DROPINDEX;
    }
  return DDLStrategy.NO_STRATEGY;
}
}
