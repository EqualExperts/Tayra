package com.ee.tayra.io.criteria;

import java.util.Date;

public class TimestampUntil extends TimestampCriteria {

  private Date timestamp;
  private int increment;

  public TimestampUntil(final String filter) {
    super();
    this.timestamp = getTimestampFrom(filter);
    this.increment = getIncrementFrom(filter);
  }

  @Override
  public final boolean isSatisfiedBy(final String document) {
    String tsDocument = document.replaceAll("\"", "").replaceAll(" ", "");
    if (timestamp.compareTo(getTimestampFrom(tsDocument)) > 0) {
      return true;
    }
    if (timestamp.compareTo(getTimestampFrom(tsDocument)) == 0) {
      return increment >= getIncrementFrom(tsDocument);
    }
    return false;
  }

}
