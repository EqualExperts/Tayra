/*******************************************************************************
 * Copyright (c) 2013, Equal Experts Ltd
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation
 * are those of the authors and should not be interpreted as representing
 * official policies, either expressed or implied, of the Tayra Project.
 ******************************************************************************/
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
      documents = charBuffer.toString().split("\\n");
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
