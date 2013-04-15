package com.ee.tayra.io;

import java.io.IOException;

public interface DocumentWriter {

  void writeDocument(String document);

  void close() throws IOException;

  void flush();
}
