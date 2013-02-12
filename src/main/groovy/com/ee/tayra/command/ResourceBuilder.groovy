package com.ee.tayra.command

class ResourceBuilder {

  public def build (boolean isDryRun, def binding, String destMongoDB, int port, String username, String password, def criteria, String exceptionFile, PrintWriter console) {
    if(!isDryRun){
      new RegularRestoreFactory(binding, destMongoDB, port, username, password, criteria, exceptionFile, console)
    } else {
      new DryRunFactory(binding, criteria, console)
    }
  }
}
