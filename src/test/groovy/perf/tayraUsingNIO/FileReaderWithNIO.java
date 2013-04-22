package perf.tayraUsingNIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import com.ee.tayra.io.DocumentReader;
import com.ee.tayra.io.ReadNotifier;

public class FileReaderWithNIO implements DocumentReader {

	private static final int BUFFER_SIZE = 4 * 1024; // Default : 8 KB
	private RandomAccessFile sourceFile;
	// private String[] documentBuffer = null;
	private int readSize = 0;
	// private long startTime = new Date().getTime();
	private MappedByteBuffer sourceBuffer;
	private int index = 0;
	private boolean readingFromStart = true;

	// Source File - NIO Read
	File source = null;
	private String leftOver;
	private String[] documents = null;

	public FileReaderWithNIO(String fileName) {
		source = new File(fileName);
		try {
			System.out.println("Reading file.. " + fileName);
			sourceFile = new RandomAccessFile(source, "r");
			documents = getDocumentsFromFile("");
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	String[] getDocumentsFromFile(String prependLeftoverDocument) {
		String[] documents = null;
		index = 0;
		try {
			if (readingFromStart) {
				sourceFile.seek(0L);
			}
			readingFromStart = false;
			long filePointer = sourceFile.getFilePointer();
			long fileLength = sourceFile.length();

			if (filePointer < fileLength) {
				documents = getDocumentsFromFile(filePointer, fileLength);

				sourceFile.seek(filePointer + readSize);
			}
			if (!prependLeftoverDocument.isEmpty()) {
				documents[0] = prependLeftoverDocument + documents[0];
			}
		} catch (IOException ioe) {
		}
		return documents;
	}

	String[] getDocumentsFromFile(long filePointer, long fileLength)
			throws IOException, CharacterCodingException {
		String[] documents;
		readSize = (int) Math.min(BUFFER_SIZE, fileLength - filePointer);
		sourceBuffer = sourceFile.getChannel().map(
				FileChannel.MapMode.READ_ONLY,
				filePointer, readSize);

		Charset charset = Charset.defaultCharset();
		CharsetDecoder decoder = charset.newDecoder();
		CharBuffer charBuffer = decoder.decode(sourceBuffer);
		documents = charBuffer.toString().split("\\n");
		return documents;
	}

	@Override
	public String readDocument() {
		try {
			while (sourceFile.getFilePointer() <= sourceFile.length()) {
				if (index < documents.length) {
					String document = documents[index++];
					if (document != null && document.contains("{")
							&& document.contains("}}")) {
						return document.trim();
					} else {
						leftOver = document;
					}
				}
				if (sourceFile.getFilePointer() < sourceFile.length()) {
					documents = getDocumentsFromFile(leftOver);
				} else
					break;

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String[] loadBufferWithData() throws IOException {
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

			sourceFile.seek(sourceFile.getFilePointer() + readSize);
		}
		return documents;
	}

	@Override
	public void close() throws IOException {
		sourceFile.close();
	}

}
