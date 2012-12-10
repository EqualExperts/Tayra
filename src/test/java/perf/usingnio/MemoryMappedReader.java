package usingnio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

public final class MemoryMappedReader {

  private static final int BUFFER_SIZE = 4194304;

  private MemoryMappedReader() {
  }

  public static void main(final String[] args) throws IOException {
    String name = args[0];
    File file = new File(name);

    // use RandomAccessFile because it supports readFully()
    RandomAccessFile fileHandle = new RandomAccessFile(file, "r");
    fileHandle.seek(0L);
    int readSize = 0;
    long startTime = new Date().getTime();
    while (fileHandle.getFilePointer() < fileHandle.length()) {
      readSize = (int) Math.min(BUFFER_SIZE,
            fileHandle.length() - fileHandle.getFilePointer());
      MappedByteBuffer buffer = fileHandle.getChannel().map(
            FileChannel.MapMode.READ_ONLY,
            fileHandle.getFilePointer(), readSize);

      // Reading from Memory Mapped File
      while (buffer.hasRemaining()) {
        buffer.get();
      }
      fileHandle.seek(fileHandle.getFilePointer() + readSize);
    }
    long endTime = new Date().getTime();
    fileHandle.close();
    System.out.printf("%s, %d\n", file.getName(), (endTime - startTime));
  }
}
