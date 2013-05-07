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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;

class Chunker implements Iterable<Chunk> {

  private static final long START_POSITION = 0L;
  private RandomAccessFile sourceFile;
  private File source = null;
  private static long fileSize;
  private static PartialDocumentHandler handler = new PartialDocumentHandler();
  private final long chunkSize;
  private Iterator<Chunk> chunkIterator;
  private Iterator<String> documentIterator;
  private String document = "";

  public Chunker(final String fileName, final long bufferSize)
      throws IOException {
    source = new File(fileName);
    sourceFile = new RandomAccessFile(source, "r");
    this.chunkSize = bufferSize;
    fileSize = sourceFile.length();
    setFilePointerTo(START_POSITION);
    chunkIterator = iterator();
  }

  private void setFilePointerTo(final long newPosition) throws IOException {
    sourceFile.seek(newPosition);
  }

  @Override
  public final Iterator<Chunk> iterator() {
    return new ChunkIterator(sourceFile, chunkSize);
  }

  public String getDocument() {
    document = "";
    do {
      if (thereAreNoDocuments()) {
        if (hasMoreChunks()) {
          try {
            Chunk chunk = chunkIterator.next();
            documentIterator = chunk.iterator();
          } catch (Exception problem) {
            throw new RuntimeException(problem);
          }
        } else {
          return null;
        }
      }
      try {
        document = document + documentIterator.next();
      } catch (Exception problem) {
        throw new RuntimeException(problem);
      }
    } while (handler.isPartial(document));
    return document.trim();
  }

  final boolean hasMoreChunks() {
    return chunkIterator.hasNext();
  }

  final boolean thereAreNoDocuments() {
    return (documentIterator == null || !documentIterator.hasNext());
  }

  public final void close() throws IOException {
    sourceFile.close();
  }

  private static class ChunkIterator implements Iterator<Chunk> {
    private final RandomAccessFile sourceFile;
    private long filePointer;
    private final long chunkSize;

    public ChunkIterator(final RandomAccessFile sourceFile,
        final long chunkSize) {
      this.sourceFile = sourceFile;
      this.chunkSize = chunkSize;
    }

    @Override
    public final boolean hasNext() {
      try {
        filePointer = sourceFile.getFilePointer();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return (filePointer < fileSize);
    }

    private void setFilePointerTo(final long newPosition)
        throws IOException {
      sourceFile.seek(newPosition);
    }

    @Override
    public final Chunk next() {
      try {
        Chunk chunk = new Chunk(sourceFile.getChannel(), filePointer,
            fileSize, chunkSize, handler);
        setFilePointerTo(filePointer + chunk.getReadSize());
        return chunk;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public final void remove() {
      throw new UnsupportedOperationException(
          "remove chunk is not supported");
    }
  }

  static class PartialDocumentHandler {
    private String partialDoc = "";

    final void handlePartialDocument(final String partialDoc) {
      this.partialDoc = partialDoc;
    }

    final String prependPartialDocumentTo(final String document) {
      String completeDocument = partialDoc + document;
      partialDoc = "";
      return completeDocument;
    }

    boolean isPartial(final String document) {
      try {
        if (document.trim().isEmpty()) {
          return true;
        }
        JSON.parse(document);
        return false;
      } catch (JSONParseException ex) {
        return true;
      }
    }
  }
}
