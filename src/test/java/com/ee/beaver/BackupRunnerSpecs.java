package com.ee.beaver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.UnknownHostException;

import org.junit.Test;

import com.ee.beaver.runner.BackupRunner;
import com.ee.beaver.runner.NotAReplicaSetNode;
import com.mongodb.MongoException;

public class BackupRunnerSpecs {
  private String host = "localhost";
  private int port = 27017;
  
  @Test
  public void connectsToANodeInReplicaSet() throws Exception {
    assertThat(new BackupRunner(host, port), notNullValue());
  }
  
  @Test
  public void doesNotConnectToStandaloneMongoInstance() throws Exception {
  try {
      //When
      new BackupRunner(host, 27020);
      fail("Should Not Connect to a standalone node");
    } catch(NotAReplicaSetNode e) {
    //Then
    assertThat(e.getMessage(), is("localhost is not a part of ReplicaSet"));
    }
  }

  @Test
  public void writesToDestination() throws Exception {
    //Given
    BackupRunner backupRunner = new BackupRunner(host, port);
    Writer writer = new StringWriter();

    //When
    backupRunner.backup(null, writer);
    String expected = "something";
    
    //Then
    assertThat(writer.toString(), is(expected));
  }

  @Test
  public void readsOplog() throws Exception {
  //Given
  BackupRunner backupRunner = new BackupRunner(host, port);
  Writer to = new StringWriter();

  MongoCollection oplog = null;
  //When
  backupRunner.backup(oplog, to);
  String expected = "something";

  //Then
  assertThat(to.toString(), is(expected));
  }
}
