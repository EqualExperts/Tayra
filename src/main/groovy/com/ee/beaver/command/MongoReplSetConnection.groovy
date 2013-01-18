package com.ee.beaver.command

import com.mongodb.MongoClient
import com.mongodb.MongoException
import com.mongodb.ServerAddress

public class MongoReplSetConnection {

  private MongoClient master
  private def nodes = []
  private boolean retryable = true

  public MongoReplSetConnection(String sourceMongoDB, int port, boolean retryable = true) {
    ServerAddress server = new ServerAddress(sourceMongoDB, port)
    master = new MongoClient(server)
    nodes = getNodesWithinReplicaSet(master)
    this.retryable = retryable
  }

  private getNodesWithinReplicaSet(master) {
    // TODO: Replace this API by different call (to investigate with mongo guys)
    String [] hosts = master.getDB("test").command("ismaster").get("hosts")
    println "Hosts are: $hosts"
    hosts.collect {
      getServerAddress(it)
    }
  }

  private ServerAddress getServerAddress(String host) {
    String[] addressInfo = host.split(":")
    String source = addressInfo[0].split("/")[0]
    int port = Integer.valueOf(addressInfo[1])
    new ServerAddress(source, port)
  }

  def using(Closure runnable, Closure betweenRetry = {}) {
    def clonedRunnable = runnable.clone()
    boolean shouldContinue = true
    def mongoClient
    while(shouldContinue) {
      try {
        shouldContinue = retryable
        clonedRunnable(master)
        shouldContinue = false
      } catch(MongoException.Network problem) {
        if (retryable) {
          println "Primary crashed. Re-establishing Connection"
          betweenRetry.clone().call()
          mongoClient = waitUntilElectionCompletes(nodes)
          master = connectToNewMaster(mongoClient)
          nodes = getNodesWithinReplicaSet(master)
        }
      }
    }
  }

  private connectToNewMaster(mongoClient) {
    String primaryNode = mongoClient.getReplicaSetStatus().master
    println("The new master is: $primaryNode")
    ServerAddress serverAddress = getServerAddress(primaryNode)
    new MongoClient(serverAddress)
  }

  private def waitUntilElectionCompletes(nodes) {
    println "Re-electing master..."
    MongoClient mongoClient = new MongoClient(nodes)
    while(mongoClient.getReplicaSetStatus().master == null);
    mongoClient
  }
}
