package com.ee.tayra.command.restore

import spock.lang.Specification

import com.ee.tayra.parameters.EnvironmentProperties
import com.mongodb.MongoClient

class RestoreFactorySpecs extends Specification{

	private RestoreCmdDefaults config
	private MongoClient ignoreMongo
	private PrintWriter ignoreConsole

	def setup() {
		config = new RestoreCmdDefaults()
		config.destination = EnvironmentProperties.secureSrcNode
		config.port = EnvironmentProperties.secureSrcPort
		config.username = EnvironmentProperties.username
		config.password = EnvironmentProperties.password
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
	
}