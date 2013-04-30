package com.ee.tayra.exploratory

import com.ee.tayra.command.restore.Restore

class MemoryMappedRestoreExploratoryTest extends RequiresExploratoryTestSupport {

  private def context
  static StringBuilder result

  def setupSpec() {
    addExtraNestedDataTo(src);
    takeBackup()
  }

  def setup() {
    result = new StringBuilder()
    context = new Binding()
  }

  def cleanup() {
    deleteDataFrom(tgt)
    deleteExtraDataFrom(src)
    deleteExtraDataFrom(tgt)
  }

  def restoresOnStandaloneWhenBufferSizeIsGiven() {
    given: 'arguments for restore contains -d, --port, -f, --fBuffer options'
      context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '--fBuffer=1kb'])

    when: 'restore runs with above args'
      new Restore(context).run()

    then: 'the target Node should contain all documents'
      assertTargetNodeContainsExtraDocuments()
      assertTargetNodeContainsAllDocuments()
  }

  def restoresOnStandaloneWhenBufferSizeIsGivenWithCriteria() {
    given: 'arguments for restore contains -d, --port, -f, --fBuffer options'
      context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '--fBuffer=1kb', '--sNs=People'])

    when: 'restore runs with above args'
      new Restore(context).run()

    then: 'the target Node should contain all documents'
      assertTargetNodeContainsExtraDocuments()
      ! assertTargetNodeContainsAllDocuments()
  }

  def restoresOnStandaloneWhenBufferSizeIsGivenWithExcludingCriteria() {
    given: 'arguments for restore contains -d, --port, -f, --fBuffer options'
    context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '--fBuffer=1kb', '--sNs=People', '--sExclude'])
  
    when: 'restore runs with above args'
    new Restore(context).run()
  
    then: 'the target Node should contain all documents'
      ! assertTargetNodeContainsExtraDocuments()
      assertTargetNodeContainsAllDocuments()
  }

  private assertTargetNodeContainsExtraDocuments() {
    tgt.getDB("People").getCollection("Information").count == 10
  }

  


}
