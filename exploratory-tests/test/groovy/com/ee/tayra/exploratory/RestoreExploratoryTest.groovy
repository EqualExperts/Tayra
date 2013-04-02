package com.ee.tayra.exploratory

import com.ee.tayra.command.restore.Restore

class RestoreExploratoryTest extends ExploratoryTestSupport {

  private def context
  private static StringBuilder result

  def setupSpec() {
	takeBackup()

    ExpandoMetaClass.enableGlobally()
    PrintWriter.metaClass.println = { String data ->
      result << data
    }
  }

  def setup() {
    result = new StringBuilder()
    context = new Binding()
  }

  private void cleanup() {
    deleteDataFrom(tgt)
  }

  def restoresOnNonAuthenticatedStandaloneWhenCredentialsAreGiven() {
    given: 'arguments for restore contains -d, --port, -f, --sExclude, --sNs options'
      context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '-u', USERNAME, '-p', PASSWORD])
  
    when: 'restore runs with above args'
      new Restore(context).run()
  
    then: 'the target Node should contain all documents'
      assertTargetNodeContainsAllDocuments()
  }

  def ignoresDestinationPortArgsWithDryRunOption () {
    given: 'arguments for restore contains -d, --port, -f, --dry-run options'
      context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '--dry-run'])

    when: 'restore runs with above args'
      new Restore(context).run()
    
    then: 'the target Node should not contain any document'
      !assertTargetNodeContainsAllDocuments()
  }

  def criteriaIsAppliedWhenSNsAndSExcludeIsGivenWithDryRunOption() {
    given: 'arguments for restore contains -d, --port, -f, --dry-run options'
      context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '--sExclude', '--sNs=Tayra,EELab.thing', '--dry-run'])

    when: 'restore runs with above args'
      new Restore(context).run()

    then: 'the target Node should not contain any document'
//      !result.toString().contains('"ns" : "Tayra.')
//      !result.toString().contains('"ns" : "EELab.thing"')
  }

  def excludesDocumentsbelongingToMultipleNsWithSExcludeWhileRestore() {
	  given: 'arguments for restore contains -d, --port, -f, --sExclude, --sNs options'
		context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '--sExclude', '--sNs=DL,EELab.thing'])
  
	  when: 'backup runs with above args'
		new Restore(context).run()
  
	  then: 'the target Node should not contain documents having ns as DL and EELab.thing'
		!tgt.getDatabaseNames().contains("DL")
		tgt.getDB("EELab").getCollection("thing").count() == 0
	}
  
	def noCriteriaIsAppliedWhenNoValueIsGivenforSns() {
	  given:'arguments contains -s, --port, -f, --sNs options'
		context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '--sNs='])
  
	  when: 'Restore runs'
		new Restore(context).run()
  
	  then: 'the target node should contain all documents'
	    assertTargetNodeContainsAllDocuments()
	}
  
	def allDocumentsAreRestoredWhenNoValueIsGivenforSUntil() {
	  given: 'arguments for restore contains -f, -d, --port, -f, --sUntil options'
		context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '--sUntil='])
  
	  when: 'restore runs'
		new Restore(context).run()
  
	  then: 'the target Node should contain all documents'
		assertTargetNodeContainsAllDocuments()
	}

  private assertTargetNodeContainsAllDocuments() {
	  tgt.getDB("DL").getCollection("profile").count == 1
	  (tgt.getDB("DL").getCollection("thing").count()) == 1
	  (tgt.getDB("Tayra").getCollection("profile").count()) == 1
	  (tgt.getDB("Tayra").getCollection("thing").count()) == 1
	  (tgt.getDB("EELab").getCollection("profile").count()) == 1
	  (tgt.getDB("EELab").getCollection("thing").count()) == 1
  }
}

//  def allBackupFilesAreReadWhenFAlleIsGivenWithDryRunOption() {
//	  given:'Backup is taken'
////	    addDataTo(src)
//	    Binding backupContext = new Binding()
//        backupContext.setVariable('args', ['-s', srcHOST, "--port=$srcPORT", '-f', backupFile, '--fSize=1KB', '--fMax=3'])
//        new Backup(backupContext).run()
//  
//	  and:'arguments for restore contains -d, --port, -f, --dry-run options'
//		context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '--fAll', '--dry-run'])
//  
//	  when: 'restore runs with above args'
//		new Restore(context).run()
//  
//	  then: 'the target Node should not contain any document'
////		!result.toString().contains('"ns" : "Tayra.')
////		!result.toString().contains('"ns" : "EELab.thing"')
//	}