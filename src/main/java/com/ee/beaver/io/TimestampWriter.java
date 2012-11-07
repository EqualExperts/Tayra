package com.ee.beaver.io;

import java.io.IOException;
import java.io.Writer;

public class TimestampWriter extends Writer {
    private final Writer destination;
    private final Writer delegateTarget;

    public TimestampWriter(final Writer destination,
      final Writer delegateTarget) {
        this.destination = destination;
        this.delegateTarget = delegateTarget;
    }

    @Override
    public final void write(final char[] data, final int off, final int len)
      throws IOException {
        delegateTarget.write(data, off, len);
        char[] ts = extractTimestamp(data);
        destination.write(ts, off, ts.length);
    }

    private char[] extractTimestamp(final char[] data) {
        String oplogDocument = new String(data);
        String ts = oplogDocument.substring(oplogDocument.indexOf("\"ts\" :"),
            oplogDocument.indexOf("}") + 1);
        return ts.toCharArray();
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
    }
}
