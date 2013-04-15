package com.ee.tayra.command.backup

import com.ee.tayra.io.DocumentWriter

class StringDocumentWriter implements DocumentWriter {
    private StringBuilder result

    public StringDocumentWriter() {
        result = new StringBuilder()
    }
    @Override
    void writeDocument(String document) throws IOException {
      result << document
    }

    @Override
    void close() throws IOException {
    }

    @Override
    void flush() throws IOException {
    }

    @Override
    public java.lang.String toString() {
      result.toString()
    }
}
