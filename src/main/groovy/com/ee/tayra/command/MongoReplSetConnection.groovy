package com.ee.tayra.command

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

  public MongoReplSetConnection(String sourceMongoDB, int port, boolean retryable = true) {
    ServerAddress server = new ServerAddress(sourceMongoDB, port)
    node = new MongoClient(server)
    isMaster = node.getDB("test").command("ismaster").get("ismaster")
    nodes = getNodesWithinReplicaSet(node)
    this.retryable = retryable
  }

  private getNodesWithinReplicaSet(master) {
    // TODO: Replace this API by different call (to investigate with mongo guys)
    String [] hosts = node.getDB("test").command("ismaster").get("hosts")
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
    while(shouldContinue) {
      try {
        shouldContinue = retryable
        clonedRunnable(node)
        shouldContinue = false
        } catch(MongoException.Network problem) {
        if (retryable) {
          println "\nNode crashed. Re-establishing Connection"
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
      println 'Still waiting for master to be elected...'
      sleep 2 * 1000
    }
    String primaryNode = mongoClient.getReplicaSetStatus().master
    println "Elected master node is: $primaryNode"
    mongoClient
  }
}
