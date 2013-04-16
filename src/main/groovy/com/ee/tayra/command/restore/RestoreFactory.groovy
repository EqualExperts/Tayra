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

import com.ee.tayra.io.CopyListener
import com.ee.tayra.io.DocumentReader
import com.ee.tayra.io.Notifier
import com.ee.tayra.io.Replayer
import com.ee.tayra.io.Reporter
import com.ee.tayra.io.criteria.CriteriaBuilder
import com.ee.tayra.io.criteria.Criterion
import com.mongodb.MongoClient

abstract class RestoreFactory {
  
  protected final Criterion criteria
  
  public static RestoreFactory createFactory (RestoreCmdDefaults config, MongoClient mongo, PrintWriter console) {
    config.dryRunRequired ? new DryRunFactory(config, console) : new DefaultFactory(config, mongo, console)
  }
  
  RestoreFactory(RestoreCmdDefaults config) {
    criteria = createCriteria(config)
  }
  
  private Criterion createCriteria(RestoreCmdDefaults config) {
    if(config.sNs || config.sUntil || config.sExclude || config.sSince) {
      new CriteriaBuilder().build {
        if(config.sUntil) {
          usingUntil config.sUntil
        }
        if(config.sSince) {
          usingSince config.sSince
        }
        if(config.sNs) {
          usingNamespace config.sNs
        }
        if(config.sExclude) {
             usingExclude()
        }
      }
    }
  }

  public abstract DocumentReader createReader(File file)

  public abstract Replayer createWriter()

  public abstract CopyListener createListener()
  
  public abstract Notifier createNotifier()

  public abstract Reporter createReporter()

}
