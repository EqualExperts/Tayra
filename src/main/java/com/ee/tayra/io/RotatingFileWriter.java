package com.ee.tayra.io;

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
  private int fileMax = 0;

  private final PatternLayout layout;
  private RollingFileAppender appender;
  private Logger logger;
  private final String recordToFile;

  public RotatingFileWriter(final String recordToFile) throws IOException {
    super();
    this.recordToFile = recordToFile;
    layout = new PatternLayout();
    configure();
  }

  public final void setFileSize(final String fileSize) throws IOException {
    this.fileSize = fileSize;
    if (fileMax == 0) {
      fileMax = DEFAULT_FILEMAX;
    }
    configure();
  }

  public final void setFileMax(final int fileMax) throws IOException {
    this.fileMax = fileMax;
    configure();
  }

  private void configure() throws IOException {
    appender = new RollingFileAppender(layout, recordToFile, false);
    logger = Logger.getLogger(recordToFile);
    appender.setMaxFileSize(fileSize);
    appender.setMaxBackupIndex(fileMax);
    appender.setImmediateFlush(true);
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
