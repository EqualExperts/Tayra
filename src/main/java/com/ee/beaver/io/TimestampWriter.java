package com.ee.beaver.io;

import java.io.IOException;
import java.io.Writer;

public class TimestampWriter extends Writer {
	
	private final Writer destination;
	private final Writer delegateTarget;

	public TimestampWriter(final Writer destination, final Writer delegateTarget) {
		this.destination = destination;
		this.delegateTarget = delegateTarget;
	}

	@Override
	public void write(char[] data, int off, int len) throws IOException {
		delegateTarget.write(data, off, len);
		char[] ts = extractTimestamp(data);
		destination.write(ts, off, len);
	}

	private char[] extractTimestamp(char[] data) {
		String[] splitData = new String(data).split(",");
		String ts = splitData[0] + " , " + splitData[1];
		System.out.println(ts.substring(2));
		return ts.substring(2).toCharArray();
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
	}
}
