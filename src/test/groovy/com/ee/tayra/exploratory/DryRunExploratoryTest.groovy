package com.ee.tayra.exploratory

import com.ee.tayra.command.backup.Backup
import com.ee.tayra.command.restore.Restore
import com.mongodb.DB
import com.mongodb.MongoClient

class DryRunExploratoryTest extends ExploratoryResource {

  private def context
  private static StringBuilder result

  def setupSpec() {
    addDataTo(src)
    ExpandoMetaClass.enableGlobally()
    PrintWriter.metaClass.println = { String data ->
      result << data
    }
  }

  def cleanupSpec() {
    deleteDataFrom(src)
  }
  
  private void setup() {
  result = new StringBuilder()
  context = new Binding()
  
  Binding backupContext = new Binding()
  backupContext.setVariable('args', ['-s', srcHOST, "--port=$srcPORT", '-f', backupFile])
  new Backup(backupContext).run()
  }

  private void addDataTo(MongoClient mongoNode) {
    DB DLDb = mongoNode.getDB("DL")
    DB TayraDb = mongoNode.getDB("Tayra")
    DB EELabDb = mongoNode.getDB("EELab")
    run('db.profile.insert({name:"One"})', DLDb)
    run('db.thing.insert({name:"One"})', DLDb)
    run('db.profile.insert({name:"One"})', TayraDb)
    run('db.thing.insert({name:"One"})', TayraDb)
    run('db.profile.insert({name:"One"})', EELabDb)
    run('db.thing.insert({name:"One"})', EELabDb)
  }

  private void deleteDataFrom(MongoClient mongoNode) {
    mongoNode.getDB("DL").dropDatabase()
    mongoNode.getDB("Tayra").dropDatabase()
    mongoNode.getDB("EELab").dropDatabase()
  }

  def ignoresDestinationPortArgsWithDryRunOption () {

    given:'arguments for restore contains -d, --port, -f, --dry-run options'
      context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '--dry-run'])

    when: 'restore runs with above args'
      new Restore(context).run()
    
    then: 'the target Node should not contain any document'
      !tgt.getDatabaseNames().contains("DL") 
      !tgt.getDatabaseNames().contains("Tayra") 
      !tgt.getDatabaseNames().contains("EELab")
  }
  
//  def criteriaIsAppliedWhenSNsAndSExcludeIsGivenWithDryRunOption() {
//    
//    given:'arguments for restore contains -d, --port, -f, --dry-run options'
//    context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '--sExclude', '--sNs=Tayra,EELab.thing', '--dry-run'])
//  
//    when: 'restore runs with above args'
//    new Restore(context).run()
//    
//    then: 'the target Node should not contain any document'
//      println 'result' +result.toString()
//      !result.toString().contains('"ns" : "Tayra.')
//    !result.toString().contains('"ns" : "EELab.thing"')
//  }
}