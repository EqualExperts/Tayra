package com.ee.tayra;

import java.util.HashMap;
import java.util.Map;

public class NamedParameters {
  private final Map<String, String> namedParams;

  public NamedParameters() {
    namedParams = new HashMap<String, String>();
  }

  public final void add(final String name, final String value) {
    namedParams.put(name, value);
  }

  public final int size() {
    return namedParams.size();
  }

  public final String get(final String key) {
    return namedParams.get(key);
  }

  public final String substitueValuesIn(final String data) {
    String result = data;
    for (Map.Entry<String, String> nameValue : namedParams.entrySet()) {
      String key = nameValue.getKey();
      String value = nameValue.getValue();
      result = result.replace(key, value);
    }
    return result;
  }
}
