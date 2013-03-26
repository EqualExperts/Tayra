package com.ee.tayra;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class NamedParameters {
  private Map<String, String> namedParams;

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

  public final Set<Entry<String, String>> entrySet() {
    return namedParams.entrySet();
  }
}
