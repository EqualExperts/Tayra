package com.ee.tayra.utils

import com.ee.tayra.io.DocumentWriter

class StringDocumentWriter implements DocumentWriter {
    private StringBuilder result

    public StringDocumentWriter() {
      result = new StringBuilder()
    }
    @Override
    void writeDocument(String document) {
      result << document
    }

    @Override
    void close() throws IOException {
    }

    @Override
    public java.lang.String toString() {
      result.toString()
    }
}
