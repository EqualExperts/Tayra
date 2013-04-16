package com.ee.tayra.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;


public class FileBasedTimestampRepository implements TimestampRepository {
    private final File timestampFile;
    private PrintWriter console;

    public FileBasedTimestampRepository(
            final File timestampFile, final PrintWriter console) {
        this.console = console;
        if (timestampFile.isDirectory()) {
          final String message = "Expecting " + timestampFile.getName()
                    + " to be a File, but found Directory";
            console.println(message);
          throw new IllegalArgumentException(message);
      }
      this.timestampFile = timestampFile;
    }

    @Override
    public final void save(final String tstamp) throws IOException {
       final FileWriter fileWriter = new FileWriter(timestampFile);
       fileWriter.write(tstamp);
       fileWriter.flush();
       fileWriter.close();
    }

    @Override
    public final String retrieve() throws IOException {
      if (timestampFile.exists()) {
        if (timestampFile.canRead() && timestampFile.length() > 0) {
          final BufferedReader reader =
                  new BufferedReader(new FileReader(timestampFile));
          final String timestamp = reader.readLine();
          reader.close();
          return timestamp;
        } else {
          console.println("Unable to read " + timestampFile.getName());
        }
      }
      return "";
    }
}
