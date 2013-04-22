package perf.tayraUsingNIO;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

import com.ee.tayra.io.DocumentReader;

public class NewMemoryMappedFileReader implements DocumentReader {

	private RandomAccessFile sourceFile;
	private Iterator<String> documentIterator = null;
	private Iterator<Chunk> chunkIterator;

	public NewMemoryMappedFileReader(String fileName) throws IOException {
		chunkIterator = new Chunker(fileName).iterator();
	}

	@Override
	public String readDocument() throws Exception{
		if (documentIterator == null || (documentIterator.hasNext() == false)) {
			if (chunkIterator.hasNext()) {
				Chunk chunk = chunkIterator.next();
				documentIterator = chunk.iterator();
			} else {
				return null;
			}
		} else {
			return null;
		}
//		String document = documentIterator.next();
//		if (document != null && document.contains("{")
//				&& document.contains("}}")) {
//			return document.trim();
//		} else {
//			leftOver = document;
//		}
		return documentIterator.next();
	}

	@Override
	public void close() throws IOException {
		sourceFile.close();
	}

}
