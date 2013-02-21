package com.ee.tayra.fixtures;

public class Result {
  private static final String BLANK = "";
  private Object srcValue = 0;
  private Object destValue = 0;

    public Result(final Object srcValue, final Object destValue) {
    this.srcValue = srcValue;
    this.destValue = destValue;
  }

  public final String getSourceValue() {
    if (srcValue == null) {
      return BLANK;
    }
    return srcValue.toString();
  }

  public final String getDestinationValue() {
    if (destValue == null) {
      return BLANK;
    }
    return destValue.toString();
  }
}
