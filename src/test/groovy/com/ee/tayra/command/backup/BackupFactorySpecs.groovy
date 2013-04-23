package com.ee.tayra.command.backup

import spock.lang.Specification

import com.ee.tayra.domain.MongoCollection
import com.ee.tayra.io.*
import com.ee.tayra.io.listener.timestamp.TimestampRecorder;
import com.ee.tayra.io.reader.OplogReader;
import com.ee.tayra.io.reader.SelectiveOplogReader;
import com.ee.tayra.io.writer.RotatingFileWriter;

import static com.ee.tayra.ConnectionFactory.*

class BackupFactorySpecs extends Specification {
  private BackupCmdDefaults config
  private BackupFactory factory
  private def console

  def setup() {
    config = new BackupCmdDefaults()
    config.recordToFile = 'test.out'
    config.port = unsecureSrcPort
    console = Mock(PrintWriter)
  }

  def createsSelectiveOplogReaderWhenCriteriaIsGiven() {
    given: 'command arguments contains criteria'
      def oplog = Mock(MongoCollection)
      config.sNs = 'db'

    when: 'factory is created'
      factory = new BackupFactory(config, console)

    then: 'reader created is instance of SelectiveOplogReader'
      factory.createReader(oplog).class == SelectiveOplogReader
  }

  def createsOplogReaderWhenNoCriteriaIsGiven() {
    given: 'command arguments contains no criteria'
      def oplog = Mock(MongoCollection)

    when: 'factory is created'
      factory = new BackupFactory(config, console)

    then: 'reader created is instance of SelectiveOplogReader'
      factory.createReader(oplog).class == OplogReader
  }

  def createsWriter() {
    when: 'factory is created'
      factory = new BackupFactory(config, console)

    then: 'writer created is instance of RotatingFileWriter'
      factory.createDocumentWriter().class == RotatingFileWriter
  }

  def createsTimestampRecorder() {
    when: 'factory is created'
      factory = new BackupFactory(config, console)

    then: 'writer created is instance of TimestampRecorder'
      factory.createTimestampRecorder().class == TimestampRecorder
  }
}
