package com.ee.beaver.runner;

import java.io.IOException;
import java.io.Writer;
import java.net.UnknownHostException;

import com.ee.beaver.MongoCollection;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;

public class BackupRunner {
  private Mongo mongo;

  public BackupRunner(final String host, final int port)
  throws UnknownHostException {
    ServerAddress server = new ServerAddress(host, port);
    mongo = new Mongo(server);
    DB local = mongo.getDB("local");
    boolean oplogExists = local.collectionExists("oplog.rs");
    if (!oplogExists) {
      close();
      throw new NotAReplicaSetNode("localhost is not a part of ReplicaSet");
    }
    // TO DO: Investigate why this API does not work?
    //ReplicaSetStatus replicaSetStatus = mongo.getReplicaSetStatus();
    //boolean master = replicaSetStatus.isMaster(server);
  }

  public final void backup(final MongoCollection oplog, final Writer writer)
    throws IOException {
    writer.append("something");
  }

  public final void close() {
    mongo.close();
  }
}
