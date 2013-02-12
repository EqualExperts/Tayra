package com.ee.tayra.command

import com.ee.tayra.domain.operation.Operations
import com.ee.tayra.io.OplogReplayer
import com.ee.tayra.io.RestoreProgressReporter
import com.ee.tayra.io.SelectiveOplogReplayer
import com.mongodb.Mongo
import com.mongodb.ServerAddress
import java.io.PrintWriter;

class RegularRestoreFactory {

  private final def binding;
  private final PrintWriter console;
  private final def criteria;
  private final Mongo mongo;
  private final listeningReporter

  public RegularRestoreFactory(def binding, String destMongoDB, int port,
   String username, String password, def criteria, String exceptionFile,
   PrintWriter console) {
    this.binding = binding;
    this.criteria = criteria;
    this.console = console;
    ServerAddress server = new ServerAddress(destMongoDB, port);
    this.mongo = new Mongo(server)
    getAuthenticator(mongo).authenticate(username, password)
    this.listeningReporter = new RestoreProgressReporter(new FileWriter
      (exceptionFile), console)
  }

  def getWriter() {
    binding.hasVariable('writer') ? binding.getVariable('writer')
        : new SelectiveOplogReplayer(criteria, new OplogReplayer(new Operations(mongo)))
  }

  def getListener() {
    binding.hasVariable('listener') ? binding.getVariable('listener')
        : listeningReporter
  }

  def getReporter() {
    binding.hasVariable('reporter') ? binding.getVariable('reporter')
        : listeningReporter
  }

  def getAuthenticator(mongo) {
    binding.hasVariable('authenticator') ?
        binding.getVariable('authenticator') : new MongoAuthenticator(mongo)
  }
}
