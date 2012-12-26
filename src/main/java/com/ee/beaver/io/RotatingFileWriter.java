package com.ee.beaver.io;

import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

public class RotatingFileWriter extends Writer {

  private static final String DEFAULT_FILESIZE = "512MB";
  private static final int DEFAULT_FILEMAX = 20;

  private String fileSize = DEFAULT_FILESIZE;
  private int fileMax = DEFAULT_FILEMAX;

  private PatternLayout layout;
  private RollingFileAppender appender;
  private Logger logger;

  public RotatingFileWriter(final String recordToFile, final String fSize,
      final int fMax)  throws IOException {
    super();
    if (fSize != null) {
      fileSize = fSize;
    }
    if (fMax > 0) {
      fileMax = fMax;
    }
    if (fSize == null && fMax == 0) {
      fileMax = 0;
    }
    setupLoggingProperties(recordToFile);
  }

  private void setupLoggingProperties(final String recordToFile)
    throws IOException {
      layout = new PatternLayout();
      appender = new RollingFileAppender(layout, recordToFile, false);
      appender.setMaxFileSize(fileSize);
      appender.setMaxBackupIndex(fileMax);
      appender.setImmediateFlush(true);
      logger = Logger.getLogger(recordToFile);
      logger.removeAllAppenders();
      logger.setAdditivity(false);
      logger.setLevel(Level.INFO);
      logger.addAppender(appender);
  }

  @Override
  public final void write(final String document, final int off, final int len)
    throws IOException {
      logger.info(document);
  }

  @Override
  public final void flush() throws IOException {
  }

  @Override
  public final void close() throws IOException {
    appender.close();
  }

  @Override
  public final void write(final char[] document, final int off, final int len)
    throws IOException {
      write(document, off, len);
  }
}
