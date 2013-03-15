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
import com.ee.tayra.io.OplogReader
import com.ee.tayra.io.ProgressReporter
import com.ee.tayra.io.RotatingFileWriter
import com.ee.tayra.io.SelectiveOplogReader
import com.ee.tayra.io.TimestampRecorder
import com.ee.tayra.io.criteria.CriteriaBuilder
import com.mongodb.MongoException

class BackupFactory {
  private final BackupCmdDefaults config
  private final def listeningReporter
  private final def logWriter
  private final def criteria

  public BackupFactory (config){
    this.config = config
    logWriter = new RotatingFileWriter(config.recordToFile)
    logWriter.fileSize = config.fileSize
    logWriter.fileMax = config.fileMax
    listeningReporter = new ProgressReporter(config.console)
    if(config.sNs){
      criteria = new CriteriaBuilder().build { usingNamespace config.sNs }
    }
  }

  public def getReader(def oplog, def timestamp) {
    criteria ? new SelectiveOplogReader(new OplogReader(oplog, timestamp, config.isContinuous), criteria)
        : new OplogReader(oplog, timestamp, config.isContinuous)
  }

  public def getWriter(Writer writer){
    new TimestampRecorder(writer)
  }

  public def getProblemListener() {
    new CopyListener() {
          void onReadSuccess(String document){
          }
          void onWriteSuccess(String document){
          }
          void onWriteFailure(String document, Throwable problem){
          }
          void onReadFailure(String document, Throwable problem){
            if(problem instanceof MongoException)
              throw problem
          }
        }
  }
}
