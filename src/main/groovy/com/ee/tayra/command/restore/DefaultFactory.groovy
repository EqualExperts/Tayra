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
package com.ee.tayra.command.restore

import com.ee.tayra.domain.operation.Operations
import com.ee.tayra.io.CopyListener
import com.ee.tayra.io.DocumentReader
import com.ee.tayra.io.FileDocumentReader
import com.ee.tayra.io.Notifier
import com.ee.tayra.io.OplogReplayer
import com.ee.tayra.io.Replayer
import com.ee.tayra.io.Reporter
import com.ee.tayra.io.RestoreProgressReporter
import com.ee.tayra.io.SelectiveOplogReplayer
import com.mongodb.MongoClient

class DefaultFactory extends RestoreFactory {

  private final MongoClient mongo
  private final def listeningReporter

  public DefaultFactory(RestoreCmdDefaults config, MongoClient mongo, PrintWriter console) {
    super(config)
    this.mongo = mongo
    listeningReporter = new RestoreProgressReporter(new FileWriter
        (config.exceptionFile), new FileWriter
        (config.exceptionDetailsFile), console)
  }

  @Override
  public Replayer createWriter() {
    OplogReplayer oplogReplayer = new OplogReplayer(new Operations(mongo))
	oplogReplayer.notifier = createNotifier()
    criteria ? new SelectiveOplogReplayer(criteria, oplogReplayer) :
        oplogReplayer
  }

  @Override
  public CopyListener createListener() {
    (CopyListener)listeningReporter
  }

  @Override
  public Reporter createReporter() {
    (Reporter)listeningReporter
  }

  @Override
  public DocumentReader createReader(final String fileName){
    File file = new File(fileName)
    FileDocumentReader reader = new FileDocumentReader(new BufferedReader(new FileReader(file)))
	reader.notifier = createNotifier()
	reader
  }

//  @Override
//  public Notifier createNotifier() {
//    return new Notifier(createListener());
//  }

  private Notifier createNotifier() {
    return new Notifier(createListener());
  }
}