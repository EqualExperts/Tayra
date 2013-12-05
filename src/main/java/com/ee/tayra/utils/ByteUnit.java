package com.ee.tayra.utils;

public enum ByteUnit {
  NOTHING (1, null),
  B  (1, NOTHING),
  KB (1024, B),
  MB (1024, KB),
  GB (1024, MB);

  private final int factor;

  private ByteUnit(final int factor, final ByteUnit byteUnit) {
    if (byteUnit == null) {
      this.factor = factor;
    } else {
      this.factor = factor * byteUnit.factor;
    }
  }

  public int toInt() {
    return factor;
  }

  public static ByteUnit from(final String unit) {
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
