package com.ee.tayra.command

import com.ee.tayra.io.RestoreProgressReporter
import com.ee.tayra.io.SelectiveOplogReplayer
import spock.lang.Specification

class DefaultRestoreFactorySpecs extends Specification {

	private Config config
	private def factory

	def setup() {
		config = new Config()
		config.destMongoDB = 'localhost'
		config.port = 27017
		config.username = 'admin'
		config.password = 'admin'
		config.exceptionFile = 'exception.documents'
		factory =  new DefaultRestoreFactory(config)
	}

	def createsEmptyListener() {
		expect: 'listener created is instance of EmptyProgressReporter'
			factory.createListener() instanceof RestoreProgressReporter
	}

	def createsEmptyReporter() {
		expect: 'reporter created is instance of EmptyProgressReporter'
				factory.createReporter() instanceof RestoreProgressReporter
	}

	def createsWriter() {
		expect: 'writer created is instance of SelectiveOplogReplayer'
			factory.createWriter() instanceof SelectiveOplogReplayer
	}
}
