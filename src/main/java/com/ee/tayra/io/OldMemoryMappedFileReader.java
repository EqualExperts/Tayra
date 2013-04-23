package com.ee.tayra.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import com.ee.tayra.io.reader.DocumentReader;

public class OldMemoryMappedFileReader implements DocumentReader {

	private static final int BUFFER_SIZE = 1048576; // Default : 8 KB
	private ReadNotifier notifier;
	private RandomAccessFile sourceFile;
	private String[] documentBuffer = null;
	private int readSize = 0;
	// private long startTime = new Date().getTime();
	private MappedByteBuffer sourceBuffer;
	private int index = 0;
	private boolean readingFromStart = true;

	// Source File - NIO Read
	File source = null;
	private String leftOver;

	public OldMemoryMappedFileReader(String fileName) {
		source = new File(fileName);
		try {
			sourceFile = new RandomAccessFile(source, "r");
			initializeBuffer("");
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	void initializeBuffer(String toAppend) {
		index = 0;
		try {
			documentBuffer = getBuffer();
			documentBuffer[0] = toAppend + documentBuffer[0];
		} catch (IOException ioe) {
			notifier.notifyReadFailure(null, ioe);
		}
	}

	@Override
	public String readDocument() {
		try {
			while (sourceFile.getFilePointer() <= sourceFile.length()) {
				notifier.notifyReadStart("");
				if (index < documentBuffer.length) {
					String document = documentBuffer[index++];
					if (document != null && document.contains("{")
							&& document.contains("}}")) {
						notifier.notifyReadSuccess(document);
						return document.trim();
					} else {
						leftOver = document;
					}
				}
				if (sourceFile.getFilePointer() < sourceFile.length()) {
					initializeBuffer(leftOver);
				} else {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String[] getBuffer() throws IOException {
		if (readingFromStart) {
			sourceFile.seek(0L);
		}
		readingFromStart = false;

		String[] documents = null;
		if (sourceFile.getFilePointer() < sourceFile.length()) {
			readSize = (int) Math.min(BUFFER_SIZE, sourceFile.length()
					- sourceFile.getFilePointer());
			sourceBuffer = sourceFile.getChannel().map(
					FileChannel.MapMode.READ_ONLY, sourceFile.getFilePointer(),
					readSize);

			Charset charset = Charset.defaultCharset();
			CharsetDecoder decoder = charset.newDecoder();
			CharBuffer charBuffer = decoder.decode(sourceBuffer);
			documents = charBuffer.toString().split("\\n");

//			StringBuilder stringBuilder = new StringBuilder();
//			while (sourceBuffer.hasRemaining()) {
//				byte input = sourceBuffer.get();
//				String character = new String(new byte[] { input });
//				stringBuilder.append(character);
//			}
//			documents = stringBuilder.toString().split("\\n");

//			documents = sourceBuffer.asCharBuffer().toString().split("\\n");
//			System.out.println(documents[0]);

			sourceFile.seek(sourceFile.getFilePointer() + readSize);
		}
		return documents;
	}

	@Override
	public void close() throws IOException {
		sourceFile.close();
	}
}
