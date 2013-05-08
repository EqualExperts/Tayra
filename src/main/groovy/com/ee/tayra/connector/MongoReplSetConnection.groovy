/*******************************************************************************
 * Copyright (c) 2013, Equal Experts Ltd
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
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
package com.ee.tayra.connector

import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.MongoException
import com.mongodb.ReadPreference
import com.mongodb.ServerAddress

public class MongoReplSetConnection {

  private MongoClient node
  private def nodes = []
  private boolean retryable = true
  private boolean isMaster = false
  private PrintWriter console

  public MongoReplSetConnection(String sourceMongoDB, int port,
      boolean retryable = true, PrintWriter console = new PrintWriter(System.out,true)) {
    ServerAddress server = new ServerAddress(sourceMongoDB, port)
    node = new MongoClient(server)
    isMaster = node.getDB("test").command("ismaster").get("ismaster")
    nodes = getNodesWithinReplicaSet(node)
    this.retryable = retryable
    this.console = console
  }

  private def getNodesWithinReplicaSet(master) {
    // TODO: Replace this API by different call (to investigate with mongo guys)
    String [] hosts = node.getDB("test").command("ismaster").get("hosts")
    console.println "Hosts are: $hosts"
    hosts.collect { getServerAddress(it) }
  }

  private ServerAddress getServerAddress(String host) {
    String[] addressInfo = host.split(":")
    String source = addressInfo[0].split("/")[0]
    int port = Integer.valueOf(addressInfo[1])
    new ServerAddress(source, port)
  }

  def using(Closure runnable, Closure betweenRetry = {}) {
    boolean shouldContinue = true
    while(shouldContinue) {
      try {
        shouldContinue = retryable
        node.with runnable
        shouldContinue = false
      } catch(MongoException.Network problem) {
        if (retryable) {
          console.println "\nNode crashed. Re-establishing Connection"
          betweenRetry.clone().call()
          node = connectToANewNode(nodes)
          nodes = getNodesWithinReplicaSet(node)
        }
      }
    }
  }

  private connectToANewNode(nodes) {
    def options = new MongoClientOptions.Builder()
    if(isMaster) {
      options.readPreference(ReadPreference.primary())
    } else {
      options.readPreference(ReadPreference.secondaryPreferred())
    }
    getNodeAfterElection(nodes, options.build())
  }

  private def getNodeAfterElection(nodes, options) {
    MongoClient mongoClient = new MongoClient(nodes, options)
    while(mongoClient.getReplicaSetStatus().master == null) {
      console.println 'Still waiting for master to be elected...'
      sleep 2 * 1000
    }
    String primaryNode = mongoClient.getReplicaSetStatus().master
    console.println "Elected master: $primaryNode"
    mongoClient
  }
}