/*******************************************************************************
 * Copyright (c) 2013, Equal Experts Ltd
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * Ê Êthis list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * Ê Ênotice, this list of conditions and the following disclaimer in the
 * Ê Êdocumentation and/or other materials provided with the distribution.
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
