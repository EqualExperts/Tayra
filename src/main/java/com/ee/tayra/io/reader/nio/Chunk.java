package com.ee.tayra.io.reader.nio;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;

import com.ee.tayra.io.reader.nio.Chunker.PartialDocumentHandler;

class Chunk implements Iterable<String> {

  private long readSize;
  private MappedByteBuffer chunk;
  private final PartialDocumentHandler handler;

  Chunk(final FileChannel channel, final long filePointer,
      final long fileLength, final long chunkSize,
      final PartialDocumentHandler handler) throws IOException {
    this.handler = handler;
    readSize = Math.min(chunkSize, fileLength - filePointer);
    chunk = channel.map(FileChannel.MapMode.READ_ONLY, filePointer,
        readSize);
  }

  public final long getReadSize() {
    return readSize;
  }

  @Override
  public final Iterator<String> iterator() {
    try {
      return new DocumentIterator(chunk, handler);
    } catch (CharacterCodingException e) {
      throw new RuntimeException(e);
    }
  }

  private static class DocumentIterator implements Iterator<String> {

    private final CharBuffer charBuffer;
    private final String[] documents;
    private int index = 0;
    private final PartialDocumentHandler handler;

    public DocumentIterator(final MappedByteBuffer chunk,
        final PartialDocumentHandler handler)
        throws CharacterCodingException {
      this.handler = handler;
      Charset charset = Charset.defaultCharset();
      CharsetDecoder decoder = charset.newDecoder();
      this.charBuffer = decoder.decode(chunk);
      documents = charBuffer.toString().split(
          System.getProperty("line.separator"));
      documents[0] = handler.prependPartialDocumentTo(documents[0]);
    }

    @Override
    public final boolean hasNext() {
      if (isLastDocumentPartial()) {
        handler.handlePartialDocument(documents[index]);
        return false;
      }
      return documents.length > index;
    }

    public boolean isLastDocumentPartial() {
      if (isLastDocument()) {
        String lastDocument = documents[index];
        return handler.isPartial(lastDocument);
      } else {
        return false;
      }
    }

    final boolean isLastDocument() {
      return index == documents.length - 1;
    }

    @Override
    public final String next() {
      try {
        return documents[index++];
      } catch (Exception e) {
        throw new IllegalStateException("Index Out of Bounds");
      }
    }

    @Override
    public final void remove() {
      throw new UnsupportedOperationException("remove not supported");
    }
  }
}
