package com.ee.beaver.io;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampCriteria implements Criterion {

  private static final String TS_IDENTIFIER = "$ts:";
  private static final String INC_IDENTIFIER = "$inc:";
  private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
  private static final long MILLI_CONVERSION = 1000L;
  private final Date timeStampUntil;
  private final int increment;

  public TimestampCriteria(final String filter) {
    this.timeStampUntil = getTimestampFrom(filter);
    this.increment = getIncrementFrom(filter);
  }

@Override
  public boolean isSatisfiedBy(final String document) {
    String tsDocument = document.replaceAll("\"", "")
      .replaceAll(" ", "");
    if (timeStampUntil.compareTo(getTimestampFrom(tsDocument)) > 0) {
    return true;
    }
    if (timeStampUntil.compareTo(getTimestampFrom(tsDocument)) == 0) {
      return increment >= getIncrementFrom(tsDocument);
    }
    return false;
  }

  private int getIncrementFrom(final String filter) {
    if (filter.contains(INC_IDENTIFIER)) {
      int incStartIndex = filter.indexOf(INC_IDENTIFIER)
              + INC_IDENTIFIER.length();
      int incEndIndex = filter.indexOf("}", incStartIndex);
      return Integer.parseInt(filter
              .substring(incStartIndex, incEndIndex).trim());
     }
    return Integer.MAX_VALUE;
  }

  private Date getTimestampFrom(final String filter) {
   if (filter.contains(TS_IDENTIFIER)) {
     int tsStartIndex = filter.indexOf(TS_IDENTIFIER) + TS_IDENTIFIER.length();
     int tsEndIndex = filter.indexOf(INC_IDENTIFIER);
     return new Date(Long.parseLong(filter.substring(tsStartIndex, tsEndIndex)
                              .replaceAll(",", "").trim()) * MILLI_CONVERSION);
   } else {
     try {
       SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
       return format.parse(filter.substring(filter.indexOf("=") + 1));
     } catch (ParseException p) {
       return new Date(0L);
     }
   }
  }

}
