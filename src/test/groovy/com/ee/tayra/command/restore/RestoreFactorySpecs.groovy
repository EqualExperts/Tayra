package com.ee.tayra.command.restore

import com.ee.tayra.command.restore.DefaultFactory;
import com.ee.tayra.command.restore.DryRunFactory;
import com.ee.tayra.command.restore.RestoreCmdDefaults;
import com.ee.tayra.command.restore.RestoreFactory;

import spock.lang.Specification

class RestoreFactorySpecs extends Specification{

	private boolean isDryrunSupplied
	private RestoreCmdDefaults config

	def setup() {
		config = new RestoreCmdDefaults()
		config.mongo = 'localhost'
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
			factory.class == DefaultFactory
	}


	def createsDryRunFactoryWhenDryrunOptionIsTrue() {
		given: 'dry-run option is supplied through command-line'
			isDryrunSupplied = true

			when: 'factory is created'
				def factory = RestoreFactory.create(isDryrunSupplied, config)

			then: 'it is an instance of DefaultRestoreFactory'
				factory.class == DryRunFactory
	}

}