package com.ee.tayra.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DataUnit {
  private final int bytesFactor;
    private final int value;
    private static final Map<String, DataUnit> cache
          = new HashMap<String, DataUnit>();
    public static final DataUnit NOTHING = new DataUnit(0, 1, null);
    public static final DataUnit  B = new DataUnit(1, 1, NOTHING);
    public static final DataUnit KB = new DataUnit(1, 1024, B);
    public static final DataUnit MB = new DataUnit(1, 1024, KB);
    public static final DataUnit GB = new DataUnit(1, 1024, MB);

    private static final String regex = "^([0-9]+)(.+)$";
    private static final Pattern numberPattern = Pattern.compile(regex);
    public static final int PRIME = 31;

  private DataUnit(final int value, final DataUnit unit) {
    this(value, 1, unit);
  }

  private
  DataUnit(final int value, final int bytesFactor, final DataUnit other) {
    if (other == NOTHING) {
      this.bytesFactor = bytesFactor;
    } else {
      this.bytesFactor = bytesFactor * other.bytesFactor();
    }
    this.value = value;
  }

  public int bytesFactor() {
    return bytesFactor;
  }

  public int value() {
    return value;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }

    DataUnit that = (DataUnit) other;

    if (bytesFactor != that.bytesFactor) {
      return false;
    }

    if (value != that.value) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = bytesFactor;
    result = PRIME * result + value;
    return result;
  }

  public long toLongValue() {
    return value() * bytesFactor();
  }

  public static DataUnit from(final String valueWithUnit) {
    if (valueWithUnit == null || valueWithUnit.isEmpty()) {
      throw new IllegalArgumentException("Valid values are B, KB, MB, GB");
    }
    if (cache.containsKey(valueWithUnit)) {
      return cache.get(valueWithUnit);
    }
    Matcher matcher = numberPattern.matcher(valueWithUnit);
    if (matcher.matches()) {
      String group = matcher.group(1);
      int value = Integer.parseInt(group);
      String unit = matcher.group(2);
      final DataUnit dunit = new DataUnit(value, toBasicUnit(unit));
      cache.put(valueWithUnit, dunit);
      return dunit;
    }
    throw new IllegalArgumentException("Don't know how to represent "
            + valueWithUnit);
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
