package com.ee.tayra.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DataUnit {
  public static final DataUnit  B = new DataUnit(1, 1);
  public static final DataUnit KB = new DataUnit(1, 1024);
  public static final DataUnit MB = new DataUnit(1, 1024 * 1024);
  public static final DataUnit GB = new DataUnit(1, 1024 * 1024 * 1024);

  private static final String regex = "^([0-9]+)(.*)$";
  private static final Pattern numberPattern = Pattern.compile(regex);
  private final int bytesFactor;
  private final int value;

  private DataUnit(final int value, final int bytesFactor) {
    this.bytesFactor = bytesFactor;
    this.value = value;
  }

  public int bytesFactor() {
    return bytesFactor;
  }

  public int value() {
    return value;
  }

  public long toLongValue() {
    return value() * bytesFactor();
  }

  public static DataUnit from(final String value) {
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException("Valid values are B, KB, MB, GB");
    }
    Matcher matcher = numberPattern.matcher(value);
    if (matcher.matches()) {
      String group = matcher.group(1);
      int number = Integer.parseInt(group);
      String unit = matcher.group(2);
      return new DataUnit(number, toBasicUnit(unit).bytesFactor());
    }
    throw new IllegalArgumentException("Don't know how to represent " + value);
  }

  private static DataUnit toBasicUnit(final String unit) {
    String unitUpperCase = unit.trim().toUpperCase();
    if (unitUpperCase.contains("G")) {
      return GB;
    }
    if (unitUpperCase.contains("M")) {
      return MB;
    }
    if (unitUpperCase.contains("K")) {
      return KB;
    }
    return B;
  }
}
