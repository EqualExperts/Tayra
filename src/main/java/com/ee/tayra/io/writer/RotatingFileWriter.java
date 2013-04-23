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
package com.ee.tayra.io.writer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.ee.tayra.io.WriteNotifier;

import java.io.IOException;

public class RotatingFileWriter implements DocumentWriter {

  private static final String DEFAULT_FILESIZE = "512MB";
  private static final int DEFAULT_FILEMAX = 0;

  private String fileSize = DEFAULT_FILESIZE;
  private int fileMax = DEFAULT_FILEMAX;

  private final PatternLayout layout;
  private RollingFileAppender appender;
  private Logger logger;
  private final String recordToFile;
  private WriteNotifier notifier;

  public RotatingFileWriter(final String recordToFile) throws IOException {
    super();
    this.recordToFile = recordToFile;
    notifier = WriteNotifier.NONE;
    layout = new PatternLayout();
    configure();
  }

  public final void setFileSize(final String fileSize) throws IOException {
    this.fileSize = fileSize;
    configure();
  }

  public final void setFileMax(final int fileMax) throws IOException {
    this.fileMax = fileMax;
    configure();
  }

  public final void setNotifier(final WriteNotifier notifier) {
    this.notifier = notifier;
  }

  private void configure() throws IOException {
    appender = new RollingFileAppender(layout, recordToFile, false);
    logger = createLogger();
    appender.setMaxFileSize(fileSize);
    appender.setMaxBackupIndex(fileMax);
    appender.setImmediateFlush(true);
    logger.removeAllAppenders();
    logger.setAdditivity(false);
    logger.setLevel(Level.INFO);
    logger.addAppender(appender);
  }

  Logger createLogger() {
    return Logger.getLogger(recordToFile);
  }

  @Override
  public final void close() throws IOException {
    appender.close();
  }

  @Override
  public final void writeDocument(final String document) {
    notifier.notifyWriteStart(document);
    try {
      logger.info(document);
      notifier.notifyWriteSuccess(document);
    } catch (Exception e) {
      notifier.notifyWriteFailure(document, e);
    }
  }
}
