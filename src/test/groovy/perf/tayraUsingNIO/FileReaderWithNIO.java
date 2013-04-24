package perf.tayraUsingNIO;

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
  private File source = null;
  private String leftOver;
  private String[] documents = null;

  public FileReaderWithNIO(final String fileName) {
    source = new File(fileName);
    try {
      System.out.println("Reading file.. " + fileName);
      sourceFile = new RandomAccessFile(source, "r");
      documents = getDocumentsFromFile("");
    } catch (FileNotFoundException e) {
      System.out.println(e.getMessage());
    }
  }

  final String[] getDocumentsFromFile(final String prependLeftoverDocument) {
    String[] newDocuments = null;
    index = 0;
    try {
      if (readingFromStart) {
        sourceFile.seek(0L);
      }
      readingFromStart = false;
      long filePointer = sourceFile.getFilePointer();
      long fileLength = sourceFile.length();

      if (filePointer < fileLength) {
        newDocuments = getDocumentsFromFile(filePointer, fileLength);

        sourceFile.seek(filePointer + readSize);
      }
      if (!prependLeftoverDocument.isEmpty()) {
        newDocuments[0] = prependLeftoverDocument + newDocuments[0];
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    return newDocuments;
  }

  final String[] getDocumentsFromFile(final long filePointer,
    final long fileLength) throws IOException {
    String[] documentsFromFile;
    readSize = (int) Math.min(BUFFER_SIZE, fileLength - filePointer);
    sourceBuffer = sourceFile.getChannel().map(
        FileChannel.MapMode.READ_ONLY,
        filePointer, readSize);

    Charset charset = Charset.defaultCharset();
    CharsetDecoder decoder = charset.newDecoder();
    CharBuffer charBuffer = decoder.decode(sourceBuffer);
    documentsFromFile = charBuffer.toString().split("\\n");
    return documentsFromFile;
  }

  @Override
  public final String readDocument() {
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
        } else {
          break;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public final String[] loadBufferWithData() throws IOException {
    if (readingFromStart) {
      sourceFile.seek(0L);
    }
    readingFromStart = false;

    String[] documentsInBuffer = null;
    if (sourceFile.getFilePointer() < sourceFile.length()) {
      readSize = (int) Math.min(BUFFER_SIZE, sourceFile.length()
          - sourceFile.getFilePointer());
      sourceBuffer = sourceFile.getChannel().map(
          FileChannel.MapMode.READ_ONLY, sourceFile.getFilePointer(),
          readSize);

      Charset charset = Charset.defaultCharset();
      CharsetDecoder decoder = charset.newDecoder();
      CharBuffer charBuffer = decoder.decode(sourceBuffer);
      documentsInBuffer = charBuffer.toString().split("\\n");

      sourceFile.seek(sourceFile.getFilePointer() + readSize);
    }
    return documentsInBuffer;
  }

  @Override
  public final void close() throws IOException {
    sourceFile.close();
  }

}
