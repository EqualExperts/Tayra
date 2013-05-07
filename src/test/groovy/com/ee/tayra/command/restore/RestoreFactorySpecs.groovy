 package com.ee.tayra.command.restore

import spock.lang.Specification

import static com.ee.tayra.ConnectionFactory.*
import com.ee.tayra.io.reader.nio.MemoryMappedDocumentReader
import com.ee.tayra.io.reader.FileDocumentReader;
import com.mongodb.MongoClient

class RestoreFactorySpecs extends Specification{

  private RestoreCmdDefaults config
  private MongoClient ignoreMongo
  private PrintWriter ignoreConsole

  def setup() {
    config = new RestoreCmdDefaults()
    config.destination = secureSrcNode
    config.port = secureSrcPort
    config.username = username
    config.password = password
    config.exceptionFile = 'exception.documents'
  }

  def createsAppropriateFactories() {
    given: 'Dry run is required or not'
      config.dryRunRequired = dryRunRequired

    expect: 'correct factory is created'
      RestoreFactory.createFactory(config, ignoreMongo, ignoreConsole).getClass() == klass

    where: 'appropriate factories are created for dry run and non dry run options'
      dryRunRequired |   klass
          true       | DryRunFactory
          false      | DefaultFactory
  }

  def createsAppropriateReaders() {
    given:'a file'
      String fileName = 'test.out'
      config.fBuffer= bufferSize

    and:'factory is created'
      def factory = RestoreFactory.createFactory(config, ignoreMongo, ignoreConsole)

    expect:'readers to be of type'
      factory.createReader(fileName).getClass() == klass

    where:'appropriate reader is created'
      bufferSize |   klass
         '8MB'   | MemoryMappedDocumentReader
          ''     | FileDocumentReader
  }

}