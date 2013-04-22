package perf.tayraUsingNIO;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;

public class Chunk implements Iterable<String>{

	private static final int BUFFER_SIZE = 4 * 1024;
	private int readSize;
	private MappedByteBuffer sourceBuffer;

	public Chunk(FileChannel channel, long filePointer, long fileLength) throws IOException {
		readSize = (int) Math.min(BUFFER_SIZE, fileLength - filePointer);
		sourceBuffer = channel.map(FileChannel.MapMode.READ_ONLY, filePointer,
				readSize);
	}

	public int getReadSize() {
		return readSize;
	}

	public boolean isEmpty() {
		return false;
	}

	public Iterator<String> iterator() {
		try {
			return new DocumentIterator(sourceBuffer);
		} catch (CharacterCodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static class DocumentIterator implements Iterator<String> {

		private final CharBuffer charBuffer;
		private final String[] documents;
		private int index = 0;

		public DocumentIterator(MappedByteBuffer sourceBuffer) throws CharacterCodingException {
			Charset charset = Charset.defaultCharset();
			CharsetDecoder decoder = charset.newDecoder();
			this.charBuffer = decoder.decode(sourceBuffer);
			//TODO: preferably traverse charBuffer
			documents = charBuffer.toString().split("\\n");
		}

		@Override
		public boolean hasNext() {
			return documents.length == index++;
		}

		@Override
		public String next() {
//			while()
			String document = documents[index++];
			if (document != null && document.contains("{")
					&& document.contains("}}")) {
				return document.trim();
//			} else {
//				leftOver = document;
			}
			return documents[index];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
