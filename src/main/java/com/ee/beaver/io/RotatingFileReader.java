package com.ee.beaver.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RotatingFileReader extends BufferedReader {

  private String prefixOfFile;
  private List<String> fileNames = new ArrayList<String>();
  private FileReader fileReader = null;
  private BufferedReader bufferedReader = null;
  private int nextFileIndex = 0;
  private String lineRead = null;

  public RotatingFileReader(final String prefixOfFile, final Reader reader)
      throws Exception {
    super(reader);
    this.prefixOfFile = prefixOfFile;
    readAllFileNames();
    reader.close();
  }

  private void readAllFileNames() throws Exception {
    final File directory = new File(System.getProperty("user.dir") + '\\');
    for (final File file : directory.listFiles()) {
      if (!file.isDirectory() && file.getName().startsWith(prefixOfFile)) {
        fileNames.add(file.getName());
      }
    }

    Collections.sort(fileNames, new Comparator<String>() {
      public int compare(final String a, final String b) {
        return b.compareTo(a);
      } // reverse the comparison result
    });
    loadBufferedReader();
  }

  public final List<String> getAllFileNames() {
    return fileNames;
  }

  public final String readLine() {
    try {
      lineRead = bufferedReader.readLine();
      if (lineRead != null) {
        return lineRead;
      } else if (nextFileIndex < fileNames.size()) {
        loadBufferedReader();
        return readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private void loadBufferedReader() throws Exception {
    if (fileReader != null) {
      fileReader.close();
    }
    if (bufferedReader != null) {
      bufferedReader.close();
    }
    fileReader = new FileReader(fileNames.get(nextFileIndex));
    bufferedReader = new BufferedReader(fileReader);
    System.out.println("Restoring File : " + fileNames.get(nextFileIndex));
    nextFileIndex++;
  }

  public final void close() {
    try {
      fileReader.close();
      bufferedReader.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
