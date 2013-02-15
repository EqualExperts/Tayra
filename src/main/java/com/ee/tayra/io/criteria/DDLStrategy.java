package com.ee.tayra.io.criteria;

public enum DDLStrategy {
  CREATECOLLECTION {
    @Override
    public boolean matchCollection(final String incomingCollectionName,
        final String payload) {
        int startIndex;
        int endIndex;
        startIndex = payload.indexOf("\"create\" :") - 1;
        endIndex = payload.indexOf("}", startIndex);
          if (payload.contains("capped")) {
              endIndex = payload.indexOf(",", startIndex);
          }
          String collection = payload.substring(startIndex, endIndex)
                  .split(":") [1].replaceAll("\"", BLANK).trim();
          if (incomingCollectionName.equals(collection)) {
            return true;
          }
          return false;
    }
},
  DROPCOLLECTION {
    @Override
    public boolean matchCollection(final String incomingCollectionName,
        final String payload) {
        int startIndex = payload.indexOf("\"drop\" :") - 1;
          int endIndex = payload.indexOf("}", startIndex);
          String collection = payload.substring(startIndex, endIndex)
                  .split(":") [1].replaceAll("\"", BLANK).trim();
          if (incomingCollectionName.equals(collection)) {
            return true;
          }
        return false;
    }
},
  CREATEINDEX {
      @Override
      public boolean matchCollection(final String incomingCollectionName,
           final String payload) {
        int startIndex = payload.indexOf("\"ns\" :") - 1;
          int endIndex = payload.indexOf(",", startIndex);
          String namespace = payload.substring(startIndex, endIndex)
                  .split(":") [1].replaceAll("\"", BLANK).trim();
          String collection = namespace.split("\\.", 2) [1];
          if (incomingCollectionName.equals(collection)) {
            return true;
          }
        return false;
      }
},
  DROPINDEX {
      @Override
      public boolean matchCollection(final String incomingCollectionName,
          final String payload) {
        int startIndex = payload.indexOf("\"deleteIndexes\" :") - 1;
          int endIndex = payload.indexOf(",", startIndex);
          String collection = payload.substring(startIndex, endIndex)
                  .split(":") [1].replaceAll("\"", BLANK).trim();
          if (incomingCollectionName.equals(collection)) {
            return true;
          }
        return false;
        }
};
  public abstract boolean matchCollection(final String incomingCollectionName
                          , final String payload);
  private static final String BLANK = "";
}
