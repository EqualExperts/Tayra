package com.ee.beaver.io;

import java.io.IOException;
import java.io.Writer;

import com.ee.beaver.io.OplogReader;
import com.ee.beaver.domain.NotALocalDB;
import com.mongodb.DB;

public class Copier {

  private static final CharSequence NEW_LINE =
    System.getProperty("line.separator");

  public Copier(final DB local) {
    if (!"local".equals(local.getName())) {
      throw new NotALocalDB("Not a local DB");
    }

    boolean oplogExists = local.collectionExists("oplog.rs");
    if (!oplogExists) {
      throw new NotAReplicaSetNode("localhost is not a part of ReplicaSet");
    }
  }

  public final void copy(final OplogReader fromReader, final Writer toWriter)
    throws IOException {
    while (fromReader.hasDocument()) {
      String document = fromReader.readDocument();
      toWriter.append(document);
      toWriter.append(NEW_LINE);
    }
  }
}
