package usingio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;

public final class RegularIOReader {

  private static final int TIMES = 512;
  private static final int ONE_KB = 1024;

  private RegularIOReader() {
  }

  public static void main(final String[] args) throws Exception {

    int bufferSize = (TIMES * ONE_KB);
    String name = args[0];
    File file = new File(name);
    BufferedReader br = new BufferedReader(new FileReader(file), bufferSize);

    long startTime = new Date().getTime();
    String strLine = br.readLine();
    while (strLine != null) {
      strLine = br.readLine();
    }
    long endTime = new Date().getTime();
    System.out.printf("%s, %d\n", file.getName(), (endTime - startTime));
  }

}
