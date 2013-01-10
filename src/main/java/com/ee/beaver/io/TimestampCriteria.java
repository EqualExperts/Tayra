package com.ee.beaver.io;

public class TimestampCriteria implements Criterion {

  private final long timeStampUntil;
  private final int increment;
  private static final String TS_IDENTIFIER = "$ts:";
  private static final String INC_IDENTIFIER = "$inc:";

  public TimestampCriteria(final String filter) {
    this.timeStampUntil = getTimestampFrom(filter);
    this.increment = getIncrementFrom(filter);
  }

  private int getIncrementFrom(final String filter) {
   int incStartIndex = filter.indexOf(INC_IDENTIFIER) + INC_IDENTIFIER.length();
   int incEndIndex = filter.indexOf("}", incStartIndex);
   return Integer.parseInt(filter.substring(incStartIndex, incEndIndex).trim());
  }

  private long getTimestampFrom(final String filter) {
   int tsStartIndex = filter.indexOf(TS_IDENTIFIER) + TS_IDENTIFIER.length();
   int tsEndIndex = filter.indexOf(INC_IDENTIFIER);
   return Long.parseLong(filter.substring(tsStartIndex, tsEndIndex)
                              .replaceAll(",", "").trim());
  }

  @Override
  public boolean isSatisfiedBy(final String document) {
   if (timeStampUntil > getTimestampFrom(document.replaceAll("\"", "")
                                                 .replaceAll(" ", ""))) {
     return true;
   }
   if (timeStampUntil == getTimestampFrom(document.replaceAll("\"", "")
                                                 .replaceAll(" ", ""))) {
     return increment >= getIncrementFrom(document.replaceAll("\"", "")
                                                 .replaceAll(" ", ""));
   }
   return false;
  }

}
