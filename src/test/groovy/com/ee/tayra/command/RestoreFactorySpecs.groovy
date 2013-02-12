package com.ee.tayra.command

import spock.lang.Specification

class RestoreFactorySpecs extends Specification{

	private boolean isDryrunSupplied
	private Config config

	def setup() {
		config = new Config()
		config.destMongoDB = 'localhost'
		config.port = 27017
		config.username = 'admin'
		config.password = 'admin'
		config.exceptionFile = 'exception.documents'
	}

	def createsDefaultRestoreFactoryWhenDryrunOptionIsFalse() {
		given: 'dry-run option is not supplied through command-line'
			isDryrunSupplied = false

		when: 'factory is created'
			def factory = RestoreFactory.create(isDryrunSupplied, config)

		then: 'it is an instance of DefaultRestoreFactory'
			factory instanceof DefaultRestoreFactory
	}


	def createsDryRunFactoryWhenDryrunOptionIsTrue() {
		given: 'dry-run option is supplied through command-line'
			isDryrunSupplied = true

			when: 'factory is created'
				def factory = RestoreFactory.create(isDryrunSupplied, config)

			then: 'it is an instance of DefaultRestoreFactory'
				factory instanceof DryRunFactory
	}

}