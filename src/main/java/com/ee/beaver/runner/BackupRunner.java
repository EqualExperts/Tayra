package com.ee.beaver.runner;

import java.io.IOException;
import java.io.Writer;

import com.ee.beaver.NotALocalDB;
import com.ee.beaver.OplogDocument;
import com.ee.beaver.OplogReader;
import com.mongodb.DB;

public class BackupRunner {

  public BackupRunner(final DB local) {
  if (!"local".equals(local.getName())) {
  throw new NotALocalDB("Not a local DB");
}

    boolean oplogExists = local.collectionExists("oplog.rs");
    if (!oplogExists) {
      throw new NotAReplicaSetNode("localhost is not a part of ReplicaSet");
    }
    // TO DO: Investigate why this API does not work?
    //ReplicaSetStatus replicaSetStatus = mongo.getReplicaSetStatus();
    //boolean master = replicaSetStatus.isMaster(server);
  }

  public final void copy(final OplogReader fromReader, final Writer toWriter)
    throws IOException {
    while (fromReader.hasDocument()) {
      OplogDocument document = fromReader.readDocument();
      toWriter.append(document.toJson());
    }
  }
}
