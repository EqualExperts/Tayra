package com.ee.tayra.command.backup

import spock.lang.Specification

import com.ee.tayra.domain.MongoCollection
import com.ee.tayra.io.*

class BackupFactorySpecs extends Specification {
  private BackupCmdDefaults config
  private BackupFactory factory

  def setup() {
    config = new BackupCmdDefaults()
    config.recordToFile = 'test.out'
    config.port = 17017
  }

  def createsSelectiveOplogReaderWhenCriteriaIsGiven() {
    given: 'command arguments contains criteria'
      def oplog = Mock(MongoCollection)
      def timestamp = 'ts'
      config.sNs = 'db'

    when: 'factory is created'
      factory = new BackupFactory(config)

    then: 'reader created is instance of SelectiveOplogReader'
      factory.getReader(oplog, timestamp).class == SelectiveOplogReader
  }

  def createsOplogReaderWhenNoCriteriaIsGiven() {
    given: 'command arguments contains no criteria'
      def oplog = Mock(MongoCollection)
      def timestamp = 'ts'
    
    when: 'factory is created'
      factory = new BackupFactory(config)

    then: 'reader created is instance of SelectiveOplogReader'
      factory.getReader(oplog, timestamp).class == OplogReader
  }

  def createsProblemListener() {
    when: 'factory is created'
      factory = new BackupFactory(config)
      
    then: 'reporter created is instance of EmptyProgressReporter'
      factory.getProblemListener() instanceof CopyListener
  }

  def createsWriter() {
    given: 'a Rotating Log Writer'
      def mockWriter = Mock(Writer)
    
    when: 'factory is created'
      factory = new BackupFactory(config)

    then: 'writer created is instance of TimestampRecorder'
      factory.getWriter(mockWriter).class == TimestampRecorder
  }
}
