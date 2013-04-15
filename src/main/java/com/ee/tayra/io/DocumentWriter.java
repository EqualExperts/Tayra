package com.ee.tayra.io;

import java.io.IOException;

public interface DocumentWriter {

  void writeDocument(String document) throws IOException;

  void close() throws IOException;

  void flush() throws IOException;
}
