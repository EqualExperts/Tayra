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
package com.ee.tayra.command.backup

import com.ee.tayra.io.CopyListener
import com.ee.tayra.io.DocumentWriter
import com.ee.tayra.io.FileBasedTimestampRepository
import com.ee.tayra.io.MongoExceptionBubbler
import com.ee.tayra.io.Notifier
import com.ee.tayra.io.OplogReader
import com.ee.tayra.io.ProgressReporter
import com.ee.tayra.io.Reporter
import com.ee.tayra.io.RotatingFileWriter
import com.ee.tayra.io.SelectiveOplogReader
import com.ee.tayra.io.TimestampRecorder
import com.ee.tayra.io.criteria.CriteriaBuilder

class BackupFactory {
  private final BackupCmdDefaults config
  private final def listeningReporter
  private final def criteria
  private final String timestampFileName = 'timestamp.out'
  private final TimestampRecorder timestampRecorder

  public BackupFactory (BackupCmdDefaults config, console) {
    this.config = config
    listeningReporter = new ProgressReporter(console)
    def timestampRepository = new FileBasedTimestampRepository(new File(timestampFileName), console)
    timestampRecorder = new TimestampRecorder(timestampRepository)

    if(config.sNs || config.sExclude){
      criteria = new CriteriaBuilder().build {
        if(config.sNs) {
          usingNamespace config.sNs
        }
        if(config.sExclude) {
          usingExclude()
        }
      }
    }
  }

  public def createReader(def oplog) {
    String timestamp = timestampRecorder.lastDocumentTimestamp
    criteria ? new SelectiveOplogReader(new OplogReader(oplog, timestamp, config.isContinuous), criteria)
        : new OplogReader(oplog, timestamp, config.isContinuous)
  }

  public DocumentWriter createDocumentWriter() {
    def writer = new RotatingFileWriter(config.recordToFile)
    writer.fileSize = config.fileSize
    writer.fileMax = config.fileMax
    writer.notifier = new Notifier(timestampRecorder, createListener(), new MongoExceptionBubbler())
    writer
  }

  public def createListener() {
    (CopyListener)listeningReporter
  }

  public def createReporter() {
    (Reporter)listeningReporter
  }

  public def createTimestampRecorder() {
    timestampRecorder
  }
}