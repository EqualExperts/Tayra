package com.ee.tayra.io;

import java.io.IOException;

public interface TimestampRepository {
  void save(String timestamp) throws IOException;

  String retrieve() throws IOException;
}
