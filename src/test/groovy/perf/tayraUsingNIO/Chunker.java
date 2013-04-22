package perf.tayraUsingNIO;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.util.Iterator;

public class Chunker implements Iterable<Chunk> {

	private RandomAccessFile sourceFile;
	private MappedByteBuffer sourceBuffer;
	private File source = null;
	private static long fileSize;

	public Chunker(String fileName) throws IOException {
		source = new File(fileName);
		sourceFile = new RandomAccessFile(source, "r");
		fileSize = sourceFile.length();
		sourceFile.seek(0L);
	}

	@Override
	public Iterator<Chunk> iterator() {
		try {
			return new ChunkerIterator(sourceFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static class ChunkerIterator implements Iterator<Chunk> {
		private final RandomAccessFile sourceFile;
		private long filePointer;

		public ChunkerIterator(RandomAccessFile sourceFile) throws IOException {
			this.sourceFile = sourceFile;
		}

		@Override
		public boolean hasNext() {
			return (filePointer < fileSize);
		}

		@Override
		public Chunk next() {
			try {
				filePointer = sourceFile.getFilePointer();
				Chunk chunk = new Chunk(sourceFile.getChannel(), filePointer, fileSize);
				sourceFile.seek(filePointer + chunk.getReadSize());
				return chunk;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
