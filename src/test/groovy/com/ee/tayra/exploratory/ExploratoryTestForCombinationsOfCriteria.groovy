package com.ee.tayra.exploratory

import com.ee.tayra.command.backup.Backup
import com.ee.tayra.command.restore.Restore
import com.mongodb.DB
import com.mongodb.MongoClient

class ExploratoryTestForCombinationsOfCriteria extends ExploratoryResource {

  private def context

  def setupSpec() {
    addDataTo(src)
  }

  def cleanupSpec() {
    deleteDataFrom(src)
  }

  private void setup() {
    context = new Binding()
  }

  private void cleanup() {
    deleteDataFrom(tgt)
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

  def excludesDocumentsbelongingToMultipleNsWithSExcludeWhileBackup() {
    given:'arguments contains -s, -f, -u and -p options'
      context.setVariable('args', ['-s', srcHOST, "--port=$srcPORT", '-f', backupFile, '--sExclude', '--sNs=DL,EELab.thing'])

    when: 'backup runs with above args'
      new Backup(context).run()
      def backupFileContent = new File(backupFile)

    then: 'the backupFile should contain documents having only ns as DL'
      backupFileContent.eachLine { line ->
        !line.contains('"ns" : "DL.')
		!line.contains('"ns" : "EELab.thing"')
      }
  }

//  def excludesDocumentsbelongingToMultipleNsWithSExcludeWhileRestore() {
//    given:'Backup is taken'
//	    Binding backupContext = new Binding()
//	    backupContext.setVariable('args', ['-s', srcHOST, "--port=$srcPORT", '-f', backupFile])
//	    new Backup(backupContext).run()
//
//	and:'arguments for restore contains -d, --port, -f, --sExclude, --sNs options'
//    	context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '--sExclude', '--sNs=DL,EELab.thing'])
//
//    when: 'backup runs with above args'
//    	new Restore(context).run()
//
//    then: 'the target Node should contain documents having only ns as DL'
//	    !tgt.getDatabaseNames().contains("DL")
//	    tgt.getDB("EELab").getCollection("thing").count() == 0
//	    tgt.getDB("EELab").getCollection("profile").count() == 1
//  }
//
//  def noCriteriaIsAppliedWhenNoValueIsGivenforSns() {
//    given:'arguments contains -s, --port, -f, --sNs options'
//    	context.setVariable('args', ['-s', srcHOST, "--port=$srcPORT", '-f', backupFile, '--sNs='])
//
//    when: 'backup runs'
//	    new Backup(context).run()
//	    def backupFileContent = new File(backupFile)
//
//    then: 'the backupFile should contain all documents'
//      backupFileContent.text.contains('"ns" : "DL.')
//      backupFileContent.text.contains('"ns" : "EELab.')
//      backupFileContent.text.contains('"ns" : "Tayra.')
//  }
//
//  def allDocumentsAreRestoredWhenNoValueIsGivenforSUntil() {
//    given:'Backup is taken'
//	    Binding backupContext = new Binding()
//	    backupContext.setVariable('args', ['-s', srcHOST, "--port=$srcPORT", '-f', backupFile, '--sNs='])
//	    new Backup(backupContext).run()
//
//    and:'arguments for restore contains -f, -d, --port, -f, --sUntil options'
//    	context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '--sUntil='])
//
//    when: 'restore runs'
//    	new Restore(context).run()
//
//    then: 'the target Node should contain all documents'
//	    tgt.getDB("DL").getCollection("profile").count == 1
//	    (tgt.getDB("DL").getCollection("thing").count()) == 1
//	    (tgt.getDB("Tayra").getCollection("profile").count()) == 1
//	    (tgt.getDB("Tayra").getCollection("thing").count()) == 1
//	    (tgt.getDB("EELab").getCollection("profile").count()) == 1
//	    (tgt.getDB("EELab").getCollection("thing").count()) == 1
//  }
}