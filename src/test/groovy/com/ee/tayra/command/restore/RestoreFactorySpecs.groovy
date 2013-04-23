package com.ee.tayra.command.restore

import spock.lang.Specification

import static com.ee.tayra.ConnectionFactory.*

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
			RestoreFactory.createFactory(config, ignoreMongo, ignoreConsole).class == klass

		where: 'appropriate factories are created for dry run and non dry run options'
			dryRunRequired | klass
				true       | DryRunFactory
				false      | DefaultFactory
	}
	
//	def createsReader() {
//		given: ''
//			def factory = RestoreFactory.createFactory(config, ignoreMongo, ignoreConsole)
//			def mockFile = Mock(File)
//
//		when: 'correct factory is created'
//			def reader = factory.createReader(mockFile)
//
//		then: 'reader created is of type DocumentReader'
//			reader.class == FileDocumentReader
//	}
	
}