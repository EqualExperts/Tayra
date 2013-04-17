package com.ee.tayra.io;

import java.io.IOException;

public interface DocumentReader {

  String readDocument();

  void close() throws IOException;
}
